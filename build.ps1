$ErrorActionPreference = "Stop"

$SRC = "src\main"
$OUT = "build\classes"
$JAR = "build\Ralle.jar"
$RUNTIME = "build\runtime"
$DIST = "build\dist"

Write-Host "==> Cleaning build directory..." -ForegroundColor Cyan
if (Test-Path "build")
{
    Remove-Item -Recurse -Force "build"
}
New-Item -ItemType Directory -Force -Path $OUT | Out-Null

Write-Host "==> Compiling..." -ForegroundColor Cyan
javac -d $OUT -cp "lib\jna-5.14.0.jar;lib\jna-platform-5.14.0.jar" "$SRC\*.java"

Write-Host "==> Packaging JAR..." -ForegroundColor Cyan
# Copy resources into the classes output so they end up in the JAR
Copy-Item -Recurse -Force "$SRC\resources\*" $OUT

# Merge JNA's classes into the output so they end up bundled in the single JAR
$RepoRoot = Get-Location
Push-Location $OUT
jar --extract --file (Join-Path $RepoRoot "lib\jna-5.14.0.jar")
jar --extract --file (Join-Path $RepoRoot "lib\jna-platform-5.14.0.jar")
Pop-Location

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

Write-Host "==> Runtime size: $( '{0:N1} MB' -f ((Get-ChildItem $RUNTIME -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB) )"

Write-Host "==> Creating installer with jpackage..." -ForegroundColor Cyan
jpackage `
    --type exe `
    --name Ralle `
    --app-version "1.2.1" `
    --vendor "EJC" `
    --win-upgrade-uuid "a02f3a4b-aa50-4a48-bf46-8d19fa8e07c4" `
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
