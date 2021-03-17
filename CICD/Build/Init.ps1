[CmdletBinding()]
[OutputType([string])]
param (
    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [ValidateSet('', 'Patch', 'Minor', 'Major')]
    [string]
    $Release = '',

    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [ValidateSet('', 'alpha', 'beta', 'rc')]
    [string]
    $Prerelease,

    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [string]
    $Buildmetadata
)

#Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core
#Requires -Module @{ ModuleName = 'SemVerGoodies'; RequiredVersion = '0.2.0' }

Import-Module -Name "$PSScriptRoot\Utils.ps1" -Global -Force
$ErrorActionPreference = 'Stop'

$Private:CurrentDir = $PSScriptRoot
$Private:FEPath = ("../../Client/thesis" | Resolve-Path).Path

try {
    Set-Location $Private:FEPath
    # Get the Project Version from package.json
    $ProjectVersion = (Get-Content '.\package.json' | ConvertFrom-Json).version
    if (-not ($ProjectVersion | Test-GooSemVer)) {
        throw "The $ProjectVersion found in the SCM is not following the SemVer guidelines."
    }

    $NewProjectVersion = $ProjectVersion
    # Increment based on release
    if ($Release) {
        $NewProjectVersion = $NewProjectVersion | Step-GooSemVer -Identifier $Release
    }

    # Increment based on prerelease
    if ($Prerelease) {
        $scmPrerelease = ($ProjectVersion | ConvertFrom-GooSemVer).prerelease
        $scmPrereleaseMajor = $scmPrerelease.Split('.')[0]
        if ($Prerelease -eq $scmPrereleaseMajor) {
            $NewProjectVersion = $NewProjectVersion | Set-GooSemVer -Identifier Prerelease -Value $scmPrerelease
            $NewProjectVersion = $NewProjectVersion | Step-GooSemVer -Identifier Prerelease
        }
        else {
            $NewProjectVersion = $NewProjectVersion | Set-GooSemVer -Identifier Prerelease -Value $Prerelease
        }
    }

    # Set build metadata
    if ($Buildmetadata) {
        $NewProjectVersion = $NewProjectVersion | Set-GooSemVer -Identifier Buildmetadata -Value $Buildmetadata
    }

    # Test if ProjectVersion precedes the NewPorjectVersion
    if (($NewProjectVersion | Compare-GooSemVer -ReferenceVersion $ProjectVersion) -eq '<') {
        throw "The SCM version $ProjectVersion do not precede the new version $NewProjectVersion."
    }

    # Out
    $NewProjectVersion
    exit 0
}
catch {
    $_
    exit 1
}
finally {
    Set-Location $Private:CurrentDir
}
