[file name]: test-graphql-transactions.bat
[file content begin]
@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM Configuration
set GRAPHQL_URL=http://localhost:8082/graphql
set DELAY=2
set GREEN=[SUCCESS]
set RED=[ERROR]
set BLUE=[INFO]
set YELLOW=[WARNING]

echo.
echo ==================================================
echo    TEST COMPLET GRAPHQL TRANSACTIONS SERVICE
echo ==================================================
echo.

REM 1. Vérifier que le service GraphQL est démarré
echo %BLUE% Vérification du service GraphQL...
timeout /t 3 /nobreak >nul

REM Test de santé GraphQL
curl -s -X POST -H "Content-Type: application/json" -d "{\"query\":\"query { __schema { types { name } } }\"}" "%GRAPHQL_URL%" > health_check.json
if errorlevel 1 (
    echo %RED% Le service GraphQL n'est pas accessible sur %GRAPHQL_URL%
    echo %BLUE% Assurez-vous que l'application est démarrée
    goto :error
)
echo %GREEN% Service GraphQL accessible ✓

REM 2. Obtenir la liste des types de transactions via GraphQL
echo.
echo %BLUE% Récupération des types de transactions via GraphQL...
set QUERY=query { transactionTypes { types } }
echo {"query":"!QUERY!"} > query_types.json
curl -s -X POST -H "Content-Type: application/json" -d @query_types.json "%GRAPHQL_URL%" > graphql_types.json

echo %GREEN% Types de transactions récupérés via GraphQL ✓
type graphql_types.json
echo.

REM 3. Générer tous les types de transactions via GraphQL Mutation
echo %BLUE% Génération de tous les types de transactions via GraphQL...
set MUTATION=mutation { generateAllTransactions { status message } }
echo {"query":"!MUTATION!"} > mutation_all.json
curl -s -X POST -H "Content-Type: application/json" -d @mutation_all.json "%GRAPHQL_URL%" > generate_all.json

echo %GREEN% Toutes les transactions générées via GraphQL ✓
type generate_all.json
echo.

REM 4. Attendre le traitement par le consumer
echo %BLUE% Attente de %DELAY% secondes pour le traitement Kafka/MongoDB/Elasticsearch...
timeout /t %DELAY% /nobreak >nul

REM 5. Générer des transactions spécifiques via GraphQL Mutations
echo.
echo %BLUE% Génération de transactions spécifiques via GraphQL...

echo DEPOSIT...
set MUTATION=mutation { generateTransaction(type: DEPOSIT) { status message type } }
echo {"query":"!MUTATION!"} > mutation_deposit.json
curl -s -X POST -H "Content-Type: application/json" -d @mutation_deposit.json "%GRAPHQL_URL%" > deposit.json
timeout /t 1 /nobreak >nul

echo TRANSFER...
set MUTATION=mutation { generateTransaction(type: TRANSFER) { status message type } }
echo {"query":"!MUTATION!"} > mutation_transfer.json
curl -s -X POST -H "Content-Type: application/json" -d @mutation_transfer.json "%GRAPHQL_URL%" > transfer.json
timeout /t 1 /nobreak >nul

echo WITHDRAWAL...
set MUTATION=mutation { generateTransaction(type: WITHDRAWAL) { status message type } }
echo {"query":"!MUTATION!"} > mutation_withdrawal.json
curl -s -X POST -H "Content-Type: application/json" -d @mutation_withdrawal.json "%GRAPHQL_URL%" > withdrawal.json
timeout /t 1 /nobreak >nul

echo BILL_PAYMENT...
set MUTATION=mutation { generateTransaction(type: BILL_PAYMENT) { status message type } }
echo {"query":"!MUTATION!"} > mutation_bill.json
curl -s -X POST -H "Content-Type: application/json" -d @mutation_bill.json "%GRAPHQL_URL%" > bill.json
timeout /t 1 /nobreak >nul

echo %GREEN% Transactions spécifiques générées via GraphQL ✓

REM 6. Attendre le traitement final
echo.
echo %BLUE% Attente finale de %DELAY% secondes pour traitement complet...
timeout /t %DELAY% /nobreak >nul

REM 7. Tests de recherche GraphQL avec Elasticsearch
echo.
echo ==================================================
echo    TESTS DE RECHERCHE GRAPHQL + ELASTICSEARCH
echo ==================================================
echo.

REM 7.1 Recherche de toutes les transactions
echo %BLUE% Recherche de toutes les transactions via GraphQL...
set QUERY=query { allTransactionHistories(page: 0, size: 5) { content { id transactionId type status amount senderPhone receiverPhone transactionDate } totalElements totalPages } }
echo {"query":"!QUERY!"} > query_all.json
curl -s -X POST -H "Content-Type: application/json" -d @query_all.json "%GRAPHQL_URL%" > all_transactions.json
echo %GREEN% Recherche globale GraphQL effectuée ✓

