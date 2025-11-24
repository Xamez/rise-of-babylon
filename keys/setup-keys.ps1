$services = @("notification", "user", "city")
$tempPriv = Join-Path $env:TEMP ("pkcs8_" + [System.Guid]::NewGuid().ToString())
$tempPub = $tempPriv + "_pub"

if (-not (Get-Command ssh-keygen -ErrorAction SilentlyContinue)) {
    Write-Error "ssh-keygen not found."
    exit 1
}

try {
    cmd /c "ssh-keygen -q -t rsa -b 2048 -m PKCS8 -N """" -f ""$tempPriv""" | Out-Null

    if (-not (Test-Path $tempPriv)) {
        Write-Error "Key generation failed."
        exit 1
    }

    cmd /c "ssh-keygen -e -m PKCS8 -f ""$tempPriv""" | Out-File -FilePath $tempPub -Encoding ASCII

    foreach ($service in $services) {
        $targetDir = Join-Path $service "src\main\resources\keys"

        if (-not (Test-Path $targetDir)) {
            New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
        }

        Copy-Item -Path $tempPriv -Destination (Join-Path $targetDir "private.pem") -Force
        Copy-Item -Path $tempPub -Destination (Join-Path $targetDir "public.pem") -Force

        Write-Host "Keys updated for: $service"
    }
}
finally {
    if (Test-Path $tempPriv) { Remove-Item $tempPriv -Force }
    if (Test-Path $tempPub) { Remove-Item $tempPub -Force }
    if (Test-Path "$tempPriv.pub") { Remove-Item "$tempPriv.pub" -Force }
}