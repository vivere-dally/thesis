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
    'mvn' | Invoke-GooNativeCommand -CommandArgs @('surefire:test', '-Dtest=*UT') -Verbose

    try {
        'docker-compose' | Invoke-GooNativeCommand -CommandArgs @('-f', "./docker/docker-compose.test.yml", 'up', '-d')

        $jar = Get-Item -Path './target/*.jar' | Select-Object -ExpandProperty FullName
        $job = Start-Job { 'java' | Invoke-GooNativeCommand -CommandArgs @('-jar', $using:jar) }

        {
            Invoke-WebRequest -Method Options -Uri 'http://localhost:5000/api/swagger-ui/#/' | Out-Null
        } | Use-GooRetryHandler -Retries 32 -TimeoutSec 3 -Verbose

        'mvn' | Invoke-GooNativeCommand -CommandArgs @('failsafe:integration-test', '-DincludeFile=*IT') -Verbose
    }
    finally {
        'docker-compose' | Invoke-GooNativeCommand -CommandArgs @('-f', "./docker/docker-compose.test.yml", 'down')
        $job | Stop-Job | Remove-Job
    }

    ("$PSScriptRoot/../../frontend/client" | Resolve-Path).Path | Set-Location
    'npm' | Invoke-GooNativeCommand -CommandArgs ('run', 'citest') -Verbose

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

