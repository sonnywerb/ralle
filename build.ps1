$ErrorActionPreference = "Stop"

$SRC     = "src\main"
$OUT     = "build\classes"
$JAR     = "build\Ralle.jar"
$RUNTIME = "build\runtime"
$DIST    = "build\dist"

Write-Host "==> Cleaning build directory..." -ForegroundColor Cyan
if (Test-Path "build") { Remove-Item -Recurse -Force "build" }
New-Item -ItemType Directory -Force -Path $OUT | Out-Null

Write-Host "==> Compiling..." -ForegroundColor Cyan
javac -d $OUT "$SRC\Ralle.java"

Write-Host "==> Packaging JAR..." -ForegroundColor Cyan
# Copy resources into the classes output so they end up in the JAR
Copy-Item -Recurse -Force "$SRC\resources\*" $OUT
jar --create --file $JAR --main-class main.Ralle -C $OUT .

Write-Host "==> Detecting required modules..." -ForegroundColor Cyan
$modules = (jdeps --ignore-missing-deps --print-module-deps $JAR).Trim()
Write-Host "    Modules: $modules"

Write-Host "==> Creating minimal JRE with jlink..." -ForegroundColor Cyan
jlink `
    --add-modules $modules `
    --output $RUNTIME `
    --strip-debug `
    --no-header-files `
    --no-man-pages `
    --compress=2

Write-Host "==> Runtime size: $('{0:N1} MB' -f ((Get-ChildItem $RUNTIME -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB))"

Write-Host "==> Creating installer with jpackage..." -ForegroundColor Cyan
jpackage `
    --type exe `
    --name RALL-E `
    --app-version "1.0.1" `
    --input build `
    --main-jar Ralle.jar `
    --main-class main.Ralle `
    --runtime-image $RUNTIME `
    --icon "$SRC\resources\icon.ico" `
    --dest $DIST `
    --win-per-user-install `
    --win-shortcut `
    --win-shortcut-prompt `
    --win-menu

Write-Host "==> Done! Installer: $DIST" -ForegroundColor Green
Get-ChildItem $DIST
