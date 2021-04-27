[CmdletBinding()]
[OutputType()]
param ()

#Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.2.3' }

$ErrorActionPreference = 'Stop'

$Private:BEPath = ("$PSScriptRoot/../../Server/thesis" | Resolve-Path).Path
$Private:FEPath = ("$PSScriptRoot/../../Client/thesis" | Resolve-Path).Path

try {
    Set-Location -Path $Private:BEPath
    'mvn' | Invoke-GooNativeCommand -CommandArgs @('test') -Verbose

    Set-Location -Path $Private:FEPath
    # TODO npm tests
    
    exit 0
}
catch {
    $_
    $_.ScriptStackTrace
    exit 1
}
finally {
    Set-Location -Path $PSScriptRoot
}

