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
    $ACRUsername,

    [Parameter(Mandatory = $true)]
    [string]
    $ACRPassword,

    [Parameter(Mandatory = $true)]
    [string]
    $BranchName,

    [Parameter(Mandatory = $true)]
    [string]
    $Tag,

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
#Requires -Module @{ ModuleName = 'Az.MySql'; RequiredVersion = '0.6.0' }
#Requires -Module @{ ModuleName = 'LogGoodies'; RequiredVersion = '0.1.1' }
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.2.3' }
#Requires -Module @{ ModuleName = 'SemVerGoodies'; RequiredVersion = '0.2.0' }

Import-Module -Name (Get-ChildItem -Path $PSScriptRoot -Filter '*.psm1' -Recurse | Select-Object -ExpandProperty FullName) -Global -Force
$config = Get-ChildItem -Path $PSScriptRoot -Filter '*.json' -Recurse | ForEach-Object { @{$_.BaseName = Get-Content -Path $_.FullName | ConvertFrom-Json -AsHashtable } }

function Start-Deployment {
    try {
        if (-not ($Tag | Test-GooSemVer)) {
            throw "The Project Version $Tag is not following the SemVer guidelines."
        }

        # Discard build metadata
        $Tag = $Tag | Reset-GooSemVer -Identifier Buildmetadata | Reset-GooSemVer -Identifier Buildmetadata

        Add-GooLogPath "$PSScriptRoot\deliver.log" -Force
        'CREATE', 'UPDATE', 'MOUNT' | Add-GooLogLevel
        New-GooLogMessage -Stage | Write-GooLog

        $ClientSecureSecret = $ClientSecret | ConvertTo-SecureString -AsPlainText -Force

        # Az.Accounts
        $contextName = Connect-bsAzAccount $SubscriptionId $TenantId $ClientId $ClientSecureSecret
        Connect-bsAzCLIAccount $SubscriptionId $TenantId $ClientId $ClientSecret

        # Az.Resources
        $config.resourceGroup.Property.Tag += @{
            DeploymentTime = (Get-Date -AsUTC).ToString();
            BranchName     = $BranchName;
            ImageTag       = $Tag;
        }

        $resourceGroup = $config.resourceGroup | Mount-bsResourceGroup -Location $Location -ResourceGroupName $ResourceGroupName

        # Az.MySql
        $mySqlServer = $config.mySql | Mount-bsMySqlServer `
            -ResourceGroup $resourceGroup `
            -MySqlUsername $MySqlUsername `
            -MySqlPassword $MySqlPassword

        # Az.Storage
        $storageAccount = $config.storageAccount | Mount-bsStorageAccount -ResourceGroup $resourceGroup

        # Az.Websites
        $appServicePlan = $config.appServicePlan | Mount-bsAppServicePlan -ResourceGroup $resourceGroup
        $beWebApp, $feWebApp = $config.webApp | Mount-bsWebApp `
            -ResourceGroup $resourceGroup `
            -AppServicePlan $appServicePlan `
            -StorageAccount $storageAccount `
            -ACRUsername $ACRUsername `
            -ACRPassword $ACRPassword `
            -BranchName $BranchName `
            -Tag $Tag `
            -MySqlUsername $MySqlUsername `
            -MySqlPassword $MySqlPassword `
            -MySqlServer $mySqlServer

        'Deployment succeeded' | Write-GooLog -ForegroundColor DarkGreen
    }
    catch {
        $_
        $_.FullyQualifiedErrorId | Write-GooLog -Level ERROR -ForegroundColor DarkRed
        $_.ScriptStackTrace | Write-GooLog -Level ERROR -ForegroundColor DarkRed
    }
    finally {
        if ($contextName) {
            $contextName | Disconnect-bsAzAccount
        }

        $ClientId | Disconnect-bsAzCLIAccount
    }
}

Start-Deployment
