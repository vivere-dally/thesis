[CmdletBinding()]
[Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSAvoidUsingPlainTextForPassword', '')]
[Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSUseDeclaredVarsMoreThanAssignments', '')]
param (
  [Parameter(Mandatory = $true)]
  [string]
  $SubscriptionId,

  [Parameter(Mandatory = $true)]
  [string]
  $TenantId,

  [Parameter(Mandatory = $true)]
  [string]
  $ClientId,

  [Parameter(Mandatory = $true)]
  [string]
  $ClientSecret,

  [Parameter(Mandatory = $true)]
  [string]
  $ResourceGroupName,

  [Parameter(Mandatory = $true)]
  [string]
  $Location
)

$Global:ErrorActionPreference = 'Stop'
$Global:GooLogAnsiPreference = 'Set'
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls13

# Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core

#Requires -Module @{ ModuleName = 'Az.Accounts'; RequiredVersion = '2.2.7' }
#Requires -Module @{ ModuleName = 'Az.Resources'; RequiredVersion = '3.4.0' }
#Requires -Module @{ ModuleName = 'Az.Websites'; RequiredVersion = '2.5.0' }
#Requires -Module @{ ModuleName = 'LogGoodies'; RequiredVersion = '0.1.1' }
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.1.3' }

Import-Module -Name (Get-ChildItem -Path $PSScriptRoot -Filter '*.psm1' -Recurse | Select-Object -ExpandProperty FullName) -Global -Force
$config = Get-ChildItem -Path $PSScriptRoot -Filter '*.json' -Recurse | ForEach-Object {
  $obj = @{$_.BaseName = Get-Content -Path $_.FullName | ConvertFrom-Json -AsHashtable }
  $obj.($_.BaseName) | ForEach-Object { $_.Property.Tag.DeploymentTime = (Get-Date -AsUTC).ToString() }
  $obj
}

function Start-Deployment {
  try {
    Add-GooLogPath "$PSScriptRoot\deploy.log" -Force
    New-GooLogMessage -Stage | Write-GooLog

    $ClientSecret = $ClientSecret | ConvertTo-SecureString -AsPlainText -Force

    $contextName = Connect-bsAzAccount $SubscriptionId $TenantId $ClientId $ClientSecret
    New-GooLogMessage -Separator | Write-GooLog

    $resourceGroup = $config.resourceGroup | Mount-bsResourceGroup $Location $ResourceGroupName
    New-GooLogMessage -Separator | Write-GooLog

    $appServicePlan = $config.appServicePlan | Mount-bsAppServicePlan $Location $ResourceGroupName
    New-GooLogMessage -Separator | Write-GooLog

    'Deployment succeeded' | Write-GooLog -ForegroundColor Green
  }
  catch {
    $_
    $_.FullyQualifiedErrorId | Write-GooLog -Level ERROR -ForegroundColor Red
    $_.ScriptStackTrace | Write-GooLog -Level ERROR -ForegroundColor Red
  }
  finally {

    $contextName | Disconnect-bsAzAccount
  }
}

Start-Deployment
