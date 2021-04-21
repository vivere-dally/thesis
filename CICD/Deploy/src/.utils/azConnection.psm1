function Connect-bsAzAccount {
    [CmdletBinding()]
    [OutputType([string])]
    param (
        [Parameter(Mandatory = $true, Position = 0)]
        [string]
        $SubscriptionId,

        [Parameter(Mandatory = $true, Position = 1)]
        [string]
        $TenantId,

        [Parameter(Mandatory = $true, Position = 2)]
        [string]
        $ClientId,

        [Parameter(Mandatory = $true, Position = 3)]
        [securestring]
        $ClientSecret
    )

    $credential = [System.Management.Automation.PSCredential]::new($ClientId, $ClientSecret)
    $contextName = (New-Guid).Guid.ToString()
    $result = Connect-AzAccount -SubscriptionId $SubscriptionId -TenantId $TenantId -Credential $credential -ContextName $contextName -Scope Process -ServicePrincipal
    "User $($result.Context.Account.Id) connected to the subscription $($result.Context.Subscription.Name) - $($result.Context.Subscription.Id)" | Write-GooLog -ForegroundColor DarkBlue
    return $contextName
}

function Disconnect-bsAzAccount {
    [CmdletBinding()]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [string]
        $ContextName
    )

    $result = Disconnect-AzAccount -ContextName $ContextName
    "User $($result.Id) disconnected" | Write-GooLog -ForegroundColor DarkBlue
}

function Connect-bsAzCLIAccount {
    [CmdletBinding()]
    [OutputType([string])]
    param (
        [Parameter(Mandatory = $true, Position = 0)]
        [string]
        $SubscriptionId,

        [Parameter(Mandatory = $true, Position = 1)]
        [string]
        $TenantId,

        [Parameter(Mandatory = $true, Position = 2)]
        [string]
        $ClientId,

        [Parameter(Mandatory = $true, Position = 3)]
        [string]
        $ClientSecret
    )

    $result = 'az' | Invoke-GooNativeCommand -CommandArgs @(
        'login',
        '-u', $ClientId,
        '-p', $ClientSecret,
        '-t', $TenantId,
        '--service-principal'
    ) | ConvertFrom-Json -AsHashtable | Select-Object -First 1

    'az' | Invoke-GooNativeCommand -CommandArgs @('account', 'set', '-s', $SubscriptionId)
    "User $($result.user.name) connected to the subscription $($result.name) via az cli" | Write-GooLog -ForegroundColor DarkBlue
}

function Disconnect-bsAzCLIAccount {
    [CmdletBinding()]
    param (
        [Parameter(Mandatory = $true, Position = 0, ValueFromPipeline = $true)]
        [string]
        $ClientId
    )

    'az' | Invoke-GooNativeCommand -CommandArgs @('logout', '--username', $ClientId)
    "User $($ClientId) disconnected via az cli" | Write-GooLog -ForegroundColor DarkBlue
}
