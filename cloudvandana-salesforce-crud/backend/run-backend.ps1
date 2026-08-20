$ErrorActionPreference = 'Stop'
Write-Host "Checking Java..." -ForegroundColor Cyan
java -version
Write-Host "Checking Maven..." -ForegroundColor Cyan
mvn -version
Write-Host "Starting Spring Boot backend..." -ForegroundColor Green
mvn spring-boot:run
