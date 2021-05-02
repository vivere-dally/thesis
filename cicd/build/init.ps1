[CmdletBinding()]
[OutputType([string])]
param (
    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [ValidateSet('', 'Patch', 'Minor', 'Major')]
    [string]
    $Release,

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

$ErrorActionPreference = 'Stop'

$Private:ClientPath = ("$PSScriptRoot/../../frontend/client" | Resolve-Path).Path

try {
    Set-Location $Private:ClientPath
    # Get the Project Version from package.json
    $ProjectVersion = (Get-Content '.\package.json' | ConvertFrom-Json).version
    if (-not ($ProjectVersion | Test-GooSemVer)) {
        throw "The Project Version $ProjectVersion found in the SCM is not following the SemVer guidelines."
    }

    $NewProjectVersion = $ProjectVersion
    # Increment based on release
    if ($Release) {
        $NewProjectVersion = $NewProjectVersion | Step-GooSemVer -Identifier $Release
    }

    # Increment based on prerelease
    if ($Prerelease) {
        $projectVersionPrerelease = ($ProjectVersion | ConvertFrom-GooSemVer).prerelease
        $newProjectVersionPrerelease = ($NewProjectVersion | ConvertFrom-GooSemVer).prerelease
        if (
            $projectVersionPrerelease -and
            $newProjectVersionPrerelease -and
            $Prerelease -eq ($projectVersionPrerelease.split('.')[0]) -and
            $Prerelease -eq ($newProjectVersionPrerelease.split('.')[0])
        ) {
            $NewProjectVersion = $NewProjectVersion | Set-GooSemVer -Identifier Prerelease -Value $projectVersionPrerelease
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
    $_.ScriptStackTrace
    exit 1
}
finally {
    Set-Location $PSScriptRoot
}
