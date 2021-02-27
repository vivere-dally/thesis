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
    [string]
    $FrontendAbsolutePath = ("../../Client/thesis" | Resolve-Path).Path,

    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [string]
    $ProjectVersion,

    [Parameter(Mandatory = $false)]
    [boolean]
    $UseCommonNpmModules = $true,

    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [string]
    $CommonNpmModulesPath = 'E:\Dev\npm\node_modules'
)

#Requires -Module @{ ModuleName = 'SemVerPs'; RequiredVersion = '1.0' }
Import-Module -Name "$PSScriptRoot\Utils.ps1" -Global -Force
$pythonUtilsPath = "$PSScriptRoot\Utils.py"
$ErrorActionPreference = 'Stop'

try {
    Set-Location -Path $BackendAbsolutePath
    # Update Backend's Version
    if ($ProjectVersion) {
        Invoke-NativeCommand -Command "mvn" -CommandArgs @('versions:set', "-DnewVersion=$ProjectVersion")
    }

    # Build Backend
    Invoke-NativeCommand -Command "mvn" -CommandArgs @('-B', '-DskipTests', 'clean', 'package')

    Set-Location $FrontendAbsolutePath
    # Update Frontend's Version
    if ($ProjectVersion) {
        Invoke-NativeCommand -Command 'python' -CommandArgs @($pythonUtilsPath, 'update_package_json_file', ('.\package.json' | Resolve-Path).Path, $ProjectVersion)
        Invoke-NativeCommand -Command 'python' -CommandArgs @($pythonUtilsPath, 'update_package_json_file', ('.\package-lock.json' | Resolve-Path).Path, $ProjectVersion)
    }

    # Create junction for node_modules. Improve lifetime of my SSD since Jenkins is running on it.
    if ($UseCommonNpmModules -and (Test-Path $CommonNpmModulesPath) -and -not (Test-Path '.\node_modules')) {
        Invoke-NativeCommand -Command "cmd.exe" -CommandArgs @('/c', 'mklink', '/J', '.\node_modules', $CommonNpmModulesPath)
    }

    # Build Frontend
    Invoke-NativeCommand -Command "npm" -CommandArgs @('install')
    Invoke-NativeCommand -Command "npm" -CommandArgs @('run', 'build')

    # Zip Frontend's Artifacts
    Compress-Archive -Path ".\build\*" -DestinationPath ".\thesis-$ProjectVersion.zip" -CompressionLevel Fastest
    exit 0
}
catch {
    $_
    exit 1
}
