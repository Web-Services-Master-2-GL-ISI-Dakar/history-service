@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo.
echo ================================================
echo        TEST RAPIDE GRAPHQL TRANSACTIONS
echo ================================================
echo.

set GRAPHQL_URL=http://localhost:8082/graphql
set SUCCESS_COUNT=0
set ERROR_COUNT=0

echo [INFO] Vérification de la connectivité GraphQL...

echo {"query":"{ __typename }"} > gql_ping.json
curl -s -X POST -H "Content-Type: application/json" -d @gql_ping.json "%GRAPHQL_URL%" > ping_result.json

if %errorlevel% neq 0 (
    echo [ERROR] Impossible de contacter GraphQL.
    goto end
)

echo [SUCCESS] Connectivité GraphQL OK.
echo.

echo ==================================================
echo        GENERATION DES MUTATIONS GRAPHQL
echo ==================================================
echo.

call :mutation "DEPOSIT"
call :mutation "TRANSFER"
call :mutation "BILL_PAYMENT"

echo.
echo ==================================================
echo               RÉCAPITULATIF
echo ==================================================
echo [INFO] Succès: !SUCCESS_COUNT! / 3
echo [INFO] Erreurs: !ERROR_COUNT! / 3
echo.

echo ==================================================
echo        TEST DE RECHERCHE (GraphQL Query)
echo ==================================================
echo.

echo {"query":"query { searchTransactions(searchInput: { type: TRANSFER, size: 5 }) { totalElements content { id type status } } }"} > gql_search.json

curl -s -X POST -H "Content-Type: application/json" -d @gql_search.json "%GRAPHQL_URL%" > gql_search_result.json

if %errorlevel% equ 0 (
    echo [SUCCESS] Résultat sauvegardé dans gql_search_result.json
    type gql_search_result.json
) else (
    echo [ERROR] Échec de la recherche GraphQL
)

:end
echo.
echo Appuyez sur une touche pour fermer...
pause >nul
exit /b


:mutation
set TYPE=%~1
set FILE=gql_mut_%TYPE%.json
set OUT=response_%TYPE%.json

echo {"query":"mutation { generateTransaction(type: %TYPE%) { status message type } }"} > "%FILE%"

echo [ACTION] Mutation %TYPE%...

curl -s -X POST -H "Content-Type: application/json" -d @"%FILE%" "%GRAPHQL_URL%" -o "%OUT%" >nul

if %errorlevel% equ 0 (
    for /f %%i in ('type "%OUT%" ^| find /c /v ""') do set LINES=%%i
    if !LINES! gtr 0 (
        echo [SUCCESS] %TYPE% généré → %OUT%
        set /a SUCCESS_COUNT+=1
    ) else (
        echo [ERROR] %TYPE% réponse vide.
        set /a ERROR_COUNT+=1
    )
) else (
    echo [ERROR] %TYPE% échec de la mutation.
    set /a ERROR_COUNT+=1
)

exit /b
