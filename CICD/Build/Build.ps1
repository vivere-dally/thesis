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
    [string]
    $ProjectVersion,

    [Parameter(Mandatory = $false)]
    [boolean]
    $UseCommonNpmModules = $true,

    [Parameter(Mandatory = $false)]
    [string]
    $CommonNpmModulesPath = 'E:\Dev\npm\node_modules'
)

#Requires -Module @{ ModuleName = 'SemVerPs'; RequiredVersion = '1.0' }
Import-Module -Name "$PSScriptRoot\Utils.ps1" -Global -Force

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
        $packageJson = Get-Content -Path ".\package.json" | ConvertFrom-Json
        $packageJson.version = $ProjectVersion
        $packageJson | ConvertTo-Json -Depth 5 | Set-Content -Path ".\package.json" -Force
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
