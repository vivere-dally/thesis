function Mount-bsAppServicePlan {
  [CmdletBinding()]
  [OutputType([Microsoft.Azure.Commands.WebApps.Models.WebApp.PSAppServicePlan])]
  param (
    [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
    [hashtable]
    $ASPConfig,

    [Parameter(Mandatory = $true, Position = 0)]
    [string]
    $Location,

    [Parameter(Mandatory = $true, Position = 1)]
    [string]
    $ResourceGroupName
  )

  New-GooLogMessage 'AppServicePlan Management' -Step| Write-GooLog

  $aspName = "$ResourceGroupName$($ASPConfig.Suffix)"
  $asp = Get-AzAppServicePlan -ResourceGroupName $ResourceGroupName -Name $aspName -ErrorAction SilentlyContinue
  if (-not $asp) {
    $params = @{
      Location          = $Location;
      ResourceGroupName = $ResourceGroupName;
      Name              = $aspName;
    } + $ASPConfig.Property;
    $asp = New-AzAppServicePlan @params
    "Created $($asp.Name)" | Write-GooLog -ForegroundColor Green
  }
  else {

  }

  $asp = $asp | Set-AzAppServicePlan 
  "Mounted $($asp.Name)" | Write-GooLog
  return $asp
}
