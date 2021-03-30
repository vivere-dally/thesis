[CmdletBinding()]
[OutputType()]
param (
    [Parameter(Mandatory = $true)]
    [string]
    $ProjectVersion
)

#Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core
#Requires -Module @{ ModuleName = 'SemVerGoodies'; RequiredVersion = '0.2.0' }

$ErrorActionPreference = 'Stop'
Import-Module -Name "$PSScriptRoot\Utils.ps1" -Global -Force

$Private:BEPath = ("../../Server/thesis" | Resolve-Path).Path
$Private:FEPath = ("../../Client/thesis" | Resolve-Path).Path

try {
    if (-not ($ProjectVersion | Test-GooSemVer)) {
        throw "The $ProjectVersion is not following the SemVer guidelines."
    }

    'git' | Invoke-NativeCommand -CommandArgs @('checkout', 'main')
    $currentGitBranch = 'git' | Invoke-NativeCommand -CommandArgs @('branch', '--show-current') -NoLogs
    if ('main' -ne $currentGitBranch) {
        throw "Cannot TAG any branch besides main."
    }

    # Discard build metadata
    $ProjectVersion = $ProjectVersion | Reset-GooSemVer -Identifier Buildmetadata

    # Update Backend's Version and stage
    Set-Location -Path $Private:BEPath
    'mvn' | Invoke-NativeCommand -CommandArgs @('versions:set', "-DnewVersion=$ProjectVersion")
    'git' | Invoke-NativeCommand -CommandArgs @('add', '.\pom.xml')

    # Update Frontend's Version and stage
    Set-Location -Path $Private:FEPath
    @('.\package.json', '.\packageAfter.json') | Edit-JsonField -Name 'version' -Value $ProjectVersion
    'git' | Invoke-NativeCommand -CommandArgs @('add', '.\package.json')
    'git' | Invoke-NativeCommand -CommandArgs @('add', '.\package-lock.json')

    # Commit & Push changes to origin
    'git' | Invoke-NativeCommand -CommandArgs @('commit', '-m', 'JENKINS: Updated the versions after the release.')
    'git' | Invoke-NativeCommand -CommandArgs @('push', 'origin')

    # Create tag
    $tagName = "v$ProjectVersion"
    'git' | Invoke-NativeCommand -CommandArgs @('tag', '-a', $tagName, '-m', 'JENKINS: Created a new tag after the release.')
    'git' | Invoke-NativeCommand -CommandArgs @('push', 'origin', $tagName)

    exit 0
}
catch {
    $_
    exit 1
}
finally {
    Set-Location -Path $PSScriptRoot
}
