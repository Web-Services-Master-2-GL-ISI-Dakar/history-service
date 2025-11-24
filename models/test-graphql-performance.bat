@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: ==================================================
:: CONFIGURATION
:: ==================================================
set GRAPHQL_URL=http://localhost:8082/graphql
set GREEN=[SUCCESS]
set RED=[ERROR]
set BLUE=[INFO]
set YELLOW=[WARNING]
set CYAN=[DEBUG]

:: ==================================================
:: COMPTEURS DE PERFORMANCE
:: ==================================================
set /a TOTAL_REQUESTS=0
set /a SUCCESS_COUNT=0
set /a ERROR_COUNT=0

:: ==================================================
:: MESURE DU TEMPS DE DEBUT
:: ==================================================
for /f "tokens=1-3 delims=:,." %%a in ("%time%") do (
    set /a START_HOUR=%%a
    set /a START_MIN=%%b
    set /a START_SEC=%%c
)
set /a START_TIME=!START_HOUR!*3600 + !START_MIN!*60 + !START_SEC!

:: ==================================================
:: AFFICHAGE HEADER
:: ==================================================
echo.
echo ==================================================
echo           TEST PERFORMANCE GRAPHQL
echo ==================================================
echo.

:: ==================================================
:: VERIFICATION DE LA CONNECTIVITE
:: ==================================================
echo %BLUE% Verification de la connectivite GraphQL...
set QUERY=query { __schema { types { name } } }
echo {"query":"!QUERY!"} > schema_query.json
curl -s -X POST -H "Content-Type: application/json" -d @schema_query.json "%GRAPHQL_URL%" > schema_check.json

if errorlevel 1 (
    echo %RED% GraphQL n'est pas accessible
    goto error_exit
)
echo %GREEN% Connectivite GraphQL verifiee ✓
echo.

:: ==================================================
:: PARAMETRES DU TEST
:: ==================================================
echo %CYAN% Parametres du test GraphQL:
echo %CYAN% - URL GraphQL: %GRAPHQL_URL%
echo %CYAN% - Nombre de mutations: 10
echo %CYAN% - Type: TRANSFER
echo %CYAN% - Delai entre requetes: 1 seconde
echo.

:: ==================================================
:: EXECUTION DES MUTATIONS
:: ==================================================
echo %BLUE% Demarrage des mutations GraphQL...
echo.

for /l %%i in (1,1,10) do (
    set /a TOTAL_REQUESTS+=1
    echo %BLUE% Mutation GraphQL %%i/10 en cours...

    set MUTATION=mutation { generateTransaction(type: TRANSFER) { status message type } }
    echo {"query":"!MUTATION!"} > mutation_temp_%%i.json

    curl -s -X POST -H "Content-Type: application/json" -d @mutation_temp_%%i.json "%GRAPHQL_URL%" -o graphql_transaction_%%i.json -w "%%{http_code}" > http_status_%%i.txt 2>nul

    set /p HTTP_STATUS=<http_status_%%i.txt
    del http_status_%%i.txt
    del mutation_temp_%%i.json

    if "!HTTP_STATUS!"=="200" (
        echo %GREEN% Mutation %%i reussie (HTTP !HTTP_STATUS!)
        set /a SUCCESS_COUNT+=1
        for %%f in (graphql_transaction_%%i.json) do (
            if %%~zf gtr 0 (
                echo %CYAN%   Reponse GraphQL sauvegardee: graphql_transaction_%%i.json
            ) else (
                echo %YELLOW%   Attention: reponse GraphQL vide
                del graphql_transaction_%%i.json 2>nul
            )
        )
    ) else (
        echo %RED% Mutation %%i echouee (HTTP !HTTP_STATUS!)
        set /a ERROR_COUNT+=1
        del graphql_transaction_%%i.json 2>nul
    )

    if %%i lss 10 (
        timeout /t 1 /nobreak >nul
    )
)

