[CmdletBinding()]
[OutputType()]
param (
    [Parameter(Mandatory = $false, Position = 0, ValueFromPipeline = $true)]
    [string]
    $ResourceGroupName
)

function main {
    $CREDS = @{
        username = 'autoadmin';
        password = 'autoadmin';
    }
    $JSON_CREDS = $creds | ConvertTo-Json

    $URI = if ($ResourceGroupName) {
        "https://${ResourceGroupName}api.azurewebsites.net/api"
    }
    else {
        'http://localhost:5000/api'
    }

    $SIGNUP_URI = "$uri/signup"
    $LOGIN_URI = "$uri/login"

    # Signup
    Invoke-WebRequest -Uri $SIGNUP_URI -Method Post -Body $JSON_CREDS -ContentType 'application/json'

    # Login
    $response = Invoke-WebRequest -Uri $LOGIN_URI -Method Post -Body $JSON_CREDS -ContentType 'application/json'

    # Get data
    $userId = ($response.Content | ConvertFrom-Json).id
    $ACCOUNT_URI = "$URI/user/$userId/account"

    $accessToken = $response.Headers.accessToken
    $tokenType = $response.Headers.tokenType
    $TOKEN = "$tokenType $accessToken"

    $NO_ACCOUNTS = 5
    $NO_TRANSACTIONS = 200
    for ($i = 0; $i -lt $NO_ACCOUNTS; $i++) {
        $response = Invoke-WebRequest -Uri $ACCOUNT_URI -Method Post -Body (@{
                currency      = Get-RandomCurrencyType;
                money         = Get-RandomMoney;
                monthlyIncome = Get-RandomMoney;
            } | ConvertTo-Json) -ContentType 'application/json' -Headers @{Authorization = $TOKEN }
        $accountId = ($response.Content | ConvertFrom-Json).id
        $TRANSACTION_URI = "$URI/user/$userId/account/$accountId/transaction"
        for ($j = 0; $j -lt $NO_TRANSACTIONS; $j++) {
            try {
                $response = Invoke-WebRequest -Uri $TRANSACTION_URI -Method Post -Body (@{
                        message = "Transaction $j";
                        type    = Get-RandomTransactionType;
                        value   = Get-RandomMoney;
                        date    = Get-RandomDate;
                    } | ConvertTo-Json) -ContentType 'application/json' -Headers @{Authorization = $TOKEN }
                Write-Host "`r Account $i / Transaction $j"
            }
            catch {
                # Might fail if the account funds are insufficient;
            }
        }
    }
}

function Get-RandomMoney {
    $integralPart = Get-Random -Minimum 0 -Maximum 10000
    $fractionalPart = Get-Random -Minimum 1 -Maximum 100
    return "$integralPart.$fractionalPart"
}

function Get-RandomCurrencyType {
    $currencyType = 'RON', 'EUR', 'USD';
    return $currencyType[(Get-Random -Minimum 0 -Maximum $currencyType.Count)]
}

function Get-RandomTransactionType {
    $transactionType = 'INCOME', 'EXPENSE';
    return $transactionType[(Get-Random -Minimum 0 -Maximum $transactionType.Count)]
}

function Get-RandomDate {
    $date = Get-Date
    $millis = Get-Random -Minimum 0 -Maximum 31556952000 # 1 year in milliseconds
    $date = $date.AddMilliseconds(-$millis)
    return $date.ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ')
}

try {
    main
}
catch {
    $_
}
