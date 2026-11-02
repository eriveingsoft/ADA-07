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

# Detección dinámica y portable de JavaFX (sin hardcodear rutas absolutas de usuario)
$fxPath = $env:PATH_TO_FX
if (-not $fxPath -or -not (Test-Path $fxPath)) {
    $searchRoots = @(
        "${env:ProgramFiles}\Java",
        "${env:ProgramFiles(x86)}\Java",
        $env:JAVA_HOME,
        (Join-Path $HOME "Downloads"),
        $HOME
    ) | Where-Object { $_ -and (Test-Path $_) }

    $candidate = Get-ChildItem -Path $searchRoots -Filter "javafx-sdk*" -Recurse -Depth 4 -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($candidate) {
        $fxPath = Join-Path $candidate.FullName "lib"
    }
}
if (-not $fxPath -or -not (Test-Path $fxPath)) {
    Write-Host "JavaFX SDK no encontrado en rutas locales. Descargando OpenJFX para CI / entorno portable..." -ForegroundColor Yellow
    $toolsDir = Join-Path $PSScriptRoot "../.tools"
    if (-not (Test-Path $toolsDir)) { New-Item -ItemType Directory -Path $toolsDir | Out-Null }
    $fxExtract = Join-Path $toolsDir "javafx-sdk-21.0.2"
    if (-not (Test-Path $fxExtract)) {
        $fxZip = Join-Path $toolsDir "javafx.zip"
        $fxUrl = "https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_windows-x64_bin-sdk.zip"
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri $fxUrl -OutFile $fxZip -UseBasicParsing
        Expand-Archive -Path $fxZip -DestinationPath $toolsDir -Force
        Remove-Item $fxZip -Force -ErrorAction SilentlyContinue
    }
    $candidate = Get-ChildItem -Path $toolsDir -Filter "javafx-sdk*" -Recurse -Depth 3 -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($candidate) {
        $fxPath = Join-Path $candidate.FullName "lib"
    }
}
$fxArgs = @()
if ($fxPath -and (Test-Path $fxPath)) {
    $fxArgs = @("--module-path", $fxPath, "--add-modules", "javafx.controls,javafx.fxml,javafx.media")
}

# Usamos find equivalente en PowerShell para compilar recursivamente todo el código
$javaFiles = Get-ChildItem -Path $srcDir -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
if ($javaFiles.Count -gt 0) {
    if ($fxArgs.Count -gt 0) {
        javac $fxArgs -d $binDir $javaFiles
    } else {
        javac -d $binDir $javaFiles
    }
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: La compilación del código fuente falló." -ForegroundColor Red
        exit 1
    }
    # Normalizar casing de carpetas en bin (evitar que Controlador con mayúscula interfiera con imports de paquetes en minúscula)
    $ctrlUpper = Join-Path $binDir "Controlador"
    if (Test-Path $ctrlUpper) {
        $tempDir = Join-Path $binDir "ctrl_norm_temp"
        Rename-Item -Path $ctrlUpper -NewName "ctrl_norm_temp" -ErrorAction SilentlyContinue
        Rename-Item -Path $tempDir -NewName "controlador" -ErrorAction SilentlyContinue
    }
    # Sincronización de recursos (.fxml, .css, recursos) hacia bin (CR-02 / AC-16)
    $vistaSrc = Join-Path $srcDir "vista"
    $vistaBin = Join-Path $binDir "vista"
    $vistaBinUpper = Join-Path $binDir "Vista"
    if (Test-Path $vistaSrc) {
        if (-not (Test-Path $vistaBin)) { New-Item -ItemType Directory -Path $vistaBin | Out-Null }
        if (-not (Test-Path $vistaBinUpper)) { New-Item -ItemType Directory -Path $vistaBinUpper | Out-Null }
        Copy-Item -Path "$vistaSrc/*" -Destination $vistaBin -Recurse -Force
        Copy-Item -Path "$vistaSrc/*" -Destination $vistaBinUpper -Recurse -Force
    }
    $recSrc = "EBOGE/recursos"
    $recBin = Join-Path $binDir "recursos"
    if (Test-Path $recSrc) {
        if (-not (Test-Path $recBin)) { New-Item -ItemType Directory -Path $recBin | Out-Null }
        Copy-Item -Path "$recSrc/*" -Destination $recBin -Recurse -Force
        Copy-Item -Path "$recSrc/*" -Destination $binDir -Recurse -Force
    }
} else {
    Write-Host "No se encontraron archivos .java en src." -ForegroundColor Yellow
}

