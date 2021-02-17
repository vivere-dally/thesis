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
    $ProjectPath = "../../Server/thesis"
)

Import-Module -Name @(
    "$PSScriptRoot\Utils.ps1"
) -Global -Force

try {
    $currentPath = $PSScriptRoot
    Set-Location -Path $ProjectPath
    Invoke-NativeCommand -Command "mvn" -CommandArgs @('-B', '-DskipTests', 'clean', 'package')
    Set-Location -Path $currentPath
}
catch {
    $_
}
