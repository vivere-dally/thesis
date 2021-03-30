[CmdletBinding()]
[OutputType()]
[Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSAvoidUsingPlainTextForPassword', '')]
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
Import-Module -Name "$PSScriptRoot/Utils.ps1" -Global -Force

$Private:BEPath = ("../../Server/thesis" | Resolve-Path).Path
$Private:FEPath = ("../../Client/thesis" | Resolve-Path).Path

try {
    if (-not ($ProjectVersion | Test-GooSemVer)) {
        throw "The $ProjectVersion is not following the SemVer guidelines."
    }

    $Buildmetadata = ($ProjectVersion | ConvertFrom-GooSemVer).buildmetadata
    $ProjectVersion = $ProjectVersion | Reset-GooSemVer -Identifier Buildmetadata
    'docker' | Invoke-NativeCommand -CommandArgs @('login', "$ACRUsername.azurecr.io", '-u', $ACRUsername, '-p', $ACRPassword)

    # Docker Build backend image
    Set-Location -Path $Private:BEPath
    $tag = "$ACRUsername.azurecr.io/$BranchName/thesisapi:$ProjectVersion"
    'docker' | Invoke-NativeCommand -CommandArgs @('build', '-f', './docker/Dockerfile', '-t', $tag, '.', '--label', "buildmetadata=$Buildmetadata")
    'docker' | Invoke-NativeCommand -CommandArgs @('push', $tag)
    'docker' | Invoke-NativeCommand -CommandArgs @('rmi', $tag)

    # Docker Build frontend image
    Set-Location -Path $Private:FEPath
    $tag = "$ACRUsername.azurecr.io/$BranchName/thesis:$ProjectVersion"
    'docker' | Invoke-NativeCommand -CommandArgs @('build', '-f', './docker/Dockerfile', '-t', $tag, '.', '--label', "buildmetadata=$Buildmetadata")
    'docker' | Invoke-NativeCommand -CommandArgs @('push', $tag)
    'docker' | Invoke-NativeCommand -CommandArgs @('rmi', $tag)

    exit 0
}
catch {
    $_
    exit 1
}
finally {
    Set-Location -Path $PSScriptRoot
}
