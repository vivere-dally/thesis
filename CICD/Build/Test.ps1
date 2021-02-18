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
    $FrontendAbsolutePath = ("../../Client/thesis" | Resolve-Path).Path
)

Import-Module -Name @(
    "$PSScriptRoot\Utils.ps1"
) -Global -Force

try {
    Set-Location -Path $BackendAbsolutePath
    Invoke-NativeCommand -Command "mvn" -CommandArgs @('test')

    Set-Location -Path $FrontendAbsolutePath
    # TODO npm tests
}
catch {
    $_
}

