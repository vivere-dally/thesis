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
        [Parameter(Mandatory = $true)]
        [string]
        $Command,
        
        [Parameter(Mandatory = $false)]
        [array]
        $CommandArgs = @(),

        [Parameter(Mandatory = $false)]
        [switch]
        $NoLogs
    )

    & $Command $CommandArgs

    $formattedCommandArgs = $commandArgs | ForEach-Object { "[$_]" }
    $formattedCommandArgs = $formattedCommandArgs -join ' '
    if (-not $NoLogs) {
        Write-Host @"
Command: [$command]
CommandArgs: $formattedCommandArgs
Exit Code: $LASTEXITCODE
"@
    }

    if ($LASTEXITCODE -ne 0) {
        throw 'Failure'
    }
}