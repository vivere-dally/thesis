
function Format-bsAzResourceUpdate {
  [CmdletBinding()]
  [OutputType([string])]
  param (
    [Parameter(Mandatory = $true, Position = 0)]
    [psobject]
    $Old,

    [Parameter(Mandatory = $true, Position = 1)]
    [psobject]
    $New,

    [Parameter(Mandatory = $false)]
    [string[]]
    $Ignore = @()
  )

  $Old = $Old | ConvertTo-GooFlattenHashtable -Depth 5
  $New = $New | ConvertTo-GooFlattenHashtable -Depth 5

  $output = ($Old.Keys + $New.Keys) | Sort-Object -Unique | ForEach-Object {
    $oldValue = ($Old.ContainsKey($_)) ? $Old.$_ : 'N/A'
    $newValue = ($New.ContainsKey($_)) ? $New.$_ : 'N/A'
    if ($oldValue -ne $newValue -and $_ -notin $Ignore) {      
      [PSCustomObject]@{
        Property = $_;
        '-|'     = '||';
        OldValue = $oldValue;
        '|-'     = '||';
        NewValue = $newValue;
      }
    }
  }

  return $output | Format-Table -AutoSize -Wrap | Out-String
}
