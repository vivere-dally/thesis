
function Mount-bsMySqlServer {
    [CmdletBinding()]
    [OutputType([Microsoft.Azure.PowerShell.Cmdlets.MySql.Models.Api20171201.IServer])]
    [Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSAvoidUsingPlainTextForPassword', '')]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [hashtable]
        $MSConfig,

        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.ResourceManager.Cmdlets.SdkModels.PSResourceGroup]
        $ResourceGroup,

        [Parameter(Mandatory = $true)]
        [string]
        $MySqlUsername,

        [Parameter(Mandatory = $true)]
        [string]
        $MySqlPassword
    )

    New-GooLogMessage 'MySqlServer Management' -Step | Write-GooLog

    $MySqlSecurePassword = ($MySqlPassword | ConvertTo-SecureString -AsPlainText -Force)
    $__config = @{
        GeoRedundantBackup = $MSConfig.Property.GeoRedundantBackup ? `
            [Microsoft.Azure.PowerShell.Cmdlets.MySql.Support.GeoRedundantBackup]::Enabled : `
            [Microsoft.Azure.PowerShell.Cmdlets.MySql.Support.GeoRedundantBackup]::Disabled;
        MinimalTlsVersion  = [Microsoft.Azure.PowerShell.Cmdlets.MySql.Support.MinimalTlsVersionEnum]::($MSConfig.Property.MinimalTlsVersion -replace '_', '');
        Version            = [Microsoft.Azure.PowerShell.Cmdlets.MySql.Support.ServerVersion]::Eight0;
    }

    $mssName = "$($ResourceGroup.ResourceGroupName)$($MSConfig.Suffix)"
    $mss = Get-AzMySqlServer -ResourceGroupName $ResourceGroup.ResourceGroupName -Name $mssName -ErrorAction SilentlyContinue
    if (-not $mss) {
        $params = @{
            Location                   = $ResourceGroup.Location;
            ResourceGroupName          = $ResourceGroup.ResourceGroupName;
            Name                       = $mssName;
            AdministratorUserName      = $MySqlUsername;
            AdministratorLoginPassword = $MySqlSecurePassword;
            Sku                        = $MSConfig.Property.Sku;
            BackupRetentionDay         = $MSConfig.Property.BackupRetentionDay;
            StorageInMb                = $MSConfig.Property.StorageInMb;
        } + $__config

        $mss = New-AzMySqlServer @params

        $mss.Name | Write-GooLog -Level CREATE -ForegroundColor Green
    }
    else {
        "Fetched $($mss.Name). Verifying properties..." | Write-GooLog

        $params = @{}
        if ($MSConfig.Property.Sku -ne $mss.SkuName) {
            $params['Sku'] = $MSConfig.Property.Sku
        }

        if ($MSConfig.Property.BackupRetentionDay -ne $mss.StorageProfileBackupRetentionDay) {
            $params['BackupRetentionDay'] = $MSConfig.Property.BackupRetentionDay
        }

        if ($MSConfig.Property.StorageInMb -ne $mss.StorageProfileStorageMb) {
            $params['StorageInMb'] = $MSConfig.Property.StorageInMb
        }

        if ($__config.GeoRedundantBackup -ne $mss.StorageProfileGeoRedundantBackup) {
            $params['GeoRedundantBackup'] = $__config.GeoRedundantBackup
        }

        if ($__config.MinimalTlsVersion -ne $mss.MinimalTlsVersion) {
            $params['MinimalTlsVersion'] = $__config.MinimalTlsVersion
        }

        if ($__config.Version -ne $mss.Version) {
            $params['Version'] = $__config.Version
        }

        if (0 -lt $params.Keys.Count) {
            $params += @{
                ResourceGroupName          = $ResourceGroup.ResourceGroupName;
                Name                       = $mss.Name;
                AdministratorLoginPassword = $MySqlSecurePassword;
            }

            $updatedMss = Update-AzMySqlServer @params

            $updatedMss.Name | Write-GooLog -Level UPDATE -ForegroundColor Yellow
            Format-bsAzResourceUpdate $mss $updatedMss | Write-GooLog -Level UPDATE -ForegroundColor Yellow

            $mss = $updatedMss
        }
    }

    if ($MSConfig.Property.AllowAccessToAzureServices) {
        Update-AzMySqlFirewallRule `
            -ResourceGroupName $ResourceGroup.ResourceGroupName `
            -ServerName $mss.Name `
            -Name 'AllowAccessToAzureServices' `
            -StartIPAddress '0.0.0.0' `
            -EndIPAddress '0.0.0.0'
    }

    $mss.Name | Write-GooLog -Level MOUNT
    New-GooLogMessage -Separator | Write-GooLog

    return $mss
}
