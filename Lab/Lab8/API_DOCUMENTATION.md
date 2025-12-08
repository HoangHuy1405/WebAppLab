# Customer API Documentation

## Base URL

`http://localhost:8080/api/customers`

## Rate Limiting

The API implements rate limiting to prevent abuse and ensure fair usage across all clients.

**Rate Limit:** 100 requests per minute per IP address

**Headers:**

- When rate limit is exceeded, the API returns HTTP status code `429 Too Many Requests`

**Response when rate limit is exceeded:**

```json
{
  "error": "Too many requests",
  "message": "Rate limit exceeded. Please try again later."
}
```

**Best Practices:**

- Implement exponential backoff in your client when receiving 429 responses
- Cache responses when possible to reduce API calls
- Batch operations when feasible

---

## Endpoints

### 1. Get All Customers (with Pagination and Sorting)

**GET** `/api/customers`

**Example Request:**

```
GET /api/customers?page=0&size=10&sortBy=createdAt&sortDir=desc
```

**Response:** 200 OK

```json
{
  "totalItems": 3,
  "totalPages": 1,
  "customers": [
    {
      "id": 2,
      "customerCode": "C002",
      "fullName": "HoangHuy updated",
      "email": "hoanghuy2@example.com",
      "phone": "025489100212",
      "address": "so 1 duong 16",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:55:21"
    },
    {
      "id": 3,
      "customerCode": "C003",
      "fullName": "Bob Johnson",
      "email": "bob.johnson@example.com",
      "phone": "+1-555-0103",
      "address": "789 Pine Rd, Chicago",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:55:21"
    },
    {
      "id": 73,
      "customerCode": "C004",
      "fullName": "Ng Mai Hoang Huy",
      "email": "ngmaihoanghuy@gmail.com",
      "phone": "0332132142",
      "address": "so 1 duong so 32",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T10:35:02"
    }
  ],
  "currentPage": 0
}
```

---

### 2. Get Customer by ID

**GET** `/api/customers/{id}`

**Example Request:**

```
GET /api/customers/1
```

**Response:** 200 OK

```json
{
  "id": 2,
  "customerCode": "C002",
  "fullName": "John Partially Updated",
  "email": "john.updated@example.com",
  "phone": "02548910012",
  "address": "New Address",
  "status": "ACTIVE",
  "createdAt": "2025-12-02T15:55:21"
}
```

**Error Response:** 404 Not Found

```json
{
  "timestamp": "2025-12-09T00:31:24.0158181",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 12",
  "path": "/api/customers/12",
  "details": null
}
```

---

### 3. Create New Customer

**POST** `/api/customers`

**Request Body:**

```json
{
  "customerCode": "C010",
  "fullName": "HoangHuy",
  "email": "hoanghuy@example.com",
  "phone": "025489100212",
  "address": "so 1 duong 16",
  "status": "ACTIVE"
}
```

**Response:** 201 Created

```json
{
  "id": 74,
  "customerCode": "C010",
  "fullName": "HoangHuy",
  "email": "hoanghuy@example.com",
  "phone": "025489100212",
  "address": "so 1 duong 16",
  "status": "ACTIVE",
  "createdAt": "2025-12-09T00:23:01.771499"
}
```

**Error Response:** 400 Bad Request (Validation Error)

```json
{
  "timestamp": "2025-12-09T00:33:02.1825618",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/customers",
  "details": ["email: Invalid email format"]
}
```

**Error Response:** 409 Conflict (Duplicate)

```json
{
  "timestamp": "2025-12-09T00:33:22.2530919",
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists: hoanghuy2@example.com",
  "path": "/api/customers",
  "details": null
}
```

---

### 4. Update Customer (Full Update)

**PUT** `/api/customers/{id}`

**Request Body:**

```json
{
  "customerCode": "C010",
  "fullName": "HoangHuy updated",
  "email": "hoanghuy2@example.com",
  "phone": "025489100212",
  "address": "so 1 duong 16"
}
```

**Note:** Customer code is immutable and cannot be changed.

