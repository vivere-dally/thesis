function Mount-bsResourceGroup {
  [CmdletBinding()]
  [OutputType([Microsoft.Azure.Commands.ResourceManager.Cmdlets.SdkModels.PSResourceGroup])]
  param (
    [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
    [hashtable]
    $RGConfig,

    [Parameter(Mandatory = $true, Position = 0)]
    [string]
    $Location,

    [Parameter(Mandatory = $true, Position = 1)]
    [string]
    $ResourceGroupName
  )
  
  New-GooLogMessage 'ResourceGroup Management' -Step | Write-GooLog

  $resourceGroup = Get-AzResourceGroup -Location $Location -Name $ResourceGroupName -ErrorAction SilentlyContinue
  if (-not $resourceGroup) {
    $resourceGroup = New-AzResourceGroup -Location $Location -Name $ResourceGroupName -Tag $RGConfig.Property.Tag
    "Created $($resourceGroup.ResourceGroupName)" | Write-GooLog -ForegroundColor Green
  }

  $resourceGroup = Set-AzResourceGroup -Name $ResourceGroupName -Tag $RGConfig.Property.Tag
  "Updated the remote resource $($resourceGroup.ResourceGroupName)" | Write-GooLog
  "Mounted $($resourceGroup.ResourceGroupName)" | Write-GooLog
  return $resourceGroup
}
