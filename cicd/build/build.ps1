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
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.2.3' }

$ErrorActionPreference = 'Stop'

$Private:CommonNpmModulesPath = 'E:\Dev\npm\node_modules'
$Private:ServerPath = ("$PSScriptRoot/../../backend/server" | Resolve-Path).Path
$Private:ClientPath = ("$PSScriptRoot/../../frontend/client" | Resolve-Path).Path

try {
    Set-Location -Path $Private:ServerPath
    # Update Server's Version
    if ($ProjectVersion) {
        'mvn' | Invoke-GooNativeCommand -CommandArgs @('versions:set', "-DnewVersion=$ProjectVersion")
    }

    # Build Server
    'mvn' | Invoke-GooNativeCommand -CommandArgs @('-B', '-DskipTests', 'clean', 'package') -Verbose

    Set-Location $Private:ClientPath
    # Update Client's Version
    if ($ProjectVersion) {
        @('.\package.json', '.\package-lock.json') | ForEach-Object {
            $content = Get-Content -Path $_ | ConvertFrom-Json
            $content.version = $ProjectVersion
            $content | ConvertTo-Json -Depth 10 | Set-Content -Path $_ -Force | Out-Null
        }
    }

    # Create junction for node_modules.
    # Improve lifetime of the SSD.
    # Improve the speed of the Build Job.
    if (-not $FreshNpmModules -and
        -not (Test-Path '.\node_modules') -and
        (Test-Path $Private:CommonNpmModulesPath)) {
        'cmd.exe' | Invoke-GooNativeCommand -CommandArgs @('/c', 'mklink', '/J', '.\node_modules', $Private:CommonNpmModulesPath)
    }

    # Build Client
    'npm' | Invoke-GooNativeCommand -CommandArgs @('install') -Verbose
    'npm' | Invoke-GooNativeCommand -CommandArgs @('run', 'build', '--production') -Verbose

    # Zip Client's Artifacts
    Compress-Archive -Path ".\build\*" -DestinationPath ".\client-$ProjectVersion.zip" -CompressionLevel Fastest -Force
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
