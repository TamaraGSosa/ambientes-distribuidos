param(
    [string]$Label = "carga",
    [int]$DurationSeconds = 130,
    [int]$IntervalSeconds = 5
)

$outDir = Join-Path $PSScriptRoot "resultados"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null
$csvPath = Join-Path $outDir "monitor-$Label.csv"

"timestamp,node_cpu_s,node_ram_mb,primary_cpu,primary_mem,replica_cpu,replica_mem" | Out-File -FilePath $csvPath -Encoding utf8

$endTime = (Get-Date).AddSeconds($DurationSeconds)
while ((Get-Date) -lt $endTime) {
    $ts = (Get-Date).ToString("yyyy-MM-dd HH:mm:ss")

    $nodeProc = Get-Process node -ErrorAction SilentlyContinue | Sort-Object WorkingSet64 -Descending | Select-Object -First 1
    $nodeCpu = if ($nodeProc) { [math]::Round($nodeProc.CPU, 2) } else { 0 }
    $nodeRamMb = if ($nodeProc) { [math]::Round($nodeProc.WorkingSet64 / 1MB, 2) } else { 0 }

    $pCpu = "N/A"; $pMem = "N/A"; $rCpu = "N/A"; $rMem = "N/A"
    $stats = docker stats tp8-postgres-primary tp8-postgres-replica --no-stream --format "{{.Name}}|{{.CPUPerc}}|{{.MemUsage}}" 2>$null
    foreach ($line in $stats) {
        $parts = $line -split "\|"
        if ($parts.Length -eq 3) {
            if ($parts[0] -eq "tp8-postgres-primary") { $pCpu = $parts[1]; $pMem = $parts[2] }
            if ($parts[0] -eq "tp8-postgres-replica") { $rCpu = $parts[1]; $rMem = $parts[2] }
        }
    }

    "$ts,$nodeCpu,$nodeRamMb,`"$pCpu`",`"$pMem`",`"$rCpu`",`"$rMem`"" | Out-File -FilePath $csvPath -Append -Encoding utf8
    Start-Sleep -Seconds $IntervalSeconds
}

Write-Host "Monitoreo finalizado: $csvPath"
