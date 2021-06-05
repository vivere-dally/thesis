[CmdletBinding()]
[OutputType()]
param (
    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [string]
    $ProjectVersion
)

#Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.2.3' }

$Global:ErrorActionPreference = 'Stop'

function Main {
    try {
        Build-Server
        Build-Client
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
}

function Build-Server {
    ("$PSScriptRoot/../../backend/server" | Resolve-Path).Path | Set-Location
    # Update Server's Version
    if ($ProjectVersion) {
        'mvn' | Invoke-GooNativeCommand -CommandArgs @('versions:set', "-DnewVersion=$ProjectVersion")
    }

    # Build Server
    'mvn' | Invoke-GooNativeCommand -CommandArgs @('-B', '-DskipTests', 'clean', 'package') -Verbose
}

function Build-Client {
    ("$PSScriptRoot/../../frontend/client" | Resolve-Path).Path | Set-Location
    # Update Client's Version
    if ($ProjectVersion) {
        @('.\package.json', '.\package-lock.json') | ForEach-Object {
            $content = Get-Content -Path $_ | ConvertFrom-Json
            $content.version = $ProjectVersion
            $content | ConvertTo-Json -Depth 10 | Set-Content -Path $_ -Force | Out-Null
        }
    }

    # Build Client
    'npm' | Invoke-GooNativeCommand -CommandArgs @('install') -Verbose
    'npm' | Invoke-GooNativeCommand -CommandArgs @('run', 'build', '--production') -Verbose

    # Zip Client's Artifacts
    Compress-Archive -Path ".\build\*" -DestinationPath ".\client-$ProjectVersion.zip" -CompressionLevel Fastest -Force
}

# Entrypoint
Main
