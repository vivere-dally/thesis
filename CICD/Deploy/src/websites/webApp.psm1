
function Mount-bsWebApp {
    [CmdletBinding()]
    [OutputType([Microsoft.Azure.Commands.WebApps.Models.PSSite])]
    [Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSAvoidUsingPlainTextForPassword', '')]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [hashtable]
        $WAConfig,

        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.ResourceManager.Cmdlets.SdkModels.PSResourceGroup]
        $ResourceGroup,
        
        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.WebApps.Models.WebApp.PSAppServicePlan]
        $AppServicePlan,

        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.Management.Storage.Models.PSStorageAccount]
        $StorageAccount,

        [Parameter(Mandatory = $true)]
        [string]
        $ACRUsername,
    
        [Parameter(Mandatory = $true)]
        [string]
        $ACRPassword,

        [Parameter(Mandatory = $true)]
        [string]
        $BranchName,
    
        [Parameter(Mandatory = $true)]
        [string]
        $Tag,
    
        [Parameter(Mandatory = $true)]
        [string]
        $MySqlRootPassword,
    
        [Parameter(Mandatory = $true)]
        [string]
        $MySqlUsername,
    
        [Parameter(Mandatory = $true)]
        [string]
        $MySqlPassword
    )

    process {
        New-GooLogMessage 'WebApp Management' -Step | Write-GooLog

        'Initializing App Settings, Connection Strings and Docker-Compose Placeholders' | Write-GooLog
        @($WAConfig.AppSettings, $WAConfig.ConnectionStrings, $WAConfig.DockerCompose.Placeholders) | ForEach-Object {
            $_ | ForEach-Object {
                if (-not $_.Value) {
                    $_.Value = $_.Expression | Invoke-Expression
                }
            }
        }

        'Replacing placeholders in the docker-compose file' | Write-GooLog
        $dockerComposePath = ("$PSScriptRoot$($WAConfig.DockerCompose.Path)" | Resolve-Path).Path
        $content = (Get-Content -Path $dockerComposePath) -join [System.Environment]::NewLine
        $WAConfig.DockerCompose.Placeholders | ForEach-Object {
            $content = $content.Replace($_.Name, $_.Value)
        }

        $content | Set-Content -Path $dockerComposePath -Force | Out-Null

        $waName = "$($ResourceGroup.ResourceGroupName)$($WAConfig.Suffix)"
        $wa = Get-AzWebApp -ResourceGroupName $ResourceGroup.ResourceGroupName -Name $waName -ErrorAction SilentlyContinue
        if (-not $wa) {
            $wa = New-AzWebApp `
                -Location $ResourceGroup.Location `
                -ResourceGroupName $ResourceGroup.ResourceGroupName `
                -Name $waName `
                -AppServicePlan $AppServicePlan.Name

            $wa.Name | Write-GooLog -Level CREATE -ForegroundColor Green
        }

        "Fetched $($wa.Name). Verifying properties..." | Write-GooLog

        # Update after creation as well
        $params = @{}
        if ($WAConfig.Property.AlwaysOn -ne $wa.SiteConfig.AlwaysOn) {
            $params['AlwaysOn'] = $WAConfig.Property.AlwaysOn
        }

        if ($WAConfig.Property.HttpsOnly -ne $wa.HttpsOnly) {
            $params['HttpsOnly'] = $WAConfig.Property.HttpsOnly
        }

        if ($WAConfig.Property.MinTlsVersion -ne $wa.SiteConfig.MinTlsVersion) {
            $params['MinTlsVersion'] = $WAConfig.Property.MinTlsVersion
        }

        if ($WAConfig.Property.FtpsState -ne $wa.SiteConfig.FtpsState) {
            $params['FtpsState'] = $WAConfig.Property.FtpsState
        }

        if ($WAConfig.Property.Use32BitWorkerProcess -ne $wa.SiteConfig.Use32BitWorkerProcess) {
            $params['Use32BitWorkerProcess'] = $WAConfig.Property.Use32BitWorkerProcess
        }

        if ($WAConfig.Property.Http20Enabled -ne $wa.SiteConfig.Http20Enabled) {
            $params['Http20Enabled'] = $WAConfig.Property.Http20Enabled
        }

        if ($WAConfig.Property.WebSocketsEnabled -ne $wa.SiteConfig.WebSocketsEnabled) {
            $params['WebSocketsEnabled'] = $WAConfig.Property.WebSocketsEnabled
        }

        if (0 -lt $params.Keys.Count) {
            $params += @{
                ResourceGroupName = $ResourceGroup.ResourceGroupName;
                Name              = $wa.Name;
            }

            $updatedWa = Set-AzWebApp @params

            $updatedWa.Name | Write-GooLog -Level UPDATE -ForegroundColor Yellow
            Format-bsAzResourceUpdate $wa $updatedWa | Write-GooLog -Level UPDATE -ForegroundColor Yellow

            $wa = $updatedWa
        }

        'Configuring app settings...' | Write-GooLog
        $appSettings = @{}
        $WAConfig.AppSettings | ForEach-Object {
            $appSettings[$_.Name] = $_.Value
        }

        'Configuring connection strings...' | Write-GooLog
        $connectionStrings = @{} 
        $WAConfig.ConnectionStrings | ForEach-Object {
            $connectionStrings[$_.Name] = $_.Value
        }

        'Configuring storage account file share...' | Write-GooLog
        $azureStoragePath = @()
        $accessKey = Get-AzStorageAccountKey -ResourceGroupName $ResourceGroup.ResourceGroupName -Name $StorageAccount.StorageAccountName `
        | Where-Object { 'Full' -eq $_.Permissions } `
        | Select-Object -ExpandProperty Value -First 1
        $WAConfig.AzureStoragePath | ForEach-Object {
            $azureStoragePath += New-AzWebAppAzureStoragePath `
                -Name $_.Name `
                -AccountName $StorageAccount.StorageAccountName `
                -Type AzureFiles `
                -ShareName $_.ShareName `
                -AccessKey $accessKey `
                -MountPath $_.MountPath
        }

        $params = @{
            ResourceGroupName = $ResourceGroup.ResourceGroupName;
            Name              = $wa.Name;
        }

        if (0 -lt $appSettings.Count) {
            $params['AppSettings'] = $appSettings
        }

        if (0 -lt $connectionStrings.Count) {
            $params['ConnectionStrings'] = $connectionStrings
        }

        if (0 -lt $azureStoragePath.Length) {
            $params['AzureStoragePath'] = $azureStoragePath
        }

        $wa = Set-AzWebApp @params

        "$($wa.Name) app settings, connection strings & storage account file share" | Write-GooLog -Level UPDATE -ForegroundColor Yellow

        "Restarting to apply the latest changes" | Write-GooLog
        $wa | Restart-AzWebApp | Out-Null

        'az' | Invoke-GooNativeCommand -CommandArgs @(
            'webapp',
            'config',
            'container',
            'set',
            '--resource-group', $ResourceGroup.ResourceGroupName,
            '--name', $wa.Name,
            '--multicontainer-config-type', 'COMPOSE',
            '--multicontainer-config-file', ("$PSScriptRoot$($WAConfig.DockerCompose.Path)" | Resolve-Path).Path
        ) | Out-Null
   
        "$($wa.Name) image tag to $Tag" | Write-GooLog -Level UPDATE -ForegroundColor Yellow

        $wa.Name | Write-GooLog -Level MOUNT
        New-GooLogMessage -Separator | Write-GooLog

        return $wa
    }
}
