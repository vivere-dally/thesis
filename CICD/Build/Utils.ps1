function Invoke-NativeCommand {
    <#
    .SYNOPSIS
        Invoke a command
    .DESCRIPTION
        This cmdlet uses the Call Operator to run a command.
        If the $LASTEXITCODE automatic variable is different than 0, an error is thrown.
    .EXAMPLE
        PS C:\> Invoke-NativeCommand "cmd.exe" @("/c", "exit 1")
    #>
    [CmdletBinding()]
    [OutputType()]
    param (
        [Parameter(Mandatory = $true, ValueFromPipeline = $true, Position = 0)]
        [string]
        $Command,
        
        [Parameter(Mandatory = $false, Position = 1)]
        [array]
        $CommandArgs = @(),

        [Parameter(Mandatory = $false, Position = 2)]
        [switch]
        $NoLogs
    )

    $stopWatch = [Diagnostics.Stopwatch]::StartNew()
    & $Command $CommandArgs
    $stopWatch.Stop()

    $formattedCommandArgs = $commandArgs | ForEach-Object { "[$_]" }
    $formattedCommandArgs = $formattedCommandArgs -join ' '
    if (-not $NoLogs) {
        $totalTime = "{0:n3}" -f $stopWatch.Elapsed.TotalSeconds
        Write-Host @"
Command: [$command]
CommandArgs: $formattedCommandArgs
Exit Code: $LASTEXITCODE
Total time: $totalTime s
"@
    }

    if ($LASTEXITCODE -ne 0) {
        throw 'Failure'
    }
}

function Edit-JsonField {
    [CmdletBinding()]
    [OutputType()]
    param (
        [Parameter(Mandatory = $true, ValueFromPipeline = $true, Position = 0)]
        [string]
        $Path,

        [Parameter(Mandatory = $true, Position = 1)]
        [string[]]
        $Name,

        [Parameter(Mandatory = $true, Position = 2)]
        [PSCustomObject[]]
        $Value
    )

    begin {
        if ($Name.Count -ne $Value.Count) {
            throw 'Name and Value must have the same cardinality.'
        }
    }

    process {
        if (-not (Test-Path -Path $Path)) {
            throw 'Invalid path'
        }

        $content = Get-Content -Path $Path | ConvertFrom-Json
        for ($i = 0; $i -lt $Name.Count; $i++) {
            $content.($Name[$i]) = $Value[$i]
        }

        $content | ConvertTo-Json -Depth 10 | Set-Content -Path $Path -Force | Out-Null
    }
}
