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
$pythonUtilsPath = "$PSScriptRoot\Utils.py"

try {
    if (-not (Test-SemVer -InputObject $ProjectVersion)) {
        throw "The $ProjectVersion is not following the SemVer guidelines."
    }

    $currentGitBranch = Invoke-NativeCommand -Command 'git' -CommandArgs @('branch', '--show-current') -NoLogs
    Invoke-NativeCommand -Command 'git' -CommandArgs @('config', '--get', 'remote.origin.url')
    if ('main' -eq $currentGitBranch) {
        $version = $ProjectVersion | ConvertTo-SemVer
        $version = $version.Change($version.Major, $version.Minor, $version.Patch, $version.Prerelease, '').ToString() # Discard build metadata

        # Update Backend Version
        Set-Location -Path $BackendAbsolutePath
        Invoke-NativeCommand -Command 'mvn' -CommandArgs @('versions:set', "-DnewVersion=$version")
        Invoke-NativeCommand -Command 'git' -CommandArgs @('add', '.\pom.xml')

        # Update Frontend Version
        Set-Location $FrontendAbsolutePath
        Invoke-NativeCommand -Command 'python' -CommandArgs @($pythonUtilsPath, 'update_package_json_file', ('.\package.json' | Resolve-Path).Path, $version)
        Invoke-NativeCommand -Command 'git' -CommandArgs @('add', '.\package.json')

        # Push changes to origin
        Invoke-NativeCommand -Command 'git' -CommandArgs @('commit', '-m', 'JENKINS: Updated the versions after the release.')
        Invoke-NativeCommand -Command 'git' -CommandArgs @('push', 'origin')

        # Create tag
        $tagName = "v$($version.ToString())"
        Invoke-NativeCommand -Command 'git' -CommandArgs @('tag', '-a', $tagName, '-m', 'JENKINS: Created a new tag after the release.')
        Invoke-NativeCommand -Command 'git' -CommandArgs @('push', 'origin', $tagName)
    }

    exit 0
}
catch {
    $_
    exit 1
}