**Response:** 200 OK

```json
{
  "id": 2,
  "customerCode": "C002",
  "fullName": "HoangHuy updated",
  "email": "hoanghuy2@example.com",
  "phone": "025489100212",
  "address": "so 1 duong 16",
  "status": "ACTIVE",
  "createdAt": "2025-12-02T15:55:21"
}
```

---

### 5. Partial Update Customer

**PATCH** `/api/customers/{id}`

**Request Body:** (All fields are optional)

```json
{
  "fullName": "John Updated partial"
}
```

**Response:** 200 OK

```json
{
  "id": 2,
  "customerCode": "C002",
  "fullName": "John Partially Updated",
  "email": "john.updated@example.com",
  "phone": "02548910012",
  "address": "New Address",
  "status": "ACTIVE",
  "createdAt": "2025-12-02T15:55:21"
}
```

---

### 6. Delete Customer

**DELETE** `/api/customers/{id}`

**Example Request:**

```
DELETE /api/customers/1
```

**Response:** 200 OK

```json
{
  "message": "Customer deleted successfully"
}
```

**Error Response:** 404 Not Found

```json
{
  "timestamp": "2025-12-09T00:36:04.4963609",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 75",
  "path": "/api/customers/75",
  "details": null
}
```

---

### 7. Search Customers (Simple Search)

**GET** `/api/customers/search`

**Example Request:**

```
GET /api/customers/search?keyword=John
```

**Description:** Searches across customer name, email, and customer code.

**Response:** 200 OK

```json
[
  {
    "id": 3,
    "customerCode": "C003",
    "fullName": "Bob Johnson",
    "email": "bob.johnson@example.com",
    "phone": "+1-555-0103",
    "address": "789 Pine Rd, Chicago",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:55:21"
  }
]
```

---

### 8. Get Customers by Status

**GET** `/api/customers/status/{status}`

**Path Parameters:**

- `status` (required) - Customer status: "ACTIVE" or "INACTIVE"

**Example Request:**

```
GET /api/customers/status/ACTIVE
```

**Response:** 200 OK

```json
[
  {
    "id": 2,
    "customerCode": "C002",
    "fullName": "HoangHuy updated",
    "email": "hoanghuy2@example.com",
    "phone": "025489100212",
    "address": "so 1 duong 16",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:55:21"
  },
  {
    "id": 3,
    "customerCode": "C003",
    "fullName": "Bob Johnson",
    "email": "bob.johnson@example.com",
    "phone": "+1-555-0103",
    "address": "789 Pine Rd, Chicago",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:55:21"
  },
  {
    "id": 73,
    "customerCode": "C004",
    "fullName": "Ng Mai Hoang Huy",
    "email": "ngmaihoanghuy@gmail.com",
    "phone": "0332132142",
    "address": "so 1 duong so 32",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T10:35:02"
  }
]
```

---

### 9. Advanced Search (Multiple Optional Parameters)

**GET** `/api/customers/advanced-search`

**Example Requests:**

```
GET /api/customers/advanced-search?name=John
GET /api/customers/advanced-search?email=example.com&status=ACTIVE
GET /api/customers/advanced-search?name=Smith&status=INACTIVE
GET /api/customers/advanced-search
```

http://localhost:8080/api/customers/advanced-search?email=@example.com&status=ACTIVE

**Response:** 200 OK

```json
[
  {
    "id": 2,
    "customerCode": "C002",
    "fullName": "HoangHuy updated",
    "email": "hoanghuy2@example.com",
    "phone": "025489100212",
    "address": "so 1 duong 16",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:55:21"
  },
  {
    "id": 3,
    "customerCode": "C003",
    "fullName": "Bob Johnson",
    "email": "bob.johnson@example.com",
    "phone": "+1-555-0103",
    "address": "789 Pine Rd, Chicago",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:55:21"
  }
]
```

---

## HTTP Status Codes

