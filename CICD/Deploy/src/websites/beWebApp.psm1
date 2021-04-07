
function Mount-bsBackendWebApp {
    [CmdletBinding()]
    [OutputType([Microsoft.Azure.Commands.WebApps.Models.PSSite])]
    [Diagnostics.CodeAnalysis.SuppressMessageAttribute('PSAvoidUsingPlainTextForPassword', '')]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [hashtable]
        $WAConfig,

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
        $ACRPassword,

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
    @($WAConfig.AppSettings, $WAConfig.ConnectionStrings, $WAConfig.DockerCompose.Placeholders) | ForEach-Object {
        $_ | ForEach-Object {
            if (-not $_.Value) {
                $_.Value = $_.Expression | Invoke-Expression
            }
        }
    }

    'Replacing placeholders in the docker-compose file' | Write-GooLog
    $dockerComposePath = ("$PSScriptRoot$($WAConfig.DockerCompose.Path)" | Resolve-Path).Path
    $content = (Get-Content -Path $dockerComposePath) -join [System.Environment]::NewLine
    $WAConfig.DockerCompose.Placeholders | ForEach-Object {
        $content = $content.Replace($_.Name, $_.Value)
    }

    $content | Set-Content -Path $dockerComposePath -Force | Out-Null

    return $WAConfig | Mount-bsWebApp -ResourceGroup $ResourceGroup -AppServicePlan $AppServicePlan -ACRUsername $ACRUsername -ACRPassword $ACRPassword
}
