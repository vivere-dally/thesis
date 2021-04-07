
function Mount-bsAppServicePlan {
    [CmdletBinding()]
    [OutputType([Microsoft.Azure.Commands.WebApps.Models.WebApp.PSAppServicePlan])]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [hashtable]
        $ASPConfig,

        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.ResourceManager.Cmdlets.SdkModels.PSResourceGroup]
        $ResourceGroup
    )

    New-GooLogMessage 'AppServicePlan Management' -Step | Write-GooLog

    $aspName = "$($ResourceGroup.ResourceGroupName)$($ASPConfig.Suffix)"
    $asp = Get-AzAppServicePlan -ResourceGroupName $ResourceGroup.ResourceGroupName -Name $aspName -ErrorAction SilentlyContinue
    if (-not $asp) {
        $params = @{
            Location          = $ResourceGroup.Location;
            ResourceGroupName = $ResourceGroup.ResourceGroupName;
            Name              = $aspName;
        } + $ASPConfig.Property;
        $asp = New-AzAppServicePlan @params

        $asp.Name | Write-GooLog -Level CREATE -ForegroundColor Green
    }
    else {
        "Fetched $($asp.Name). Verifying properties..." | Write-GooLog

        $workerSize = @{
            Small      = 1
            Medium     = 2
            Large      = 3
            ExtraLarge = 4
        }

        $params = @{}
        if ($ASPConfig.Property.Tier -ne $asp.Sku.Tier) { 
            $params['Tier'] = $ASPConfig.Property.Tier 
        }

        $skuSize = "$($ASPConfig.Property.Tier[0])$($workerSize[$ASPConfig.Property.WorkerSize])"
        if ($skuSize -ne $asp.Sku.Size) {
            $params['WorkerSize'] = $ASPConfig.Property.WorkerSize
        }

        if ($ASPConfig.Property.NumberOfWorkers -ne $asp.Sku.Capacity) {
            $params['NumberOfWorkers'] = $ASPConfig.Property.NumberOfWorkers;
        }

        if (0 -lt $params.Keys.Count) {
            $params += @{
                ResourceGroupName = $ResourceGroup.ResourceGroupName;
                Name              = $asp.Name; 
            }

            $updatedAsp = Set-AzWebApp @params

            $updatedAsp.Name | Write-GooLog -Level UPDATE -ForegroundColor Yellow
            Format-bsAzResourceUpdate $asp $updatedAsp | Write-GooLog -Level UPDATE -ForegroundColor Yellow

            $asp = $updatedAsp
        }
    }

    $asp.Name | Write-GooLog -Level MOUNT
    New-GooLogMessage -Separator | Write-GooLog

    return $asp
}
