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
$Private:BEPath = ("$PSScriptRoot/../../Server/thesis" | Resolve-Path).Path
$Private:FEPath = ("$PSScriptRoot/../../Client/thesis" | Resolve-Path).Path

try {
    Set-Location -Path $Private:BEPath
    # Update Backend's Version
    if ($ProjectVersion) {
        'mvn' | Invoke-GooNativeCommand -CommandArgs @('versions:set', "-DnewVersion=$ProjectVersion")
    }

    # Build Backend
    'mvn' | Invoke-GooNativeCommand -CommandArgs @('-B', '-DskipTests', 'clean', 'package') -Verbose

    Set-Location $Private:FEPath
    # Update Frontend's Version
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

    # Build Frontend
    'npm' | Invoke-GooNativeCommand -CommandArgs @('install') -Verbose
    'npm' | Invoke-GooNativeCommand -CommandArgs @('run', 'build') -Verbose

    # Zip Frontend's Artifacts
    Compress-Archive -Path ".\build\*" -DestinationPath ".\thesis-$ProjectVersion.zip" -CompressionLevel Fastest -Force
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
