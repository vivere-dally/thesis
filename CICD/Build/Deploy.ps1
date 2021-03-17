[CmdletBinding()]
[OutputType()]
param (
    [Parameter(Mandatory = $true)]
    [string]
    $ACRUsername,

    [Parameter(Mandatory = $true)]
    [string]
    $ACRPassword,

    [Parameter(Mandatory = $true)]
    [string]
    $ProjectVersion,

    [Parameter(Mandatory = $true)]
    [string]
    $BranchName
)

#Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core
#Requires -Module @{ ModuleName = 'SemVerGoodies'; RequiredVersion = '0.2.0' }

$ErrorActionPreference = 'Stop'
Import-Module -Name "$PSScriptRoot\Utils.ps1" -Global -Force

$Private:CurrentDir = $PSScriptRoot
$Private:BEPath = ("../../Server/thesis" | Resolve-Path).Path
$Private:FEPath = ("../../Client/thesis" | Resolve-Path).Path

try {
    if (-not ($ProjectVersion | Test-GooSemVer)) {
        throw "The $ProjectVersion is not following the SemVer guidelines."
    }

    $Buildmetadata = ($ProjectVersion | ConvertFrom-GooSemVer).buildmetadata
    $ProjectVersion = $ProjectVersion | Reset-GooSemVer -Identifier Buildmetadata

    'docker' | Invoke-NativeCommand -CommandArgs @('login')

    # Docker Build backend image
    Set-Location -Path $Private:BEPath


    # docker login bsir2465.azurecr.io -u bsir2465 -p pass
    exit 0
}
catch {
    $_
    exit 1
}
finally {
    Set-Location -Path $Private:CurrentDir
}
