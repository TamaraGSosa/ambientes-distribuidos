# Genera un certificado TLS autofirmado (X.509) y su clave privada RSA-2048 para desarrollo local.
# Uso desde TP9/certificados o desde la raíz de TP9:
#   .\generar-certificados.ps1

$ErrorActionPreference = "Stop"

$certsDir = Join-Path $PSScriptRoot "..\servidor\certs"
New-Item -ItemType Directory -Force -Path $certsDir | Out-Null

$keyPath = Join-Path $certsDir "server.key"
$crtPath = Join-Path $certsDir "server.crt"

$opensslCmd = Get-Command openssl -ErrorAction SilentlyContinue
if ($opensslCmd) {
    $openssl = $opensslCmd.Source
} elseif (Test-Path "C:\Program Files\Git\mingw64\bin\openssl.exe") {
    $openssl = "C:\Program Files\Git\mingw64\bin\openssl.exe"
} elseif (Test-Path "C:\Program Files\Git\usr\bin\openssl.exe") {
    $openssl = "C:\Program Files\Git\usr\bin\openssl.exe"
} else {
    Write-Error "No se encontró OpenSSL en el PATH ni en Git for Windows."
    exit 1
}

& $openssl req -x509 -newkey rsa:2048 -nodes `
    -keyout $keyPath `
    -out $crtPath `
    -days 365 `
    -subj "/C=AR/ST=Cordoba/L=Cordoba/O=AmbientesDistribuidos/OU=TP9/CN=localhost" `
    -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"

Write-Host "Certificados generados correctamente:"
Write-Host " - Clave privada: $keyPath"
Write-Host " - Certificado:   $crtPath"
