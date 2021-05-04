[CmdletBinding()]
[OutputType()]
param (
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
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.2.3' }

$Global:ErrorActionPreference = 'Stop'

function Main {
    try {
        if (-not ($ProjectVersion | Test-GooSemVer)) {
            throw "The Project Version $ProjectVersion is not following the SemVer guidelines."
        }

        'git' | Invoke-GooNativeCommand -CommandArgs @('checkout', $BranchName)

        # Discard build metadata
        $ProjectVersion = $ProjectVersion | Reset-GooSemVer -Identifier Buildmetadata | Reset-GooSemVer -Identifier Buildmetadata

        Update-ServerVersion
        Update-ClientVersion

        # Commit & Push changes to origin
        'git' | Invoke-GooNativeCommand -CommandArgs @('commit', '-m', 'JENKINS: Updated the versions after the release.')
        'git' | Invoke-GooNativeCommand -CommandArgs @('push', 'origin') -Verbose

        # Create tag
        $tagName = "v$ProjectVersion"
        'git' | Invoke-GooNativeCommand -CommandArgs @('tag', '-a', $tagName, '-m', 'JENKINS: Created a new tag after the release.')
        'git' | Invoke-GooNativeCommand -CommandArgs @('push', 'origin', $tagName) -Verbose

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

function Update-ServerVersion {
    ("$PSScriptRoot/../../backend/server" | Resolve-Path).Path | Set-Location
    'mvn' | Invoke-GooNativeCommand -CommandArgs @('versions:set', "-DnewVersion=$ProjectVersion")
    'git' | Invoke-GooNativeCommand -CommandArgs @('add', '.\pom.xml')
}

function Update-ClientVersion {
    ("$PSScriptRoot/../../frontend/client" | Resolve-Path).Path | Set-Location
    @('.\package.json', '.\package-lock.json') | ForEach-Object {
        $content = Get-Content -Path $_ | ConvertFrom-Json
        $content.version = $ProjectVersion
        $content | ConvertTo-Json -Depth 10 | Set-Content -Path $_ -Force | Out-Null
    }

    'git' | Invoke-GooNativeCommand -CommandArgs @('add', '.\package.json')
    'git' | Invoke-GooNativeCommand -CommandArgs @('add', '.\package-lock.json')
}

# Entrypoint
Main
