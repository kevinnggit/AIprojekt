
# 1. Login
$loginBody = @{ username = "admin"; password = "password123" } | ConvertTo-Json
try {
    $loginRes = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method Post -ContentType "application/json" -Body $loginBody
    $token = $loginRes.token
    Write-Host "Login Successful. Token starts with: $($token.Substring(0, 10))..."
}
catch {
    Write-Error "Login Failed: $($_.Exception.Message)"
    exit
}

# 2. Create Appointment
$appBody = @{
    name      = "Debug User"
    email     = "debug@test.com"
    topic     = "Debug Confirm"
    startTime = "2026-06-15T10:00:00"
} | ConvertTo-Json

try {
    $app = Invoke-RestMethod -Uri "http://localhost:8081/api/termine" -Method Post -ContentType "application/json" -Body $appBody
    $id = $app.id
    Write-Host "Created Appointment ID: $id"
}
catch {
    Write-Error "Create Failed: $($_.Exception.Message)"
    # Continue anyway if ID is not set, just to test confirm on a random ID if needed
}

# 3. Confirm Appointment
if ($id) {
    Write-Host "Attempting to Confirm ID $id..."
    try {
        $headers = @{ Authorization = "Bearer $token" }
        $confirmRes = Invoke-RestMethod -Uri "http://localhost:8081/api/termine/$id/confirm" -Method Put -Headers $headers
        Write-Host "Confirm Success: $($confirmRes | ConvertTo-Json -Depth 5)"
    }
    catch {
        Write-Error "Confirm Failed: $($_.Exception.Message)"
        Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)"
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        Write-Host "Response Body: $($reader.ReadToEnd())"
    }
}