| Status Code               | Description                                                |
| ------------------------- | ---------------------------------------------------------- |
| 200 OK                    | Request successful                                         |
| 201 Created               | Resource created successfully                              |
| 400 Bad Request           | Invalid request body or validation error                   |
| 404 Not Found             | Resource not found                                         |
| 409 Conflict              | Duplicate resource (email or customer code already exists) |
| 429 Too Many Requests     | Rate limit exceeded (100 requests per minute per IP)       |
| 500 Internal Server Error | Server error                                               |

---

## Status Code Examples

### 200 OK - Successful Request

**Request:**

```http
GET /api/customers/2
```

**Response:**

```json
{
  "id": 2,
  "customerCode": "C002",
  "fullName": "HoangHuy updated",
  "email": "hoanghuy2@example.com",
  "phone": "025489100212",
  "address": "so 1 duong 16",
  "status": "ACTIVE",
  "createdAt": "2025-12-02T15:55:21"
}
```

---

### 201 Created - Resource Created Successfully

**Request:**

```http
POST /api/customers
Content-Type: application/json

{
  "customerCode": "C100",
  "fullName": "Alice Williams",
  "email": "alice.williams@example.com",
  "phone": "+1234567890",
  "address": "999 Broadway St",
  "status": "ACTIVE"
}
```

**Response:**

```json
{
  "id": 74,
  "customerCode": "C010",
  "fullName": "HoangHuy",
  "email": "hoanghuy@example.com",
  "phone": "025489100212",
  "address": "so 1 duong 16",
  "status": "ACTIVE",
  "createdAt": "2025-12-09T00:23:01.771499"
}
```

---

### 400 Bad Request - Validation Error

**Request:**

```http
POST /api/customers
Content-Type: application/json

{
    "customerCode": "C010",
    "fullName": "HoangHuy",
    "email": "hoanghexample.com",
    "phone": "0254",
    "address": "so 1 duong 16"
}
```

**Response:**

```json
{
  "timestamp": "2025-12-09T00:42:51.9381992",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/customers",
  "details": [
    "phone: Invalid phone number format",
    "email: Invalid email format"
  ]
}
```

---

### 404 Not Found - Resource Not Found

**Request:**

```http
GET /api/customers/99999
```

**Response:**

```json
{
  "timestamp": "2025-12-09T00:31:24.0158181",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 12",
  "path": "/api/customers/12",
  "details": null
}
```

---

### 409 Conflict - Duplicate Resource

**Request:**

```http
POST /api/customers
Content-Type: application/json

{
  "customerCode": "C002",
  "fullName": "Duplicate User",
  "email": "newuser@example.com",
  "phone": "+1234567890",
  "address": "123 Test St",
  "status": "ACTIVE"
}
```

**Response:**

```json
{
  "timestamp": "2025-12-09T00:45:23.0641417",
  "status": 409,
  "error": "Conflict",
  "message": "Customer code already exists: C002",
  "path": "/api/customers",
  "details": null
}
```

---

### 500 Internal Server Error - Server Error

**Scenario:** Database connection failure or unexpected server error

**Request:**

```http
GET /api/customers
```

**Response:**

```json
{
  "timestamp": "2025-12-09T00:46:55.6843127",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Could not open JPA EntityManager for transaction",
  "path": "/api/customers",
  "details": null
}
```

---

### 429 Too Many Requests - Rate Limit Exceeded

**Scenario:** Client has exceeded the rate limit of 100 requests per minute

**Request:**

```http
GET /api/customers
```

**Response:**

```json
{
  "error": "Too many requests",
  "message": "Rate limit exceeded. Please try again later."
}
```

**Note:**

- The API allows 100 requests per minute per IP address
- After exceeding the limit, clients should wait before making additional requests
- Implement exponential backoff in your client to handle rate limiting gracefully
- Rate limits reset after 1 minute

**Example - After 100 requests within a minute:**

```bash
# 101st request within the same minute
curl -X GET http://localhost:8080/api/customers

# HTTP/1.1 429 Too Many Requests
# Content-Type: application/json

{
  "error": "Too many requests",
  "message": "Rate limit exceeded. Please try again later."
}
```