:: ==================================================
:: CALCUL DES STATISTIQUES
:: ==================================================
for /f "tokens=1-3 delims=:,." %%a in ("%time%") do (
    set /a END_HOUR=%%a
    set /a END_MIN=%%b
    set /a END_SEC=%%c
)
set /a END_TIME=!END_HOUR!*3600 + !END_MIN!*60 + !END_SEC!
set /a TOTAL_TIME=!END_TIME!-!START_TIME!
set /a SUCCESS_RATE=(SUCCESS_COUNT*100)/TOTAL_REQUESTS

echo.
echo ==================================================
echo            RAPPORT DE PERFORMANCE GRAPHQL
echo ==================================================
echo %GREEN% Mutations reussies: !SUCCESS_COUNT!/!TOTAL_REQUESTS! (!SUCCESS_RATE!%%)
if !ERROR_COUNT! gtr 0 echo %RED% Mutations echouees: !ERROR_COUNT!/!TOTAL_REQUESTS!
echo %BLUE% Temps total d'execution: !TOTAL_TIME! secondes
echo.

:: ==================================================
:: VERIFICATION FINALE VIA QUERY
:: ==================================================
if !SUCCESS_COUNT! gtr 0 (
    echo %BLUE% Verification des donnees via GraphQL...
    echo.
    echo %CYAN% Recherche des transactions recentes via GraphQL...
    set QUERY=query { searchTransactions(searchInput: { type: TRANSFER, size: 5 }) { content { id transactionId type status amount } totalElements } }
    echo {"query":"!QUERY!"} > verification_query.json
    curl -s -X POST -H "Content-Type: application/json" -d @verification_query.json "%GRAPHQL_URL%" > graphql_verification.json 2>nul

    if !errorlevel! equ 0 (
        if exist graphql_verification.json (
            for %%f in (graphql_verification.json) do (
                if %%~zf gtr 0 (
                    echo %GREEN% Donnees recuperees avec succes via GraphQL
                    echo %CYAN% Resultat sauvegarde dans: graphql_verification.json
                    echo %BLUE% Apercu des donnees GraphQL:
                    type graphql_verification.json
                ) else (
                    echo %YELLOW% Aucune donnee trouvee via GraphQL
                    del graphql_verification.json 2>nul
                )
            )
        )
    ) else (
        echo %RED% Erreur lors de la requete GraphQL
    )
)

:: ==================================================
:: INSTRUCTIONS DE VERIFICATION
:: ==================================================
echo.
echo ==================================================
echo           INSTRUCTIONS DE VERIFICATION GRAPHQL
echo ==================================================
echo %BLUE% Verifiez les elements suivants:
echo.
echo %CYAN% 1. GRAPHIQL INTERFACE:
echo    - Accedez a: http://localhost:8082/graphiql
echo    - Testez les queries et mutations interactivement
echo.
echo %CYAN% 2. LOGS APPLICATION:
echo    - Requetes GraphQL traitees
echo    - Resolutions des champs
echo    - Performance des resolvers
echo.
echo %CYAN% 3. DONNEES:
echo    - Verifiez que les mutations creent bien des transactions
echo    - Testez les recherches avec differents criteres
echo.
echo %CYAN% 4. FICHIERS GENEREES:
setlocal disabledelayedexpansion
for %%f in (graphql_*.json) do (
    if exist "%%f" echo    - %%f
)
endlocal

:success_exit
echo.
echo %GREEN% Test GraphQL termine avec succes!
goto end

:error_exit
echo.
echo %RED% Test GraphQL interrompu a cause d'une erreur critique.

:end
echo.
echo Appuyez sur une touche pour fermer...
pause >nul

:: Nettoyage optionnel des fichiers temporaires
echo.
set /p CLEANUP=Voulez-vous supprimer les fichiers de test GraphQL ? (o/n):
if /i "!CLEANUP!"=="o" (
    del graphql_*.json 2>nul
    del *.json 2>nul
    echo %GREEN% Fichiers de test GraphQL supprimes.
)

exit /b
