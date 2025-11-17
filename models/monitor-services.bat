@echo off
echo.
echo ==================================================
echo    TEST RAPIDE TRANSACTIONS
echo ==================================================
echo.

set BASE_URL=http://localhost:8082/api

echo [INFO] Génération de 3 transactions de test...

curl -s -X POST "%BASE_URL%/test/transactions/DEPOSIT"
echo [SUCCESS] Dépôt généré

curl -s -X POST "%BASE_URL%/test/transactions/TRANSFER"
echo [SUCCESS] Transfert généré

curl -s -X POST "%BASE_URL%/test/transactions/BILL_PAYMENT"
echo [SUCCESS] Paiement facture généré

timeout /t 3 /nobreak >nul

echo.
echo [INFO] Test de recherche...
curl -s "%BASE_URL%/transaction-histories/search?type=TRANSFER"
echo.
echo [SUCCESS] Test terminé!

echo.
echo Appuyez sur une touche pour fermer...
pause >nul
