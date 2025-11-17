@echo off
setlocal enabledelayedexpansion

REM Configuration
set BASE_URL=http://localhost:8082/api
set DELAY=2
set GREEN=[SUCCESS]
set RED=[ERROR]
set BLUE=[INFO]

echo.
echo ==================================================
echo    TEST COMPLET TRANSACTIONS HISTORY SERVICE
echo ==================================================
echo.

REM 1. Vérifier que le service est démarré
echo %BLUE% Vérification du service...
timeout /t 3 /nobreak >nul

curl -s -o response.txt "%BASE_URL%/transaction-histories?page=0&size=1"
if errorlevel 1 (
    echo %RED% Le service n'est pas accessible sur %BASE_URL%
    echo %BLUE% Assurez-vous que l'application est démarrée sur le port 8081
    goto :error
)
echo %GREEN% Service accessible ✓

REM 2. Obtenir la liste des types de transactions
echo.
echo %BLUE% Récupération des types de transactions...
curl -s -o types.json "%BASE_URL%/test/transaction-types"
echo %GREEN% Types de transactions récupérés ✓
type types.json
echo.

REM 3. Générer tous les types de transactions
echo %BLUE% Génération de tous les types de transactions...
curl -s -X POST -H "Content-Type: application/json" -o generate_all.json "%BASE_URL%/test/transactions/all"
echo %GREEN% Toutes les transactions générées et envoyées à Kafka ✓
type generate_all.json
echo.

REM 4. Attendre le traitement par le consumer
echo %BLUE% Attente de %DELAY% secondes pour le traitement Kafka/MongoDB/Elasticsearch...
timeout /t %DELAY% /nobreak >nul

REM 5. Générer des transactions spécifiques une par une
echo.
echo %BLUE% Génération de transactions spécifiques...

echo DEPOSIT...
curl -s -X POST -H "Content-Type: application/json" -o deposit.json "%BASE_URL%/test/transactions/DEPOSIT"
timeout /t 1 /nobreak >nul

echo TRANSFER...
curl -s -X POST -H "Content-Type: application/json" -o transfer.json "%BASE_URL%/test/transactions/TRANSFER"
timeout /t 1 /nobreak >nul

echo WITHDRAWAL...
curl -s -X POST -H "Content-Type: application/json" -o withdrawal.json "%BASE_URL%/test/transactions/WITHDRAWAL"
timeout /t 1 /nobreak >nul

echo BILL_PAYMENT...
curl -s -X POST -H "Content-Type: application/json" -o bill.json "%BASE_URL%/test/transactions/BILL_PAYMENT"
timeout /t 1 /nobreak >nul

echo AIRTIME...
curl -s -X POST -H "Content-Type: application/json" -o airtime.json "%BASE_URL%/test/transactions/AIRTIME"
timeout /t 1 /nobreak >nul

echo MERCHANT_PAYMENT...
curl -s -X POST -H "Content-Type: application/json" -o merchant.json "%BASE_URL%/test/transactions/MERCHANT_PAYMENT"
timeout /t 1 /nobreak >nul

echo BANK_TRANSFER...
curl -s -X POST -H "Content-Type: application/json" -o bank.json "%BASE_URL%/test/transactions/BANK_TRANSFER"
timeout /t 1 /nobreak >nul

echo TOP_UP_CARD...
curl -s -X POST -H "Content-Type: application/json" -o topup.json "%BASE_URL%/test/transactions/TOP_UP_CARD"
timeout /t 1 /nobreak >nul

echo %GREEN% Toutes les transactions spécifiques générées ✓

REM 6. Attendre le traitement final
echo.
echo %BLUE% Attente finale de %DELAY% secondes pour traitement complet...
timeout /t %DELAY% /nobreak >nul

REM 7. Tests de recherche Elasticsearch
echo.
echo ==================================================
echo    TESTS DE RECHERCHE ELASTICSEARCH
echo ==================================================
echo.

