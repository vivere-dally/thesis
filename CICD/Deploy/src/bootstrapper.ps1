[CmdletBinding()]
param ()

$result = Get-PSCallStack | Where-Object { $_.Command -match 'deploy.ps1' }
if (-not $result) {
  throw 'This script must be called from ./deploy.ps1'
}

$modules = Get-ChildItem -Path $PSScriptRoot -Filter '*.psm1' -Recurse | Select-Object -ExpandProperty FullName
Import-Module -Name $modules -Global -Force

$config = @{}
Get-ChildItem -Path $PSScriptRoot -Filter '*.json' -Recurse | ForEach-Object {
  $config.($_.BaseName) = Get-Content -Path $_.FullName | ConvertFrom-Json -AsHashtable
}

return $config
