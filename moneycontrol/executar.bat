@echo off
echo ============================================
echo   MoneyControl - Build e Execucao
echo ============================================
echo.

where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Java nao encontrado. Instale o JDK 21 em https://adoptium.net
    pause
    exit /b 1
)

where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Maven nao encontrado. Instale em https://maven.apache.org
    pause
    exit /b 1
)

echo [1/2] Compilando projeto...
mvn clean package -q
if %errorlevel% neq 0 (
    echo ERRO na compilacao. Veja as mensagens acima.
    pause
    exit /b 1
)

echo [2/2] Iniciando MoneyControl...
java -jar target\moneycontrol-1.0.0-executable.jar

pause