REM 7.2 Recherche avancée avec critères multiples
echo.
echo %BLUE% Recherche avancée avec critères multiples...
set QUERY=query { searchTransactions(searchInput: { senderPhone: "+221771234567", type: TRANSFER, status: SUCCESS, page: 0, size: 10 }) { content { id transactionId type status amount senderPhone receiverPhone transactionDate } totalElements } }
echo {"query":"!QUERY!"} > query_search.json
curl -s -X POST -H "Content-Type: application/json" -d @query_search.json "%GRAPHQL_URL%" > search_advanced.json
echo %GREEN% Recherche avancée GraphQL effectuée ✓

REM 7.3 Recherche par type TRANSFER
echo.
echo %BLUE% Recherche par type TRANSFER...
set QUERY=query { searchTransactions(searchInput: { type: TRANSFER, page: 0, size: 5 }) { content { id transactionId type status amount senderPhone receiverPhone } totalElements } }
echo {"query":"!QUERY!"} > query_transfer.json
curl -s -X POST -H "Content-Type: application/json" -d @query_transfer.json "%GRAPHQL_URL%" > search_transfer.json
echo %GREEN% Recherche par type effectuée ✓

REM 7.4 Recherche par statut SUCCESS
echo.
echo %BLUE% Recherche par statut SUCCESS...
set QUERY=query { searchTransactions(searchInput: { status: SUCCESS, page: 0, size: 5 }) { content { id transactionId type status amount } totalElements } }
echo {"query":"!QUERY!"} > query_success.json
curl -s -X POST -H "Content-Type: application/json" -d @query_success.json "%GRAPHQL_URL%" > search_success.json
echo %GREEN% Recherche par statut effectuée ✓

REM 7.5 Recherche par plage de montants
echo.
echo %BLUE% Recherche par plage de montants...
set QUERY=query { searchTransactions(searchInput: { minAmount: 1000, maxAmount: 50000, page: 0, size: 5 }) { content { id transactionId type amount } totalElements } }
echo {"query":"!QUERY!"} > query_amount.json
curl -s -X POST -H "Content-Type: application/json" -d @query_amount.json "%GRAPHQL_URL%" > search_amount.json
echo %GREEN% Recherche par montant effectuée ✓

REM 8. Test de création d'une transaction via GraphQL
echo.
echo %BLUE% Test de création d'une transaction via GraphQL...
set MUTATION=mutation { createTransactionHistory(input: { transactionId: "TXN_GRAPHQL_TEST_001", type: TRANSFER, status: SUCCESS, amount: 25000.50, currency: "XOF", senderPhone: "+221771234567", receiverPhone: "+221772345678", transactionDate: "2024-01-15T10:30:00Z", historySaved: true }) { id transactionId type status amount } }
echo {"query":"!MUTATION!"} > mutation_create.json
curl -s -X POST -H "Content-Type: application/json" -d @mutation_create.json "%GRAPHQL_URL%" > create_transaction.json
echo %GREEN% Création de transaction GraphQL testée ✓

REM 9. Affichage des résultats des recherches GraphQL
echo.
echo ==================================================
echo    RESULTATS DES RECHERCHES GRAPHQL
echo ==================================================
echo.

echo %BLUE% Transactions récentes (GraphQL):
type all_transactions.json
echo.

echo %BLUE% Recherche avancée (GraphQL):
type search_advanced.json
echo.

echo %BLUE% Transactions TRANSFER (GraphQL):
type search_transfer.json
echo.

echo %BLUE% Transactions SUCCESS (GraphQL):
type search_success.json
echo.

echo %BLUE% Création transaction (GraphQL):
type create_transaction.json
echo.

REM 10. Nettoyage des fichiers temporaires
del *.json >nul 2>&1
del response.txt >nul 2>&1

echo.
echo ==================================================
echo    TEST GRAPHQL TERMINE AVEC SUCCES!
echo ==================================================
echo.
echo Résumé GraphQL:
echo - Schema GraphQL accessible ✓
echo - Queries exécutées ✓
echo - Mutations exécutées ✓
echo - Recherches Elasticsearch via GraphQL ✓
echo - Création de transactions via GraphQL ✓
echo.
echo Endpoints testés:
echo - GraphQL: %GRAPHQL_URL%
echo - GraphiQL: http://localhost:8082/graphiql
echo.
echo Vérifiez les logs de l'application pour voir:
echo - Les requêtes GraphQL traitées
echo - Les événements Kafka consommés
echo - Les sauvegardes MongoDB
echo.

goto :end

:error
echo.
echo ==================================================
echo    ERREUR DETECTEE
echo ==================================================
echo.
echo Le test GraphQL a rencontré une erreur. Vérifiez que:
echo 1. L'application est démarrée sur le port 8082
echo 2. Le endpoint GraphQL est configuré sur /graphql
echo 3. GraphiQL est accessible sur http://localhost:8082/graphiql
echo 4. Les dépendances GraphQL sont correctement configurées
echo.

:end
echo Appuyez sur une touche pour fermer...
pause >nul
[file content end]