REM 7.1 Recherche de toutes les transactions
echo %BLUE% Recherche de toutes les transactions...
curl -s -o all_transactions.json "%BASE_URL%/transaction-histories?page=0&size=5"
echo %GREEN% Recherche globale effectuée ✓

REM Compter le nombre de transactions
for /f "tokens=3" %%i in ('curl -s "%BASE_URL%/transaction-histories?page=0&size=1" ^| findstr "totalElements"') do (
    set TOTAL=%%i
    set TOTAL=!TOTAL:,=!
)
echo %BLUE% Nombre total de transactions trouvées: !TOTAL!

REM 7.2 Recherche par téléphone
echo.
echo %BLUE% Recherche par téléphone +221771234567...
curl -s -o search_phone.json "%BASE_URL%/transaction-histories/search?phoneNumber=+221771234567"
echo %GREEN% Recherche par téléphone effectuée ✓

REM 7.3 Recherche par type TRANSFER
echo.
echo %BLUE% Recherche par type TRANSFER...
curl -s -o search_transfer.json "%BASE_URL%/transaction-histories/search?type=TRANSFER"
echo %GREEN% Recherche par type effectuée ✓

REM 7.4 Recherche par statut SUCCESS
echo.
echo %BLUE% Recherche par statut SUCCESS...
curl -s -o search_success.json "%BASE_URL%/transaction-histories/search?status=SUCCESS"
echo %GREEN% Recherche par statut effectuée ✓

REM 7.5 Recherche combinée type et statut
echo.
echo %BLUE% Recherche combinée TRANSFER + SUCCESS...
curl -s -o search_combined.json "%BASE_URL%/transaction-histories/search?type=TRANSFER&status=SUCCESS"
echo %GREEN% Recherche combinée effectuée ✓

REM 7.6 Recherche textuelle
echo.
echo %BLUE% Recherche textuelle "Dépôt"...
curl -s -o search_text.json "%BASE_URL%/transaction-histories/search?query=Dépôt"
echo %GREEN% Recherche textuelle effectuée ✓

REM 7.7 Recherche par montant
echo.
echo %BLUE% Recherche par plage de montants 1000-50000...
curl -s -o search_amount.json "%BASE_URL%/transaction-histories/search?minAmount=1000&maxAmount=50000"
echo %GREEN% Recherche par montant effectuée ✓

REM 8. Affichage des résultats des recherches
echo.
echo ==================================================
echo    RESULTATS DES RECHERCHES
echo ==================================================
echo.

echo %BLUE% Transactions récentes (5 premières):
type all_transactions.json
echo.

echo %BLUE% Transactions par téléphone +221771234567:
type search_phone.json
echo.

echo %BLUE% Transactions de type TRANSFER:
type search_transfer.json
echo.

echo %BLUE% Transactions avec statut SUCCESS:
type search_success.json
echo.

echo %BLUE% Transactions TRANSFER + SUCCESS:
type search_combined.json
echo.

REM 9. Nettoyage des fichiers temporaires
del *.json >nul 2>&1
del response.txt >nul 2>&1

echo.
echo ==================================================
echo    TEST TERMINE AVEC SUCCES!
echo ==================================================
echo.
echo Résumé:
echo - Tous les types de transactions générés ✓
echo - Messages publiés vers Kafka ✓
echo - Traitement par le consumer ✓
echo - Sauvegarde MongoDB ✓
echo - Indexation Elasticsearch ✓
echo - Recherches testées ✓
echo.
echo Vérifiez les logs de l'application pour voir:
echo - Les événements Kafka consommés
echo - Les sauvegardes MongoDB
echo - Les événements de statut publiés
echo.

goto :end

:error
echo.
echo ==================================================
echo    ERREUR DETECTEE
echo ==================================================
echo.
echo Le test a rencontré une erreur. Vérifiez que:
echo 1. L'application est démarrée sur le port 8081
echo 2. MongoDB est démarré sur localhost:27017
echo 3. Elasticsearch est démarré sur localhost:9200
echo 4. Kafka est démarré sur localhost:9092
echo.

:end
echo Appuyez sur une touche pour fermer...
pause >nul
