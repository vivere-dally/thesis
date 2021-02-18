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
    $BuildMetadata = 'local',

    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [ValidateSet('', 'alpha', 'beta', 'rc')]
    [string]
    $PreReleaseTag = '',

    [Parameter(Mandatory = $false)]
    [AllowEmptyString()]
    [ValidateSet('', 'Patch', 'Minor', 'Major')]
    [string]
    $ReleaseType = ''

)

Import-Module -Name @(
    "$PSScriptRoot\Utils.ps1"
) -Global -Force

try {
    Set-Location -Path $BackendAbsolutePath
    $ProjectVersion = Invoke-NativeCommand -Command 'mvn' -CommandArgs @('help:evaluate', '-Dexpression=project.version') | Where-Object { -not $_.StartsWith('[') }
    if ($ReleaseType) {
        $ProjectVersion = $ProjectVersion.TrimEnd('-SNAPSHOT')
        $parts = $ProjectVersion.Split('.') | ForEach-Object { [int]$_ }
        switch ($ReleaseType) {
            'Patch' {
                $parts[2]++
                break
            }

            'Minor' {
                $parts[2] = 0
                $parts[1]++
                break
            }

            'Major' {
                $parts[2] = 0
                $parts[1] = 0
                $parts[0]++
                break
            }
        }

        $ProjectVersion = $parts -join '.'
    }
    
    if (-not $ReleaseType) {
        if ($PreReleaseTag) {
            $ProjectVersion = "$ProjectVersion-$PreReleaseTag"
        }

        $ProjectVersion = "$ProjectVersion+$BuildMetadata"
    }

    if ('local' -ne $BuildMetadata) {
        Invoke-NativeCommand -Command "mvn" -CommandArgs @('versions:set', "-DnewVersion=$ProjectVersion")
    }

    Invoke-NativeCommand -Command "mvn" -CommandArgs @('-B', '-DskipTests', 'clean', 'package')

    Set-Location $FrontendAbsolutePath
    Invoke-NativeCommand -Command "npm" -CommandArgs @('install')
    Invoke-NativeCommand -Command "npm" -CommandArgs @('run', 'build')
}
catch {
    $_
}
