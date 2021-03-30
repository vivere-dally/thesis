function Mount-bsAppServicePlan {
  [CmdletBinding()]
  [OutputType([Microsoft.Azure.Commands.WebApps.Models.WebApp.PSAppServicePlan])]
  param ()
    
  New-GooLogMessage -Stage | Write-GooLog
}

function New-bsAppServicePlan {
  [CmdletBinding()]
  [OutputType([Microsoft.Azure.Commands.WebApps.Models.WebApp.PSAppServicePlan])]
  param (
    [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
    [hashtable]
    $ASPConfig
  )

  New-GooLogMessage -Stage | Write-GooLog
  $aspName = "$Script:ResourceGroupName$($ASPConfig.Suffix)"
  $asp = Get-AzAppServicePlan -ResourceGroupName $Script:ResourceGroupName -Name $aspName -ErrorAction SilentlyContinue
  if (-not $asp) {
    $params = @{
      Location          = $Script:Location;
      ResourceGroupName = $Script:ResourceGroupName;
      Name              = $aspName;
    }

    $params += $ASPConfig
    $asp = New-AzAppServicePlan @params
    "Created the AppServicePlan named $($asp.Name)" | Write-GooLog -ForegroundColor Green
  }

  return $asp
}

Export-ModuleMember -Function 'Mount-bsAppServicePlan'
