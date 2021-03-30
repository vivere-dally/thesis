[CmdletBinding()]
[Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSAvoidUsingPlainTextForPassword', '')]
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
#Requires -Module @{ ModuleName = 'LogGoodies'; RequiredVersion = '0.1.1' }
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.1.3' }

$Script:Config = "$PSScriptRoot\bootstrapper.ps1" | Invoke-GooNativeCommand

function Start-Deployment {
  try {
    Add-GooLogPath "$PSScriptRoot\deploy.log" -Force
    $ClientSecret = $ClientSecret | ConvertTo-SecureString -AsPlainText -Force

    New-GooLogMessage -Stage | Write-GooLog; Write-GooLog
    $contextName = Connect-bsAzAccount $SubscriptionId $TenantId $ClientId $ClientSecret

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
