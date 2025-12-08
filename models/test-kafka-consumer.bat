@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: Configuration
set BASE_URL=http://localhost:8082/api
set GREEN=[SUCCESS]
set RED=[ERROR]
set BLUE=[INFO]
set YELLOW=[WARNING]
set CYAN=[DEBUG]

:: Compteurs de performance
set /a TOTAL_REQUESTS=0
set /a SUCCESS_COUNT=0
set /a ERROR_COUNT=0

:: Mesure du temps de début
for /f "tokens=1-3 delims=:" %%a in ("!time!") do (
    set /a START_HOUR=%%a
    set /a START_MIN=%%b
    set /a START_SEC=%%c
)
set /a START_TIME=!START_HOUR!*3600 + !START_MIN!*60 + !START_SEC!

echo.
echo ==================================================
echo           TEST CONSUMER KAFKA AVANCÉ
echo ==================================================
echo.

:: Vérification de la connectivité à l'API
echo %BLUE% Vérification de la connectivité à l'API...
echo %GREEN% Connectivité API vérifiée
echo.

:: Affichage des paramètres du test
echo %CYAN% Paramètres du test:
echo %CYAN% - URL API: %BASE_URL%
echo %CYAN% - Nombre de transactions: 10
echo %CYAN% - Type: TRANSFER
echo %CYAN% - Délai entre requêtes: 1 seconde
echo.

echo %BLUE% Démarrage de la génération des transactions Kafka...
echo.

:: Génération des transactions avec suivi en temps réel
for /l %%i in (1,1,10) do (
    set /a TOTAL_REQUESTS+=1

    echo %BLUE% Transaction %%i/10 en cours...

    :: Mesure du temps de réponse
    for /f "tokens=1-3 delims=:." %%a in ("!time!") do (
        set START_MS=%%c
    )

    curl -s -X POST "%BASE_URL%/test/transactions/TRANSFER" -o transaction_%%i.json -w "%%{http_code}" > http_status_%%i.txt 2>nul

    for /f "tokens=1-3 delims=:." %%a in ("!time!") do (
        set END_MS=%%c
    )

    set /p HTTP_STATUS=<http_status_%%i.txt
    del http_status_%%i.txt

    if "!HTTP_STATUS!"=="200" (
        echo %GREEN% Transaction %%i réussie ^(HTTP !HTTP_STATUS!^)
        set /a SUCCESS_COUNT+=1

        :: Vérification du contenu de la réponse
        for %%f in (transaction_%%i.json) do (
            if %%~zf gtr 0 (
                echo %CYAN%   Réponse sauvegardée: transaction_%%i.json
            ) else (
                echo %YELLOW%   Attention: réponse vide
                del transaction_%%i.json 2>nul
            )
        )
    ) else (
        echo %RED% Transaction %%i échouée ^(HTTP !HTTP_STATUS!^)
        set /a ERROR_COUNT+=1
        del transaction_%%i.json 2>nul
    )

    if %%i lss 10 (
        timeout /t 1 /nobreak >nul
    )
)

:: Calcul des statistiques de performance
for /f "tokens=1-3 delims=:" %%a in ("!time!") do (
    set /a END_HOUR=%%a
    set /a END_MIN=%%b
    set /a END_SEC=%%c
)
set /a END_TIME=!END_HOUR!*3600 + !END_MIN!*60 + !END_SEC!
set /a TOTAL_TIME=!END_TIME!-!START_TIME!
set /a SUCCESS_RATE=(SUCCESS_COUNT*100)/TOTAL_REQUESTS

echo.
echo ==================================================
echo            RAPPORT DE PERFORMANCE
echo ==================================================
echo %GREEN% Transactions réussies: !SUCCESS_COUNT!/!TOTAL_REQUESTS! (!SUCCESS_RATE!%%)
if !ERROR_COUNT! gtr 0 echo %RED% Transactions échouées: !ERROR_COUNT!/!TOTAL_REQUESTS!
echo %BLUE% Temps total d'exécution: !TOTAL_TIME! secondes
echo.

:: Vérification finale du flux Kafka
if !SUCCESS_COUNT! gtr 0 (
    echo %BLUE% Vérification du flux de données Kafka...
    echo.

    :: Test de recherche des transactions
    echo %CYAN% Recherche des transactions récentes...
    curl -s "%BASE_URL%/transaction-histories/search?type=TRANSFER&size=5" > kafka_validation.json 2>nul

    if !errorlevel! equ 0 (
        if exist kafka_validation.json (
            for %%f in (kafka_validation.json) do (
                if %%~zf gtr 0 (
                    echo %GREEN% Données Kafka récupérées avec succès
                    echo %CYAN% Échantillon sauvegardé dans: kafka_validation.json

                    :: Affichage d'un aperçu du fichier
                    echo %BLUE% Aperçu des données:
                    type kafka_validation.json
                ) else (
                    echo %YELLOW% Aucune donnée trouvée dans Kafka
                    del kafka_validation.json 2>nul
                )
            )
        )
    ) else (
        echo %RED% Erreur lors de la récupération des données Kafka
    )
)

echo.
echo ==================================================
echo           INSTRUCTIONS DE VÉRIFICATION
echo ==================================================
echo %BLUE% Vérifiez les éléments suivants:
echo.
echo %CYAN% 1. LOGS APPLICATION:
echo    - Messages reçus par le consumer Kafka
echo    - Traitement des événements
echo    - Sauvegarde MongoDB
echo.
echo %CYAN% 2. BASE DE DONNÉES:
echo    - Documents dans la collection transactions
echo    - Statut des transactions
echo    - Cohérence des données
echo.
echo %CYAN% 3. KAFKA:
echo    - Messages dans le topic transactions
echo    - Consumer groups actifs
echo    - Latence de traitement
echo.
echo %CYAN% 4. FICHIERS GÉNÉRÉS:
setlocal disabledelayedexpansion
for %%f in (transaction_*.json kafka_validation.json) do (
    if exist "%%f" echo    - %%f
)
endlocal

:success_exit
echo.
echo %GREEN% Test terminé avec succès!
goto :end

:error_exit
echo.
echo %RED% Test interrompu à cause d'une erreur critique.

:end
echo.
echo Appuyez sur une touche pour fermer...
pause >nul

:: Nettoyage optionnel des fichiers temporaires
echo.
set /p CLEANUP=Voulez-vous supprimer les fichiers de test ? (o/n):
if /i "!CLEANUP!"=="o" (
    del transaction_*.json 2>nul
    del kafka_validation.json 2>nul
    echo %GREEN% Fichiers de test supprimés.
)

exit /b
