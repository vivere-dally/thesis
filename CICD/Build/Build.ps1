[CmdletBinding()]
[OutputType()]
param (
    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [string]
    $ProjectVersion,

    [Parameter(Mandatory = $false)]
    [boolean]
    $FreshNpmModules = $false
)

#Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core

$ErrorActionPreference = 'Stop'
Import-Module -Name "$PSScriptRoot\Utils.ps1" -Global -Force

$Private:CurrentDir = $PSScriptRoot
$Private:CommonNpmModulesPath = 'E:\Dev\npm\node_modules'
$Private:BEPath = ("../../Server/thesis" | Resolve-Path).Path
$Private:FEPath = ("../../Client/thesis" | Resolve-Path).Path

try {
    Set-Location -Path $Private:BEPath
    # Update Backend's Version
    if ($ProjectVersion) {
        'mvn' | Invoke-NativeCommand -CommandArgs @('versions:set', "-DnewVersion=$ProjectVersion")
    }

    # Build Backend
    'mvn' | Invoke-NativeCommand -CommandArgs @('-B', '-DskipTests', 'clean', 'package')

    Set-Location $Private:FEPath
    # Update Frontend's Version
    if ($ProjectVersion) {
        @('.\package.json', '.\packageAfter.json') | Edit-JsonField -Name 'version' -Value $ProjectVersion
    }

    # Create junction for node_modules.
    # Improve lifetime of the SSD.
    # Improve the speed of the Build Job.
    if (-not $FreshNpmModules -and
        -not (Test-Path '.\node_modules') -and
        (Test-Path $Private:CommonNpmModulesPath)) {
        'cmd.exe' | Invoke-NativeCommand -CommandArgs @('/c', 'mklink', '/J', '.\node_modules', $Private:CommonNpmModulesPath)
    }

    # Build Frontend
    'npm' | Invoke-NativeCommand -CommandArgs @('install')
    'npm' | Invoke-NativeCommand -CommandArgs @('run', 'build')

    # Zip Frontend's Artifacts
    Compress-Archive -Path ".\build\*" -DestinationPath ".\thesis-$ProjectVersion.zip" -CompressionLevel Fastest
    exit 0
}
catch {
    $_
    exit 1
}
finally {
    Set-Location $Private:CurrentDir
}
