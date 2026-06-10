#!/bin/bash
echo "============================================"
echo "  MoneyControl - Build e Execução"
echo "============================================"
echo ""

if ! command -v java &> /dev/null; then
    echo "ERRO: Java não encontrado."
    echo "Instale com: sudo apt install openjdk-21-jdk"
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    echo "ERRO: Maven não encontrado."
    echo "Instale com: sudo apt install maven"
    exit 1
fi

echo "[1/2] Compilando projeto..."
mvn clean package -q
if [ $? -ne 0 ]; then
    echo "ERRO na compilação. Veja as mensagens acima."
    exit 1
fi

echo "[2/2] Iniciando MoneyControl..."
java -jar target/moneycontrol-1.0.0-executable.jar
