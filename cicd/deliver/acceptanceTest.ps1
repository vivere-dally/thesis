[CmdletBinding()]
[OutputType()]
[Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSAvoidUsingPlainTextForPassword', '')]
param (
    [Parameter(Mandatory = $true)]
    [string]
    $ACRUsername,

    [Parameter(Mandatory = $true)]
    [string]
    $ACRPassword,

    [Parameter(Mandatory = $true)]
    [string]
    $Tag,

    [Parameter(Mandatory = $true)]
    [string]
    $BranchName
)

#Requires -RunAsAdministrator
#Requires -Version 7.1.3
#Requires -PSEdition Core
#Requires -Module @{ ModuleName = 'UtilsGoodies'; RequiredVersion = '0.2.3' }
#Requires -Module @{ ModuleName = 'SemVerGoodies'; RequiredVersion = '0.2.0' }

$Global:ErrorActionPreference = 'Stop'

try {
    if (-not ($Tag | Test-GooSemVer)) {
        throw "The Project Version $Tag is not following the SemVer guidelines."
    }

    # Discard build metadata
    $Tag = $Tag | Reset-GooSemVer -Identifier Buildmetadata | Reset-GooSemVer -Identifier Buildmetadata

    # Login
    'docker' | Invoke-GooNativeCommand -CommandArgs @('login', "$ACRUsername.azurecr.io", '-u', $ACRUsername, '-p', $ACRPassword)

    "$PSScriptRoot/../../automated_tests/docker" | Set-Location

    # Create ci.env file
    $envHash = @{}
    Get-Content '.env' | ForEach-Object {
        $key, $value = $_.Split('=')
        $envHash.$key = $value
    }

    $envHash.SERVER_IMAGE = "$ACRUsername.azurecr.io/$BranchName/server:$Tag"
    $envHash.CLIENT_IMAGE = "$ACRUsername.azurecr.io/$BranchName/client:$Tag"

    ($envHash.Keys | ForEach-Object { "$_=$($envHash.$_)" }) -join [System.Environment]::NewLine | Set-Content 'ci.env'

    # docker-compose up
    'docker-compose' | Invoke-GooNativeCommand -CommandArgs @('--env-file', 'ci.env', 'up', '-d')

    '../' | Set-Location

    try {
        # Wait until the server is running
        while ($true) {
            Start-Sleep -Seconds 3
            try {
                Invoke-WebRequest -Method Options -Uri 'http://localhost:5000/api/swagger-ui/#/' | Out-Null
                break
            }
            catch {}
        }

        # Run automated tests
        'mvn' | Invoke-GooNativeCommand -CommandArgs ('verify')
    }
    finally {
        './docker' | Set-Location

        # docker-compose down
        'docker-compose' | Invoke-GooNativeCommand -CommandArgs @('down')

        # Remove downloaded images
        'docker' | Invoke-GooNativeCommand -CommandArgs @('rmi', $envHash.SERVER_IMAGE)
        'docker' | Invoke-GooNativeCommand -CommandArgs @('rmi', $envHash.CLIENT_IMAGE)
    }

    exit 0
}
catch {
    $_
    $_.ScriptStackTrace
    exit 0
}
finally {
    $PSScriptRoot | Set-Location
}

