# Requires -Version 7.1.3
#Requires -Version 5.1

Import-Module "$PSScriptRoot\..\Utils.ps1" -Global -Force

@(".\package.json", ".\packageAfter.json") | Edit-JsonField -Name @('name', 'version') -Value @('test', '696969')
