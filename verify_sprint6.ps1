
# 1. Test: Create Valid Appointment (Monday 12:00)
$validBody = @{
    name      = "Valid User"
    email     = "valid@test.com"
    topic     = "Java Logic Test"
    startTime = "2026-06-15T12:00:00" # A Monday
} | ConvertTo-Json

Write-Output "Test 1: Create Valid Appointment..."
try {
    $res = Invoke-RestMethod -Uri "http://localhost:8081/api/termine" -Method Post -ContentType "application/json" -Body $validBody
    Write-Output "SUCCESS: Created ID $($res.id) with status $($res.status)"
    $validId = $res.id
}
catch {
    Write-Output "FAILED: $($_.Exception.Message)"
}

# 2. Test: Invalid Day (Saturday)
$invalidDayBody = @{
    name      = "Saturday User"
    email     = "sat@test.com"
    topic     = "Fail Test"
    startTime = "2026-06-13T12:00:00" # A Saturday
} | ConvertTo-Json

Write-Output "`nTest 2: Create Invalid Appointment (Saturday)..."
try {
    Invoke-RestMethod -Uri "http://localhost:8081/api/termine" -Method Post -ContentType "application/json" -Body $invalidDayBody
    Write-Output "FAILED: Should have been rejected!"
}
catch {
    Write-Output "SUCCESS: Rejected with $($_.Exception.Message)"
}

# 3. Test: Invalid Time (09:00)
$invalidTimeBody = @{
    name      = "Early User"
    email     = "early@test.com"
    topic     = "Fail Test"
    startTime = "2026-06-15T09:00:00"
} | ConvertTo-Json

Write-Output "`nTest 3: Create Invalid Appointment (09:00)..."
try {
    Invoke-RestMethod -Uri "http://localhost:8081/api/termine" -Method Post -ContentType "application/json" -Body $invalidTimeBody
    Write-Output "FAILED: Should have been rejected!"
}
catch {
    Write-Output "SUCCESS: Rejected with $($_.Exception.Message)"
}

# 4. Login as Admin
Write-Output "`nTest 4: Login Admin..."
try {
    $loginBody = @{ username = "admin"; password = "password123" } | ConvertTo-Json
    $loginRes = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method Post -ContentType "application/json" -Body $loginBody
    $token = $loginRes.token
    Write-Output "SUCCESS: Got Token"
}
catch {
    Write-Output "FATAL: Login failed"
    exit
}

# 5. Confirm Appointment
if ($validId) {
    Write-Output "`nTest 5: Confirm Appointment $validId..."
    try {
        $headers = @{ Authorization = "Bearer $token" }
        $res = Invoke-RestMethod -Uri "http://localhost:8081/api/termine/$validId/confirm" -Method Put -Headers $headers
        Write-Output "SUCCESS: Status is now $($res.status)"
    }
    catch {
        Write-Output "FAILED: $($_.Exception.Message)"
    }
}

# 6. Delete Appointment
if ($validId) {
    Write-Output "`nTest 6: Delete Appointment $validId..."
    try {
        $headers = @{ Authorization = "Bearer $token" }
        Invoke-RestMethod -Uri "http://localhost:8081/api/termine/$validId" -Method Delete -Headers $headers
        Write-Output "SUCCESS: Deleted"
    }
    catch {
        Write-Output "FAILED: $($_.Exception.Message)"
    }
}
