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
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.2.3' }

$ErrorActionPreference = 'Stop'

$Private:BEPath = ("$PSScriptRoot/../../Server/thesis" | Resolve-Path).Path
$Private:FEPath = ("$PSScriptRoot/../../Client/thesis" | Resolve-Path).Path

try {
    if (-not ($ProjectVersion | Test-GooSemVer)) {
        throw "The Project Version $ProjectVersion is not following the SemVer guidelines."
    }

    $Buildmetadata = ($ProjectVersion | ConvertFrom-GooSemVer).buildmetadata
    $ProjectVersion = $ProjectVersion | Reset-GooSemVer -Identifier Buildmetadata | Reset-GooSemVer -Identifier Buildmetadata
    'docker' | Invoke-GooNativeCommand -CommandArgs @('login', "$ACRUsername.azurecr.io", '-u', $ACRUsername, '-p', $ACRPassword)

    # Docker Build backend image
    Set-Location -Path $Private:BEPath
    $tag = "$ACRUsername.azurecr.io/$BranchName/thesisapi:$ProjectVersion"
    'docker' | Invoke-GooNativeCommand -CommandArgs @('build', '-f', './docker/Dockerfile', '-t', $tag, '.', '--label', "buildmetadata=$Buildmetadata")
    'docker' | Invoke-GooNativeCommand -CommandArgs @('push', $tag)
    'docker' | Invoke-GooNativeCommand -CommandArgs @('rmi', $tag)

    # Docker Build frontend image
    Set-Location -Path $Private:FEPath
    $tag = "$ACRUsername.azurecr.io/$BranchName/thesis:$ProjectVersion"
    'docker' | Invoke-GooNativeCommand -CommandArgs @('build', '-f', './docker/Dockerfile', '-t', $tag, '.', '--label', "buildmetadata=$Buildmetadata")
    'docker' | Invoke-GooNativeCommand -CommandArgs @('push', $tag)
    'docker' | Invoke-GooNativeCommand -CommandArgs @('rmi', $tag)

    exit 0
}
catch {
    $_
    $_.ScriptStackTrace
    exit 1
}
finally {
    Set-Location -Path $PSScriptRoot
}
