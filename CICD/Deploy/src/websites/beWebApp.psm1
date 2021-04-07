
function Mount-bsBackendWebApp {
    [CmdletBinding()]
    [OutputType([Microsoft.Azure.Commands.WebApps.Models.PSSite])]
    [Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSAvoidUsingPlainTextForPassword', '')]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [hashtable]
        $BEWebAppConfig,

        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.ResourceManager.Cmdlets.SdkModels.PSResourceGroup]
        $ResourceGroup,
        
        [Parameter(Mandatory = $true)]
        [Microsoft.Azure.Commands.WebApps.Models.WebApp.PSAppServicePlan]
        $AppServicePlan,

        [Parameter(Mandatory = $true)]
        [string]
        $ACRUsername,

        [Parameter(Mandatory = $true)]
        [string]
        $BranchName,

        [Parameter(Mandatory = $true)]
        [string]
        $Tag,
    
        [Parameter(Mandatory = $true)]
        [string]
        $MySqlRootPassword,
    
        [Parameter(Mandatory = $true)]
        [string]
        $MySqlUsername,
    
        [Parameter(Mandatory = $true)]
        [string]
        $MySqlPassword
    )
    
    New-GooLogMessage 'Backend WebApp Management' -Step | Write-GooLog

    'Initializing App Settings, Connection Strings and Docker-Compose Placeholders' | Write-GooLog
    @($BEWebAppConfig.AppSettings, $BEWebAppConfig.ConnectionStrings, $BEWebAppConfig.DockerCompose.Placeholders) | ForEach-Object {
        $_ | ForEach-Object {
            if (-not $_.Value) {
                $_.Value = $_.Expression | Invoke-Expression
            }
        }
    }

    'Replacing placeholders in the docker-compose file' | Write-GooLog
    $dockerComposePath = ("$PSScriptRoot$($BEWebAppConfig.DockerCompose.Path)" | Resolve-Path).Path
    $content = (Get-Content -Path $dockerComposePath) -join [System.Environment]::NewLine
    $BEWebAppConfig.DockerCompose.Placeholders | ForEach-Object {
        $content.Replace($_.Name, $_.Value)
    }

    $content | Set-Content -Path $dockerComposePath -Force | Out-Null

    return $BEWebAppConfig | Mount-bsWebApp -ResourceGroup $ResourceGroup -AppServicePlan $AppServicePlan
}
