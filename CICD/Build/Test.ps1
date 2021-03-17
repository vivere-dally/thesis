[CmdletBinding()]
[OutputType()]
param ()

#Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core

$ErrorActionPreference = 'Stop'
Import-Module -Name "$PSScriptRoot\Utils.ps1" -Global -Force

$Private:CurrentDir = $PSScriptRoot
$Private:BEPath = ("../../Server/thesis" | Resolve-Path).Path
$Private:FEPath = ("../../Client/thesis" | Resolve-Path).Path

try {
    Set-Location -Path $Private:BEPath
    'mvn' | Invoke-NativeCommand -CommandArgs @('test')

    Set-Location -Path $Private:FEPath
    # TODO npm tests
    
    exit 0
}
catch {
    $_
    exit 1
}
finally {
    Set-Location -Path $Private:CurrentDir
}

