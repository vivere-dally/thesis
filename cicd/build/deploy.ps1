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

try {
    if (-not ($ProjectVersion | Test-GooSemVer)) {
        throw "The Project Version $ProjectVersion is not following the SemVer guidelines."
    }

    $Buildmetadata = ($ProjectVersion | ConvertFrom-GooSemVer).buildmetadata
    $ProjectVersion = $ProjectVersion | Reset-GooSemVer -Identifier Buildmetadata | Reset-GooSemVer -Identifier Buildmetadata
    'docker' | Invoke-GooNativeCommand -CommandArgs @('login', "$ACRUsername.azurecr.io", '-u', $ACRUsername, '-p', $ACRPassword)

    @(
        ("$PSScriptRoot/../../backend/server" | Resolve-Path).Path,
        ("$PSScriptRoot/../../backend/frontend_config_provider" | Resolve-Path).Path,
        ("$PSScriptRoot/../../frontend/client" | Resolve-Path).Path
    ) | ForEach-Object {
        $_ | Set-Location
        $projectName = $_ | Split-Path -Leaf
        $tag = "$ACRUsername.azurecr.io/$BranchName/${projectName}:$ProjectVersion"
        'docker' | Invoke-GooNativeCommand -CommandArgs @('build', '-f', './docker/Dockerfile', '-t', $tag, '.', '--label', "buildmetadata=$Buildmetadata")
        'docker' | Invoke-GooNativeCommand -CommandArgs @('push', $tag)
        'docker' | Invoke-GooNativeCommand -CommandArgs @('rmi', $tag)
    }

    exit 0
}
catch {
    $_
    $_.ScriptStackTrace
    exit 1
}
finally {
    $PSScriptRoot | Set-Location
}
