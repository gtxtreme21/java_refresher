param(
    [ValidateSet("all", "menu", "list")]
    [string]$Mode = "all",
    [string]$TopicKey
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Resolve-Path (Join-Path $scriptDir "..")
$srcRoot = Join-Path $repoRoot "src\main\java"
$outDir = Join-Path $repoRoot "out"

Write-Host ""
Write-Host "=== Java Lead Developer Refresher ===" -ForegroundColor Cyan
Write-Host "Repository: $repoRoot"

if (-not (Test-Path $srcRoot)) {
    throw "Source directory not found: $srcRoot"
}

if (Test-Path $outDir) {
    Write-Host "[1/3] Cleaning previous build output..." -ForegroundColor DarkGray
    Remove-Item -Path $outDir -Recurse -Force
}

Write-Host "[2/3] Compiling Java sources (release 26)..." -ForegroundColor DarkGray
New-Item -ItemType Directory -Path $outDir | Out-Null

$javaFiles = Get-ChildItem -Path $srcRoot -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
if (-not $javaFiles) {
    throw "No Java source files found under $srcRoot"
}

& javac --release 26 -d $outDir $javaFiles
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed. javac exit code: $LASTEXITCODE"
}

$appArgs = @()
if ($TopicKey) {
    $appArgs += "--topic"
    $appArgs += $TopicKey
}
else {
    switch ($Mode) {
        "all" { $appArgs += "--all" }
        "menu" { $appArgs += "--menu" }
        "list" { $appArgs += "--list" }
    }
}

Write-Host "[3/3] Running study app..." -ForegroundColor DarkGray
Write-Host ""

& java -cp $outDir com.javarefresher.StudyApp @appArgs
if ($LASTEXITCODE -ne 0) {
    throw "Application failed. java exit code: $LASTEXITCODE"
}
