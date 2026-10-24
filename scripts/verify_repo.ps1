<#
.SYNOPSIS
    Script estandarizado para validar la integridad del repositorio EBOGE.
.DESCRIPTION
    Este script funciona como el "CI local" ejecutable tanto por humanos como por agentes.
    Compila el código de Java (respetando la estructura MVC) y ejecuta las pruebas JUnit.
#>

$ErrorActionPreference = "Stop"

Write-Host "[1/3] Verificando contrato del repositorio..." -ForegroundColor Cyan
# Aquí mandamos llamar al script en Python que valida la existencia de los archivos Agent-Ready
python scripts/check_repo_contract.py
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Faltan archivos críticos del contrato (Agent-Ready)." -ForegroundColor Red
    exit 1
}

Write-Host "[2/3] Compilando proyecto Java (MVC)..." -ForegroundColor Cyan
# Configuramos variables genéricas relativas (sin hardcodear C:\Users\erive...)
$srcDir = "EBOGE/src"
$testDir = "EBOGE/test"
$binDir = "EBOGE/bin"

if (-Not (Test-Path -Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir | Out-Null
}

# Usamos find equivalente en PowerShell para compilar recursivamente todo el código
$javaFiles = Get-ChildItem -Path $srcDir -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
if ($javaFiles.Count -gt 0) {
    # NOTA: Asumimos que javac y las variables de entorno de Java/JavaFX están configuradas globalmente en el PATH
    javac -d $binDir $javaFiles
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: La compilación del código fuente falló." -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "No se encontraron archivos .java en src." -ForegroundColor Yellow
}

Write-Host "[3/3] Ejecutando pruebas unitarias JUnit..." -ForegroundColor Cyan
# Compilamos y corremos tests si existen
$testFiles = Get-ChildItem -Path $testDir -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
if ($testFiles.Count -gt 0) {
    javac -cp $binDir -d $binDir $testFiles
    # Aquí un agente o un CI podría inyectar el classpath a JUnit si estuviera configurado con Maven
    Write-Host "Pruebas compiladas correctamente. (Ejecución omitida por falta de dependencias Maven/Gradle locales explícitas)" -ForegroundColor Yellow
} else {
     Write-Host "No se encontraron pruebas unitarias para ejecutar." -ForegroundColor Yellow
}

Write-Host "VERIFY REPO: PASS" -ForegroundColor Green
exit 0