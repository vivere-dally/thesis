
function Mount-bsStorageAccount {
    [CmdletBinding()]
    [OutputType([Microsoft.Azure.Commands.Management.Storage.Models.PSStorageAccount])]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [hashtable]
        $SAConfig,

        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.ResourceManager.Cmdlets.SdkModels.PSResourceGroup]
        $ResourceGroup
    )

    New-GooLogMessage 'StorageAccount Management' -Step | Write-GooLog

    $saName = "$($ResourceGroup.ResourceGroupName)$($SAConfig.Suffix)"
    $sa = Get-AzStorageAccount -ResourceGroupName $ResourceGroup.ResourceGroupName -Name $saName -ErrorAction SilentlyContinue
    if (-not $sa) {
        $params = @{
            Location          = $ResourceGroup.Location;
            ResourceGroupName = $ResourceGroup.ResourceGroupName;
            Name              = $saName;
        } + $SAConfig.Property;
        $sa = New-AzStorageAccount @params

        $sa.StorageAccountName | Write-GooLog -Level CREATE -ForegroundColor Green
    }
    else {
        "Fetched $($sa.StorageAccountName). Verifying properties..." | Write-GooLog
        
        $params = @{}
        if ($SAConfig.Property.SkuName -ne $sa.Sku.Name) {
            $params['SkuName'] = $SAConfig.Property.SkuName
        }

        if ($SAConfig.Property.AccessTier -ne $sa.AccessTier) {
            $params['AccessTier'] = $SAConfig.Property.AccessTier
        }

        if ($SAConfig.Property.MinimumTlsVersion -ne $sa.MinimumTlsVersion) {
            $params['MinimumTlsVersion'] = $SAConfig.Property.MinimumTlsVersion
        }

        if ($SAConfig.Property.EnableHttpsTrafficOnly -ne $sa.EnableHttpsTrafficOnly) {
            $params['EnableHttpsTrafficOnly'] = $SAConfig.Property.EnableHttpsTrafficOnly
        }

        if (0 -lt $params.Keys.Count) {
            $params += @{
                ResourceGroupName = $ResourceGroup.ResourceGroupName;
                Name              = $sa.Name; 
            }

            $updatedSa = Set-AzStorageAccount @params

            $updatedSa.StorageAccountName | Write-GooLog -Level UPDATE -ForegroundColor Yellow
            Format-bsAzResourceUpdate $sa $updatedSa | Write-GooLog -Level UPDATE -ForegroundColor Yellow

            $sa = $updatedSa
        }
    }

    $SAConfig.Shares | Mount-bsStorageAccountShare -StorageAccount $sa | Out-Null
    "$($SAConfig.Shares.Length) shares" | Write-GooLog -Level MOUNT

    $sa.StorageAccountName | Write-GooLog -Level MOUNT
    New-GooLogMessage -Separator | Write-GooLog

    return $sa
}

function Mount-bsStorageAccountShare {
    [CmdletBinding()]
    [OutputType([Microsoft.WindowsAzure.Commands.Common.Storage.ResourceModel.AzureStorageFileShare])]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [hashtable]
        $ShareConfig,

        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.Management.Storage.Models.PSStorageAccount]
        $StorageAccount
    )

    process {
        New-GooLogMessage 'StorageAccount File Share Management' -Step | Write-GooLog
        
        $share = $StorageAccount.Context | Get-AzStorageShare -Name $ShareConfig.Name -ErrorAction SilentlyContinue
        if (-not $share) {
            $share = $StorageAccount.Context | New-AzStorageShare -Name $ShareConfig.Name

            $share.Name | Write-GooLog -Level CREATE -ForegroundColor Green
        }
        else {
            "Fetched $($share.Name). Verifying properties..." | Write-GooLog

            # Nothing to verify at the moment. Using default settings
        }

        $share.Name | Write-GooLog -Level MOUNT
        New-GooLogMessage -Separator -Length 10 | Write-GooLog

        return $share
    }

}