Write-Host "[3/3] Compilando pruebas unitarias JUnit..." -ForegroundColor Cyan
# Detección dinámica y portable de dependencias JUnit en repositorio local m2
$m2Repo = Join-Path $HOME ".m2/repository"
$junitJars = @()
if (Test-Path $m2Repo) {
    $junitJars = Get-ChildItem -Path $m2Repo -Recurse -Filter "*.jar" -ErrorAction SilentlyContinue |
        Where-Object { ($_.FullName -match "junit|opentest4j|apiguardian") -and ($_.FullName -notmatch "sources|javadoc") } |
        Select-Object -ExpandProperty FullName
}
$sep = [System.IO.Path]::PathSeparator
if ($junitJars.Count -eq 0) {
    Write-Host "JUnit no encontrado en .m2. Descargando JAR standalone de JUnit para CI / entorno portable..." -ForegroundColor Yellow
    $toolsDir = Join-Path $PSScriptRoot "../.tools"
    if (-not (Test-Path $toolsDir)) { New-Item -ItemType Directory -Path $toolsDir | Out-Null }
    $junitStandalone = Join-Path $toolsDir "junit-platform-console-standalone-1.10.2.jar"
    if (-not (Test-Path $junitStandalone)) {
        $junitUrl = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar"
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri $junitUrl -OutFile $junitStandalone -UseBasicParsing
    }
    $junitJars = @($junitStandalone)
}
$cp = if ($junitJars.Count -gt 0) { "$binDir$sep" + ($junitJars -join $sep) } else { $binDir }

$testFiles = Get-ChildItem -Path $testDir -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
if ($testFiles.Count -gt 0) {
    if ($fxArgs.Count -gt 0) {
        javac $fxArgs -cp $cp -d $binDir $testFiles
    } else {
        javac -cp $cp -d $binDir $testFiles
    }
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: La compilación de pruebas unitarias falló." -ForegroundColor Red
        exit 1
    }
    Write-Host "Pruebas compiladas correctamente." -ForegroundColor Green

    # Ejecución de pruebas unitarias de reinicio de partida (CR-01 / RF-13)
    $testRunnerFile = Join-Path $binDir "RunRestartTests.java"
    $runnerSource = @"
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import java.io.PrintWriter;

public class RunRestartTests {
    public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(
                selectClass("controlador.ReiniciarPartidaTest"),
                selectClass("controlador.SalirPartidaTest")
            )
            .build();
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        TestExecutionSummary summary = listener.getSummary();
        summary.printTo(new PrintWriter(System.out));
        if (summary.getTotalFailureCount() > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
"@
    [System.IO.File]::WriteAllText($testRunnerFile, $runnerSource)
    if ($fxArgs.Count -gt 0) {
        javac $fxArgs -cp $cp -d $binDir $testRunnerFile
        java $fxArgs -cp $cp RunRestartTests
    } else {
        javac -cp $cp -d $binDir $testRunnerFile
        java -cp $cp RunRestartTests
    }
    Remove-Item -Path (Join-Path $binDir "RunRestartTests*") -Force -ErrorAction SilentlyContinue
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: La ejecución de pruebas unitarias falló." -ForegroundColor Red
        exit 1
    }
} else {
     Write-Host "No se encontraron pruebas unitarias para ejecutar." -ForegroundColor Yellow
}

Write-Host "VERIFY REPO: PASS" -ForegroundColor Green
exit 0