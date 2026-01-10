try {
    $body = @{
        username = "admin"
        password = "password123"
        role = "ROLE_ADMIN"
    } | ConvertTo-Json

    Write-Output "Sending Body: $body"

    $response = Invoke-WebRequest -Uri "http://localhost:8081/api/auth/register" -Method Post -ContentType "application/json" -Body $body
    Write-Output "Success: $($response.StatusCode)"
    Write-Output $response.Content
} catch {
    Write-Output ("Error Status: " + $_.Exception.Response.StatusCode)
    $stream = $_.Exception.Response.GetResponseStream()
    if ($stream) {
        $reader = New-Object System.IO.StreamReader($stream)
        Write-Output $reader.ReadToEnd()
    } else {
         Write-Output "No response stream"
         Write-Output $_.Exception.Message
    }
}
