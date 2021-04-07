
function Mount-bsWebApp {
    [CmdletBinding()]
    [OutputType([Microsoft.Azure.Commands.WebApps.Models.PSSite])]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [hashtable]
        $WAConfig,

        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.ResourceManager.Cmdlets.SdkModels.PSResourceGroup]
        $ResourceGroup,
        
        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.WebApps.Models.WebApp.PSAppServicePlan]
        $AppServicePlan
    )

    New-GooLogMessage 'WebApp Management' -Step | Write-GooLog

    $waName = "$($ResourceGroup.ResourceGroupName)$($WAConfig.Suffix)"
    $wa = Get-AzWebApp -ResourceGroupName $ResourceGroup.ResourceGroupName -Name $waName -ErrorAction SilentlyContinue
    if (-not $wa) {
        'az' | Invoke-GooNativeCommand -CommandArgs @(
            'webapp',
            'create',
            '--resource-group', $ResourceGroup.ResourceGroupName,
            '--plan', $AppServicePlan.Name
            '--name', $waName,
            '--multicontainer-config-type', 'COMPOSE',
            '--multicontainer-config-file', ("$PSScriptRoot$($WAConfig.DockerCompose.Path)" | Resolve-Path).Path
        ) | Out-Null
        $wa = Get-AzWebApp -ResourceGroupName $ResourceGroup.ResourceGroupName -Name $waName

        $wa.Name | Write-GooLog -Level CREATE -ForegroundColor Green
    }

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

    'Configuring app settings & connection strings...' | Write-GooLog
    $appSettings = $WAConfig.AppSettings | ForEach-Object {
        @{ $_.Name = $_.Value }
    }

    $connectionStrings = $WAConfig.ConnectionStrings | ForEach-Object {
        @{ $_.Name = $_.Value }
    }

    $wa = Set-AzWebApp `
        -ResourceGroupName $ResourceGroup.ResourceGroupName `
        -Name $wa.Name `
        -AppSettings $appSettings `
        -ConnectionStrings $connectionStrings

    $wa.Name | Write-GooLog -Level UPDATE -ForegroundColor Yellow
    $wa.Name | Write-GooLog -Level MOUNT
    New-GooLogMessage -Separator | Write-GooLog
}
