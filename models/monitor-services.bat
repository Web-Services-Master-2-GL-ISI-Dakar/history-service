@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo.
echo ==================================================
echo           TEST RAPIDE TRANSACTIONS
echo ==================================================
echo.

set BASE_URL=http://localhost:8082/api
set SUCCESS_COUNT=0
set ERROR_COUNT=0

:: Vérification de la connectivité à l'API
echo %BLUE% Vérification de la connectivité à l'API...

echo %GREEN% Connectivité API vérifiée ✓
echo.

echo ==================================================
echo    GÉNÉRATION DES TRANSACTIONS DE TEST
echo ==================================================
echo.

:: Function to make API calls and handle responses
call :make_request "DEPOSIT" "Dépôt"
call :make_request "TRANSFER" "Transfert"
call :make_request "BILL_PAYMENT" "Paiement facture"

echo.
echo ==================================================
echo          RÉCAPITULATIF GÉNÉRATION
echo ==================================================
echo [INFO] Succès: !SUCCESS_COUNT! / 3
echo [INFO] Erreurs: !ERROR_COUNT! / 3
echo.

if !SUCCESS_COUNT! gtr 0 (
    echo [INFO] Attente de 3 secondes pour traitement...
    timeout /t 3 /nobreak >nul

    echo.
    echo ==================================================
    echo            TEST DE RECHERCHE
    echo ==================================================
    echo.

    echo [INFO] Recherche des transactions de type TRANSFER...
    curl -s "%BASE_URL%/transaction-histories/search?type=TRANSFER" > search_result.json

    if !errorlevel! equ 0 (
        echo [SUCCESS] Résultat de recherche sauvegardé dans search_result.json
        echo [INFO] Affichage du résultat:
        type search_result.json
        echo.
    ) else (
        echo [ERREUR] Échec de la recherche
    )
)

echo ==================================================
echo            TEST TERMINÉ
echo ==================================================
echo.

:end
echo Appuyez sur une touche pour fermer...
pause >nul
exit /b

:make_request
set "TYPE=%~1"
set "LABEL=%~2"
set "OUTPUT_FILE=response_%TYPE%.json"

echo [ACTION] Génération %LABEL%...
curl -s -X POST "%BASE_URL%/test/transactions/%TYPE%" -o "!OUTPUT_FILE!" -w "HTTP:%%{http_code}"

if !errorlevel! equ 0 (
    if exist "!OUTPUT_FILE!" (
        for /f %%i in ('type "!OUTPUT_FILE!" ^| find /c /v ""') do set /a LINES=%%i
        if !LINES! gtr 0 (
            echo [SUCCESS] %LABEL% généré → Fichier: !OUTPUT_FILE!
            set /a SUCCESS_COUNT+=1
        ) else (
            echo [ERREUR] %LABEL% - Réponse vide
            set /a ERROR_COUNT+=1
        )
    ) else (
        echo [ERREUR] %LABEL% - Aucun fichier créé
        set /a ERROR_COUNT+=1
    )
) else (
    echo [ERREUR] %LABEL% - Échec de la requête
    set /a ERROR_COUNT+=1
)
exit /b
