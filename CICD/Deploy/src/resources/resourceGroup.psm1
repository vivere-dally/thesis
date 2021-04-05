
function Mount-bsResourceGroup {
    [CmdletBinding()]
    [OutputType([Microsoft.Azure.Commands.ResourceManager.Cmdlets.SdkModels.PSResourceGroup])]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [hashtable]
        $RGConfig,

        [Parameter(Mandatory = $true)]
        [string]
        $Location,

        [Parameter(Mandatory = $true)]
        [string]
        $ResourceGroupName
    )

    New-GooLogMessage 'ResourceGroup Management' -Step | Write-GooLog

    $RGConfig.Property.Tag['DeploymentTime'] = (Get-Date -AsUTC).ToString()
    $resourceGroup = Get-AzResourceGroup -Location $Location -Name $ResourceGroupName -ErrorAction SilentlyContinue
    if (-not $resourceGroup) {
        $resourceGroup = New-AzResourceGroup -Location $Location -Name $ResourceGroupName -Tag $RGConfig.Property.Tag

        $resourceGroup.ResourceGroupName | Write-GooLog -Level CREATE -ForegroundColor Green
    }
    else {
        "Fetched $($resourceGroup.ResourceGroupName). Verifying properties..." | Write-GooLog

        $updateResourceGroup = Set-AzResourceGroup -Name $ResourceGroupName -Tag $RGConfig.Property.Tag

        $updateResourceGroup.ResourceGroupName | Write-GooLog -Level UPDATE -ForegroundColor Yellow
        Format-bsAzResourceUpdate $resourceGroup $updateResourceGroup -Ignore TagsTable | Write-GooLog -ForegroundColor Yellow

        $resourceGroup = $updateResourceGroup
    }

    $resourceGroup.ResourceGroupName | Write-GooLog -Level MOUNT

    New-GooLogMessage -Separator | Write-GooLog
    return $resourceGroup
}
