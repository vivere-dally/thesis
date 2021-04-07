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
    $Location,

    [Parameter(Mandatory = $true)]
    [string]
    $BranchName,

    [Parameter(Mandatory = $true)]
    [string]
    $MySqlRootPassword,

    [Parameter(Mandatory = $true)]
    [string]
    $MySqlUsername,

    [Parameter(Mandatory = $true)]
    [string]
    $MySqlPassword
)

$Global:ErrorActionPreference = 'Stop'
$Global:GooLogAnsiPreference = 'Set'
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12 -bor [System.Net.SecurityProtocolType]::Tls13

# Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core

#Requires -Module @{ ModuleName = 'Az'; RequiredVersion = '5.7.0' }
#Requires -Module @{ ModuleName = 'LogGoodies'; RequiredVersion = '0.1.1' }
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.2.2' }

Import-Module -Name (Get-ChildItem -Path $PSScriptRoot -Filter '*.psm1' -Recurse | Select-Object -ExpandProperty FullName) -Global -Force
$config = Get-ChildItem -Path $PSScriptRoot -Filter '*.json' -Recurse | ForEach-Object { @{$_.BaseName = Get-Content -Path $_.FullName | ConvertFrom-Json -AsHashtable } }

function Start-Deployment {
    try {
        Add-GooLogPath "$PSScriptRoot\deploy.log" -Force
        'CREATE', 'UPDATE', 'MOUNT' | Add-GooLogLevel
        New-GooLogMessage -Stage | Write-GooLog

        $ClientSecureSecret = $ClientSecret | ConvertTo-SecureString -AsPlainText -Force

        # Az.Accounts
        $contextName = Connect-bsAzAccount $SubscriptionId $TenantId $ClientId $ClientSecureSecret
        Connect-bsAzCLIAccount $SubscriptionId $TenantId $ClientId $ClientSecret

        # Az.Resources
        $resourceGroup = $config.resourceGroup | Mount-bsResourceGroup -Location $Location -ResourceGroupName $ResourceGroupName

        # Az.Storage
        $storageAccount = $config.storageAccount | Mount-bsStorageAccount -ResourceGroup $resourceGroup

        # Az.Websites
        $appServicePlan = $config.appServicePlan | Mount-bsAppServicePlan -ResourceGroup $resourceGroup
        $beWebApp, $feWebApp = $config.webApp | Mount-bsWebApp -ResourceGroup $resourceGroup -AppServicePlan $appServicePlan

        'Deployment succeeded' | Write-GooLog -ForegroundColor Green
    }
    catch {
        $_
        $_.FullyQualifiedErrorId | Write-GooLog -Level ERROR -ForegroundColor Red
        $_.ScriptStackTrace | Write-GooLog -Level ERROR -ForegroundColor Red
    }
    finally {
        if ($contextName) {
            $contextName | Disconnect-bsAzAccount
        }

        $ClientId | Disconnect-bsAzCLIAccount
    }
}

Start-Deployment
