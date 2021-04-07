
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

        $sa.Name | Write-GooLog -Level CREATE -ForegroundColor Green
    }
    else {
        "Fetched $($sa.Name). Verifying properties..." | Write-GooLog
        
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

            $updatedAsp.Name | Write-GooLog -Level UPDATE -ForegroundColor Yellow
            Format-bsAzResourceUpdate $sa $updatedSa | Write-GooLog -Level UPDATE -ForegroundColor Yellow

            $sa = $updatedSa
        }
    }

    $sa.Name | Write-GooLog -Level MOUNT
    New-GooLogMessage -Separator | Write-GooLog

    return $asp
}
