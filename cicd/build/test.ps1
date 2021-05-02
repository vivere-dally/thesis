[CmdletBinding()]
[OutputType()]
param ()

#Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.2.3' }

$Global:ErrorActionPreference = 'Stop'

try {
    ("$PSScriptRoot/../../backend/server" | Resolve-Path).Path | Set-Location
    'mvn' | Invoke-GooNativeCommand -CommandArgs @('test') -Verbose

    ("$PSScriptRoot/../../frontend/client" | Resolve-Path).Path | Set-Location
    # TODO client tests

    ("$PSScriptRoot/../../backend/frontend_config_provider" | Resolve-Path).Path | Set-Location
    # TODO frontend_config_provider tests

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

