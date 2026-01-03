# Transaction History Service - API Endpoints

> Documentation des endpoints REST et GraphQL pour l'historique des transactions

**Base URL (via Gateway):** `http://localhost:8080`  
**Service Direct:** `http://localhost:8092`  
**GraphQL Endpoint:** `http://localhost:8080/graphql`

---

## Table des matières

1. [REST API - Historique des transactions](#1-rest-api---historique-des-transactions)
2. [REST API - Recherche avancée](#2-rest-api---recherche-avancée)
3. [GraphQL API](#3-graphql-api)
4. [API de test](#4-api-de-test)
5. [Enums & Types](#5-enums--types)

---

## 1. REST API - Historique des transactions

### 1.1 Lister toutes les transactions

```
GET /api/transaction-histories?page=0&size=20&sort=transactionDate,desc
```

**Headers:**
```
Authorization: Bearer {token}
```

**Query Parameters:**
| Paramètre | Type | Description |
|-----------|------|-------------|
| `page` | int | Numéro de page (0-indexed) |
| `size` | int | Taille de la page (défaut: 20) |
| `sort` | string | Champ de tri et direction |

**Response (200 OK):**
```json
[
  {
    "id": "65f1a2b3c4d5e6f7a8b9c0d1",
    "transactionId": "TX-2026-001",
    "externalTransactionId": "EXT-123456",
    "type": "TRANSFER_P2P",
    "status": "COMPLETED",
    "amount": 5000.00,
    "currency": "XOF",
    "senderPhone": "+221773031545",
    "receiverPhone": "+221778889999",
    "senderName": "Florent Azonnoudo",
    "receiverName": "Jean Dupont",
    "description": "Remboursement",
    "fees": 50.00,
    "balanceBefore": 55000.00,
    "balanceAfter": 50000.00,
    "transactionDate": "2026-01-03T14:00:00Z",
    "processingDate": "2026-01-03T14:00:05Z",
    "correlationId": "corr-123-456"
  }
]
```

---

### 1.2 Obtenir une transaction par ID

```
GET /api/transaction-histories/{id}
```

**Response (200 OK):**
```json
{
  "id": "65f1a2b3c4d5e6f7a8b9c0d1",
  "transactionId": "TX-2026-001",
  "externalTransactionId": "EXT-123456",
  "type": "TRANSFER_P2P",
  "status": "COMPLETED",
  "amount": 5000.00,
  "currency": "XOF",
  "senderPhone": "+221773031545",
  "receiverPhone": "+221778889999",
  "senderName": "Florent Azonnoudo",
  "receiverName": "Jean Dupont",
  "description": "Remboursement",
  "fees": 50.00,
  "balanceBefore": 55000.00,
  "balanceAfter": 50000.00,
  "merchantCode": null,
  "billReference": null,
  "bankAccountNumber": null,
  "transactionDate": "2026-01-03T14:00:00Z",
  "processingDate": "2026-01-03T14:00:05Z",
  "createdBy": "user-123",
  "userAgent": "OndMoney/1.0 Android",
  "ipAddress": "192.168.1.100",
  "deviceId": "device-abc-123",
  "metadata": "{\"source\": \"mobile\"}",
  "errorMessage": null,
  "correlationId": "corr-123-456",
  "version": 1,
  "historySaved": true
}
```

---

### 1.3 Créer une entrée d'historique

```
POST /api/transaction-histories
```

**Request Body:**
```json
{
  "transactionId": "TX-2026-002",
  "type": "BILL_PAYMENT",
  "status": "COMPLETED",
  "amount": 15000.00,
  "currency": "XOF",
  "senderPhone": "+221773031545",
  "description": "Paiement SENELEC",
  "merchantCode": "SENELEC",
  "billReference": "FACT-2026-001",
  "transactionDate": "2026-01-03T15:00:00Z",
  "historySaved": true
}
```

---

### 1.4 Mettre à jour une transaction

```
PUT /api/transaction-histories/{id}
```

---

### 1.5 Mise à jour partielle

```
PATCH /api/transaction-histories/{id}
Content-Type: application/merge-patch+json
```

---

### 1.6 Supprimer une transaction

```
DELETE /api/transaction-histories/{id}
```

---

## 2. REST API - Recherche avancée

### 2.1 Rechercher des transactions

```
GET /api/transaction-histories/search
```

**Query Parameters:**
| Paramètre | Type | Description | Exemple |
|-----------|------|-------------|---------|
| `senderPhone` | string | Numéro de téléphone de l'expéditeur | `+221773031545` |
| `receiverPhone` | string | Numéro de téléphone du destinataire | `+221778889999` |
| `type` | enum | Type de transaction | `TRANSFER_P2P` |
| `status` | enum | Statut de la transaction | `COMPLETED` |
| `startDate` | ISO datetime | Date de début | `2026-01-01T00:00:00Z` |
| `endDate` | ISO datetime | Date de fin | `2026-01-31T23:59:59Z` |
| `minAmount` | decimal | Montant minimum | `1000` |
| `maxAmount` | decimal | Montant maximum | `50000` |
| `page` | int | Numéro de page | `0` |
| `size` | int | Taille de la page | `20` |

**Exemples d'utilisation:**

```bash
# Transactions d'un utilisateur
GET /api/transaction-histories/search?senderPhone=+221773031545

# Transferts réussis
GET /api/transaction-histories/search?type=TRANSFER_P2P&status=COMPLETED

# Transactions sur une période
GET /api/transaction-histories/search?startDate=2026-01-01T00:00:00Z&endDate=2026-01-31T23:59:59Z

# Transactions par montant
GET /api/transaction-histories/search?minAmount=1000&maxAmount=50000

# Recherche combinée
GET /api/transaction-histories/search?senderPhone=+221773031545&type=TRANSFER_P2P&status=COMPLETED&startDate=2026-01-01T00:00:00Z
```

**Response (200 OK):**
```json
[
  {
    "id": "65f1a2b3c4d5e6f7a8b9c0d1",
    "transactionId": "TX-2026-001",
    "type": "TRANSFER_P2P",
    "status": "COMPLETED",
    "amount": 5000.00,
    "currency": "XOF",
    "senderPhone": "+221773031545",
    "receiverPhone": "+221778889999",
    "transactionDate": "2026-01-03T14:00:00Z"
  }
]
```

---

## 3. GraphQL API

**Endpoint:** `POST /graphql`

### 3.1 Queries disponibles

#### Rechercher des transactions

```graphql
query SearchTransactions($input: TransactionSearchInput!) {
  searchTransactions(searchInput: $input) {
    content {
      id
      transactionId
      type
      status
      amount
      currency
      senderPhone
      receiverPhone
      senderName
      receiverName
      description
      fees
      balanceBefore
      balanceAfter
      transactionDate
    }
    pageInfo {
      totalElements
      totalPages
      currentPage
      size
      hasNext
      hasPrevious
    }
  }
}
```

**Variables:**
```json
{
  "input": {
    "senderPhone": "+221773031545",
    "types": ["TRANSFER_P2P", "BILL_PAYMENT"],
    "statuses": ["COMPLETED"],
    "startDate": "2026-01-01T00:00:00Z",
    "endDate": "2026-01-31T23:59:59Z",
    "minAmount": 1000,
    "maxAmount": 50000,
    "page": 0,
    "size": 20,
    "sortBy": "TRANSACTION_DATE",
    "sortDirection": "DESC"
  }
}
```

---

#### Obtenir une transaction par ID

```graphql
query GetTransaction($id: ID!) {
  transactionHistory(id: $id) {
    id
    transactionId
    type
    status
    amount
    currency
    senderPhone
    receiverPhone
    senderName
    receiverName
    description
    fees
    balanceBefore
    balanceAfter
    merchantCode
    billReference
    bankAccountNumber
    transactionDate
    processingDate
    errorMessage
    correlationId
  }
}
```

---

#### Lister toutes les transactions

```graphql
query AllTransactions($page: Int, $size: Int, $sortBy: TransactionSortField, $sortDirection: SortDirection) {
  allTransactionHistories(page: $page, size: $size, sortBy: $sortBy, sortDirection: $sortDirection) {
    content {
      id
      transactionId
      type
      status
      amount
      transactionDate
    }
    pageInfo {
      totalElements
      totalPages
      currentPage
    }
  }
}
```

---

#### Transactions d'un utilisateur

```graphql
query UserTransactions(
  $phoneNumber: String!
  $page: Int
  $size: Int
  $types: [TransactionType]
  $statuses: [TransactionStatus]
  $startDate: Instant
  $endDate: Instant
  $direction: TransactionDirection
  $sortBy: TransactionSortField
  $sortDirection: SortDirection
) {
  userTransactions(
    phoneNumber: $phoneNumber
    page: $page
    size: $size
    types: $types
    statuses: $statuses
    startDate: $startDate
    endDate: $endDate
    direction: $direction
    sortBy: $sortBy
    sortDirection: $sortDirection
  ) {
    content {
      id
      transactionId
      type
      status
      amount
      currency
      senderPhone
      receiverPhone
      transactionDate
    }
    pageInfo {
      totalElements
      totalPages
    }
  }
}
```

**Variables:**
```json
{
  "phoneNumber": "+221773031545",
  "page": 0,
  "size": 20,
  "types": ["TRANSFER_P2P"],
  "direction": "SENT",
  "sortBy": "TRANSACTION_DATE",
  "sortDirection": "DESC"
}
```

---

#### Statistiques utilisateur

```graphql
query UserStats(
  $phoneNumber: String!
  $startDate: Instant
  $endDate: Instant
  $types: [TransactionType]
  $direction: TransactionDirection
) {
  userTransactionStats(
    phoneNumber: $phoneNumber
    startDate: $startDate
    endDate: $endDate
    types: $types
    direction: $direction
  ) {
    totalTransactions
    totalAmount
    totalFees
    averageAmount
    transactionsByType {
      type
      count
      totalAmount
    }
    transactionsByStatus {
      status
      count
    }
  }
}
```

---

#### Obtenir les types de transactions

```graphql
query {
  transactionTypes {
    types
  }
}
```

---

### 3.2 Mutations disponibles

#### Créer une transaction

```graphql
mutation CreateTransaction($input: TransactionHistoryInput!) {
  createTransactionHistory(input: $input) {
    id
    transactionId
    type
    status
    amount
  }
}
```

---

#### Mettre à jour une transaction

```graphql
mutation UpdateTransaction($id: ID!, $input: TransactionHistoryInput!) {
  updateTransactionHistory(id: $id, input: $input) {
    id
    status
  }
}
```

---

#### Supprimer une transaction

```graphql
mutation DeleteTransaction($id: ID!) {
  deleteTransactionHistory(id: $id)
}
```

---

#### Générer une transaction de test

```graphql
mutation GenerateTest($type: TransactionType!) {
  generateTransaction(type: $type) {
    status
    message
    type
  }
}
```

---

## 4. API de test

> ⚠️ Endpoints réservés au développement/test

### 4.1 Générer une transaction de test

```
POST /api/test/transactions/{type}
```

**Path Parameters:**
| Paramètre | Type | Valeurs |
|-----------|------|---------|
| `type` | enum | `TRANSFER_P2P`, `CARD_RECHARGE`, `BILL_PAYMENT`, `MERCHANT_PAYMENT`, `AIRTIME_PURCHASE`, `WALLET2BANK`, `BANK2WALLET` |

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Transaction TRANSFER_P2P generated and sent to Kafka",
  "type": "TRANSFER_P2P"
}
```

---

### 4.2 Générer toutes les transactions

```
POST /api/test/transactions/all
```

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "All transaction types generated and sent to Kafka",
  "generated": [
    "TRANSFER_P2P - Generated",
    "CARD_RECHARGE - Generated",
    "BILL_PAYMENT - Generated",
    "MERCHANT_PAYMENT - Generated",
    "AIRTIME_PURCHASE - Generated",
    "WALLET2BANK - Generated",
    "BANK2WALLET - Generated"
  ]
}
```

---

### 4.3 Lister les types disponibles

```
GET /api/test/transaction-types
```

**Response (200 OK):**
```json
[
  "TRANSFER_P2P",
  "CARD_RECHARGE",
  "BILL_PAYMENT",
  "MERCHANT_PAYMENT",
  "AIRTIME_PURCHASE",
  "WALLET2BANK",
  "BANK2WALLET"
]
```

---

## 5. Enums & Types

### TransactionType
| Valeur | Description |
|--------|-------------|
| `TRANSFER_P2P` | Transfert de personne à personne |
| `CARD_RECHARGE` | Recharge de carte |
| `BILL_PAYMENT` | Paiement de facture |
| `MERCHANT_PAYMENT` | Paiement marchand |
| `AIRTIME_PURCHASE` | Achat de crédit téléphonique |
| `WALLET2BANK` | Transfert vers compte bancaire |
| `BANK2WALLET` | Transfert depuis compte bancaire |

### TransactionStatus
| Valeur | Description |
|--------|-------------|
| `PENDING` | Transaction en cours |
| `COMPLETED` | Transaction réussie |
| `FAILED` | Transaction échouée |

### TransactionDirection (GraphQL)
| Valeur | Description |
|--------|-------------|
| `SENT` | Transactions envoyées |
| `RECEIVED` | Transactions reçues |
| `ALL` | Toutes les transactions |

### TransactionSortField (GraphQL)
| Valeur | Description |
|--------|-------------|
| `TRANSACTION_DATE` | Date de transaction |
| `AMOUNT` | Montant |
| `STATUS` | Statut |
| `TYPE` | Type |
| `CREATED_AT` | Date de création |

### SortDirection (GraphQL)
| Valeur | Description |
|--------|-------------|
| `ASC` | Ordre croissant |
| `DESC` | Ordre décroissant |

---

## Exemples cURL

### Rechercher les transactions d'un utilisateur
```bash
curl -X GET "http://localhost:8080/api/transaction-histories/search?senderPhone=+221773031545&type=TRANSFER_P2P&status=COMPLETED" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Requête GraphQL
```bash
curl -X POST "http://localhost:8080/graphql" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { allTransactionHistories(page: 0, size: 10) { content { id transactionId type status amount transactionDate } pageInfo { totalElements } } }"
  }'
```

### Générer une transaction de test
```bash
curl -X POST "http://localhost:8080/api/test/transactions/TRANSFER_P2P" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Codes d'erreur

| Code HTTP | Type | Description |
|-----------|------|-------------|
| 400 | `Bad Request` | Paramètres invalides |
| 401 | `Unauthorized` | Token manquant ou invalide |
| 403 | `Forbidden` | Accès refusé |
| 404 | `Not Found` | Transaction non trouvée |
| 500 | `Internal Server Error` | Erreur serveur |
