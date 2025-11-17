@echo off
setlocal enabledelayedexpansion

set BASE_URL=http://localhost:8082/api
set GREEN=[SUCCESS]
set BLUE=[INFO]

echo.
echo ==================================================
echo    TEST CONSUMER KAFKA
echo ==================================================
echo.

echo %BLUE% Test du flux Kafka complet...
echo.

REM Générer plusieurs transactions rapidement
for /l %%i in (1,1,10) do (
    echo %BLUE% Génération transaction %%i...
    curl -s -X POST "%BASE_URL%/test/transactions/TRANSFER" >nul
    timeout /t 1 /nobreak >nul
)

echo.
echo %GREEN% 10 transactions générées et envoyées à Kafka ✓
echo.
echo %BLUE% Vérifiez les logs de l'application pour voir:
echo - Messages reçus par le consumer
echo - Sauvegarde dans MongoDB
echo - Événements de statut publiés
echo.
echo Appuyez sur une touche pour fermer...
pause >nul
