
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
    
    begin {
        New-GooLogMessage 'WebApp Management' -Step | Write-GooLog
    }
    
    process {
        $webAppName = "$($ResourceGroup.ResourceGroupName)$($WAConfig.Suffix)"
        $webApp = Get-AzWebApp -ResourceGroupName $ResourceGroup.ResourceGroupName -Name $webAppName -ErrorAction SilentlyContinue
        if (-not $webApp) {
            'az' | Invoke-GooNativeCommand -CommandArgs @(
                'webapp',
                'create',
                '--resource-group', $ResourceGroup.ResourceGroupName,
                '--plan', $AppServicePlan.Name
                '--name', $webAppName,
                '--deployment-container-image-name', 'nginx'
            ) | Out-Null

            $webApp = Get-AzWebApp -ResourceGroupName $ResourceGroup.ResourceGroupName -Name $webAppName

            $webApp.Name | Write-GooLog -Level CREATE -ForegroundColor Green
        }
    }

    end {
        New-GooLogMessage -Separator | Write-GooLog
    }
}
