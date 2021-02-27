<#
.SYNOPSIS
    Short description
.DESCRIPTION
    Long description
.PARAMETER example
    Explanation of the parameter
.EXAMPLE
    PS C:\> <example usage>
    Explanation of what the example does
.NOTES
    General notes
#>
[CmdletBinding()]
[OutputType()]
param (
    [Parameter(Mandatory = $false)]
    [string]
    $BackendAbsolutePath = ("../../Server/thesis" | Resolve-Path).Path,

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
    $BuildMetadata
)

#Requires -Module @{ ModuleName = 'SemVerPs'; RequiredVersion = '1.0' }
Import-Module -Name "$PSScriptRoot\Utils.ps1" -Global -Force
$ErrorActionPreference = 'Stop'

try {
    Set-Location -Path $BackendAbsolutePath
    $ProjectVersion = Invoke-NativeCommand -Command 'mvn' -CommandArgs @('help:evaluate', '-Dexpression=project.version') -NoLogs | Where-Object { -not $_.StartsWith('[') }
    if (-not (Test-SemVer -InputObject $ProjectVersion)) {
        throw "The $ProjectVersion found in the SCM is not following the SemVer guidelines."
    }

    $ProjectVersion = $ProjectVersion | ConvertTo-SemVer
    switch ($Release) {
        'Patch' { $NewProjectVersion = $ProjectVersion.Change($ProjectVersion.Major, $ProjectVersion.Minor, $ProjectVersion.Patch + 1, $Prerelease, $BuildMetadata); break; }
        'Minor' { $NewProjectVersion = $ProjectVersion.Change($ProjectVersion.Major, $ProjectVersion.Minor + 1, 0, $Prerelease, $BuildMetadata); break; }
        'Major' { $NewProjectVersion = $ProjectVersion.Change($ProjectVersion.Major + 1, 0, 0, $Prerelease, $BuildMetadata); break; }
        Default {
            if (-not $Prerelease) {
                $NewProjectVersion = $ProjectVersion.Change($ProjectVersion.Major, $ProjectVersion.Minor, $ProjectVersion.Patch, $ProjectVersion.Prerelease, $BuildMetadata);
            }
            else {
                $NewProjectVersion = $ProjectVersion.Change($ProjectVersion.Major, $ProjectVersion.Minor, $ProjectVersion.Patch, $Prerelease, $BuildMetadata);
            }
        }
    }

    if ($ProjectVersion.CompareByPrecedence($NewProjectVersion) -gt 0) {
        throw "The SCM version $ProjectVersion does not precedes the new version $NewProjectVersion."
    }

    $NewProjectVersion.ToString().Trim()
    exit 0
}
catch {
    $_
    exit 1
}
