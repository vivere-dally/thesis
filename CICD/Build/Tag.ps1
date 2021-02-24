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
    $ProjectVersion
)

#Requires -Module @{ ModuleName = 'SemVerPs'; RequiredVersion = '1.0' }
Import-Module -Name "$PSScriptRoot\Utils.ps1" -Global -Force

try {
    if (-not (Test-SemVer -InputObject $ProjectVersion)) {
        throw "The $ProjectVersion is not following the SemVer guidelines."
    }

    $currentGitBranch = Invoke-NativeCommand -Command 'git' -CommandArgs @('branch', '--show-current') -NoLogs
    if ('main' -eq $currentGitBranch) {
        $ProjectVersion = $ProjectVersion | ConvertTo-SemVer
        $ProjectVersion = $ProjectVersion.Change($ProjectVersion.Major, $ProjectVersion.Minor, $ProjectVersion.Patch, $ProjectVersion.Prerelease, '')  # Discard build metadata

        # Update Backend Version
        Set-Location -Path $BackendAbsolutePath
        Invoke-NativeCommand -Command 'mvn' -CommandArgs @('versions:set', "-DnewVersion=$ProjectVersion")
        Invoke-NativeCommand -Command 'git' -CommandArgs @('add', '.\pom.xml')

        # Update Frontend Version
        Set-Location $FrontendAbsolutePath
        $packageJson = Get-Content -Path ".\package.json" | ConvertFrom-Json
        $packageJson.version = $ProjectVersion
        $packageJson | ConvertTo-Json -Depth 5 | Set-Content -Path ".\package.json" -Force
        Invoke-NativeCommand -Command 'git' -CommandArgs @('add', '.\package.xml')

        # Push changes to origin
        Invoke-NativeCommand -Command 'git' -CommandArgs @('commit', '-m', 'JENKINS: Updated the versions after the release.')
        Invoke-NativeCommand -Command 'git' -CommandArgs @('push', 'origin')

        # Create tag
        $tagName = "v$($ProjectVersion.ToString())"
        Invoke-NativeCommand -Command 'git' -CommandArgs @('tag', '-a', $tagName, '-m', 'JENKINS: Created a new tag after the release.')
        Invoke-NativeCommand -Command 'git' -CommandArgs @('push', 'origin', $tagName)
    }

    exit 0
}
catch {
    $_
    exit 1
}
