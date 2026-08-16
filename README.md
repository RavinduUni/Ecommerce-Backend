# 🛒 EcommerceApp — Backend API Documentation

A full-featured **Spring Boot** e-commerce backend REST API built with **Spring Data JPA**, **MapStruct**, **Lombok**, and **MySQL**.

---

## 🧰 Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Spring Boot | 4.1.0 | Framework |
| Spring Data JPA | 4.1.0 | ORM / Database layer |
| MySQL Connector | 26.7.0 | Database driver |
| MapStruct | 1.5.5.Final | Entity ↔ DTO mapping |
| Lombok | 1.18.46 | Boilerplate reduction |
| Spring Boot Actuator | — | Health & monitoring |
| Java | 21 | Language |

---

## 🗂️ Project Package Structure

```
com.ecom
├── controller/         # REST API endpoints (HTTP layer)
│   ├── CartController.java
│   ├── OrderController.java
│   ├── ProductController.java
│   └── UserController.java
├── service/            # Business logic layer
│   ├── CartService.java
│   ├── OrderService.java
│   ├── ProductService.java
│   └── UserService.java
├── repository/         # Spring Data JPA repositories
│   ├── CartItemRepository.java
│   ├── OrderRepository.java
│   ├── ProductRepository.java
│   └── UserRepository.java
├── entity/             # JPA database entities
│   ├── AddressEntity.java
│   ├── CartItemEntity.java
│   ├── OrderEntity.java
│   ├── OrderItemEntity.java
│   ├── ProductEntity.java
│   └── UserEntity.java
├── dto/                # Data Transfer Objects (API models)
│   ├── Address.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Product.java
│   └── User.java
├── mapper/             # MapStruct mappers (Entity <-> DTO)
│   ├── CartItemMapper.java
│   ├── OrderMapper.java
│   ├── ProductMapper.java
│   └── UserMapper.java
└── enums/              # Enumerations
    ├── OrderStatus.java
    └── UserRole.java
```

---

## 🔗 Entity Relationship Diagram

```
+------------------+       1:1        +-----------------+
|   UserEntity     |----------------->|  AddressEntity  |
|  (user_entity)   |   @OneToOne      |  (user_address) |
+------------------+   CASCADE ALL    +-----------------+
        |
        | 1:N (via FK on CartItemEntity)
        |
        |           N    +-----------------+
        +--------------->| CartItemEntity  |<--------------+
        |  @ManyToOne    | (cart_item...)  |   @ManyToOne  |
        |                +-----------------+               |
        |                                                  |
        | 1:N (via FK on OrderEntity)              +------------------+
        |                                          |  ProductEntity   |
        |          N    +-----------------+        |    (products)    |
        +-------------->|   OrderEntity   |        +------------------+
           @ManyToOne   +-----------------+                ^
                                |                          |
                                | 1:N @OneToMany           |
                                v                          |
                       +-----------------+   @ManyToOne   |
                       | OrderItemEntity |----------------+
                       +-----------------+
```

### Entity Relationships Summary

| Relationship | From | To | Type | Notes |
|---|---|---|---|---|
| User → Address | `UserEntity` | `AddressEntity` | `@OneToOne` | Cascade ALL, orphanRemoval |
| User → CartItems | `UserEntity` | `CartItemEntity` | `@OneToMany` (via FK) | User can have many cart items |
| Product → CartItems | `ProductEntity` | `CartItemEntity` | `@OneToMany` (via FK) | Product can be in many carts |
| User → Orders | `UserEntity` | `OrderEntity` | `@ManyToOne` (FK on order) | A user can place many orders |
| Order → OrderItems | `OrderEntity` | `OrderItemEntity` | `@OneToMany` | Cascade ALL, orphanRemoval |
| Product → OrderItems | `ProductEntity` | `OrderItemEntity` | `@ManyToOne` | One product can be in many order items |

---

## 📊 Database Tables & Fields

### `user_entity` table
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT (PK) | Auto-generated |
| `first_name` | VARCHAR | — |
| `last_name` | VARCHAR | — |
| `email` | VARCHAR | — |
| `phone` | VARCHAR | — |
| `role` | ENUM | `CUSTOMER` or `ADMIN` (default: `CUSTOMER`) |
| `address_id` | BIGINT (FK) | References `user_address.id` |
| `created_at` | DATETIME | Auto-set on create |
| `updated_at` | DATETIME | Auto-set on update |

### `user_address` table
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT (PK) | Auto-generated |
| `street` | VARCHAR | — |
| `city` | VARCHAR | — |
| `state` | VARCHAR | — |
| `country` | VARCHAR | — |
| `zipcode` | VARCHAR | — |

### `products` table
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT (PK) | Auto-generated |
| `name` | VARCHAR | — |
| `description` | TEXT | — |
| `price` | DECIMAL | — |
| `stock_quantity` | INT | — |
| `category` | VARCHAR | — |
| `image_url` | VARCHAR | — |
| `active` | BOOLEAN | Default `true` (soft delete flag) |
| `created_at` | DATETIME | Auto-set on create |
| `updated_at` | DATETIME | Auto-set on update |

### `cart_item_entity` table
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT (PK) | Auto-generated |
| `user_id` | BIGINT (FK) | References `user_entity.id` |
| `product_id` | BIGINT (FK) | References `products.id` |
| `quantity` | INT | — |
| `price` | DECIMAL | Auto-calculated: `product.price x quantity` |
| `created_at` | DATETIME | Auto-set on create |
| `updated_at` | DATETIME | Auto-set on update |

### `order_entity` table
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT (PK) | Auto-generated |
| `user_id` | BIGINT (FK) | References `user_entity.id` |
| `total_amount` | DECIMAL | Sum of all order items |
| `status` | ENUM | `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED` |
| `created_at` | DATETIME | Auto-set on create |
| `updated_at` | DATETIME | Auto-set on update |

### `order_item_entity` table
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT (PK) | Auto-generated |
| `order_id` | BIGINT (FK) | References `order_entity.id` |
| `product_id` | BIGINT (FK) | References `products.id` |
| `quantity` | INT | — |
| `price` | DECIMAL | Snapshot of price at time of order |

---

## 📦 Data Transfer Objects (DTOs)

These are the exact JSON shapes exchanged between client and server.

### `User` DTO
```json
{
  "id": 1,
  "firstName": "Ravindu",
  "lastName": "Perera",
  "email": "ravindu@example.com",
  "phone": "0771234567",
  "role": "CUSTOMER",
  "address": {
    "id": 1,
    "street": "123 Main St",
    "city": "Colombo",
    "state": "Western",
    "country": "Sri Lanka",
    "zipcode": "10100"
  },
  "createdAt": "2026-08-16T10:00:00",
  "updatedAt": "2026-08-16T10:00:00"
}
```

### `Product` DTO
```json
{
  "id": 5,
  "name": "Wireless Headphones",
  "description": "Premium noise-cancelling headphones",
  "price": 14999.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "imageUrl": "https://example.com/headphones.jpg",
  "active": true
}
```

### `CartItem` DTO
```json
{
  "id": 10,
  "user": { "id": 1, "firstName": "Ravindu", "..." : "..." },
  "product": { "id": 5, "name": "Wireless Headphones", "..." : "..." },
  "quantity": 2,
  "price": 29999.98
}
```

### `Order` DTO
```json
{
  "id": 3,
  "user": { "id": 1, "firstName": "Ravindu", "..." : "..." },
  "totalAmount": 29999.98,
  "status": "CONFIRMED",
  "items": [
    {
      "id": 7,
      "product": { "id": 5, "name": "Wireless Headphones", "..." : "..." },
      "quantity": 2,
      "price": 29999.98
    }
  ]
}
```

---

## 🌐 API Reference

> **Base URL:** `http://localhost:8080`
>
> **Authentication:** User identity is passed via the `X-User-ID` HTTP request header (user's numeric ID as a string).

---

### 👤 Users — `/api/users`

#### `GET /api/users/`
**Get all users.**

| Detail | Value |
|---|---|
| Method | `GET` |
| URL | `/api/users/` |
| Headers | None |
| Request Body | None |
| Response | `200 OK` — Array of `User` objects |

**Response Example:**
```json
[
  {
    "id": 1,
    "firstName": "Ravindu",
    "lastName": "Perera",
    "email": "ravindu@example.com",
    "phone": "0771234567",
    "role": "CUSTOMER",
    "address": {
      "id": 1,
      "street": "123 Main St",
      "city": "Colombo",
      "state": "Western",
      "country": "Sri Lanka",
      "zipcode": "10100"
    },
    "createdAt": "2026-08-16T10:00:00",
    "updatedAt": "2026-08-16T10:00:00"
  }
]
```

---

#### `GET /api/users/{id}`
**Get a single user by ID.**

| Detail | Value |
|---|---|
| Method | `GET` |
| URL | `/api/users/{id}` |
| Path Variable | `id` — User's database ID (Long) |
| Response | `200 OK` — Single `User` object |

---

#### `POST /api/users/`
**Create a new user.**

| Detail | Value |
|---|---|
| Method | `POST` |
| URL | `/api/users/` |
| Content-Type | `application/json` |
| Response | `200 OK` — Created `User` object |

**Request Body:**
```json
{
  "firstName": "Ravindu",
  "lastName": "Perera",
  "email": "ravindu@example.com",
  "phone": "0771234567",
  "role": "CUSTOMER",
  "address": {
    "street": "123 Main St",
    "city": "Colombo",
    "state": "Western",
    "country": "Sri Lanka",
    "zipcode": "10100"
  }
}
```

**Response:** Full `User` object with generated `id`, `createdAt`, `updatedAt`.

---

#### `PUT /api/users/{id}`
**Update an existing user. Null fields in the request body are ignored (partial update).**

| Detail | Value |
|---|---|
| Method | `PUT` |
| URL | `/api/users/{id}` |
| Path Variable | `id` — User's database ID |
| Content-Type | `application/json` |
| Response `200` | `"User updated successfully"` |
| Response `404` | `"User not found"` |

**Request Body (partial update example):**
```json
{
  "firstName": "Ravindu Updated",
  "phone": "0789999999"
}
```

> **Note:** The fields `id`, `address.id`, `createdAt`, and `updatedAt` are protected — they will **not** be changed even if provided in the body.

---

### 📦 Products — `/api/products`

#### `GET /api/products/`
**Get all products, including inactive ones.**

| Detail | Value |
|---|---|
| Method | `GET` |
| URL | `/api/products/` |
| Response | `200 OK` — Array of `Product` objects |

---

#### `GET /api/products/active`
**Get only active products (`active = true`).**

| Detail | Value |
|---|---|
| Method | `GET` |
| URL | `/api/products/active` |
| Response | `200 OK` — Array of active `Product` objects |

---

#### `GET /api/products/search?keyword={keyword}`
**Search active, in-stock products by name keyword (case-insensitive).**

| Detail | Value |
|---|---|
| Method | `GET` |
| URL | `/api/products/search` |
| Query Param | `keyword` — Search term matched against product names |
| Response | `200 OK` — Array of matching `Product` objects |

> Internally runs: `WHERE active = true AND stockQuantity > 0 AND LOWER(name) LIKE LOWER('%keyword%')`

**Example:** `GET /api/products/search?keyword=headphone`

---

#### `POST /api/products/`
**Create a new product.**

| Detail | Value |
|---|---|
| Method | `POST` |
| URL | `/api/products/` |
| Content-Type | `application/json` |
| Response | `201 Created` — Created `Product` object |

**Request Body:**
```json
{
  "name": "Wireless Headphones",
  "description": "Premium noise-cancelling headphones",
  "price": 14999.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "imageUrl": "https://example.com/headphones.jpg",
  "active": true
}
```

---

#### `PUT /api/products/{id}`
**Update an existing product.**

| Detail | Value |
|---|---|
| Method | `PUT` |
| URL | `/api/products/{id}` |
| Path Variable | `id` — Product's database ID |
| Response `200` | `"Product updated successfully"` |
| Response `404` | `"Product not found"` |

**Request Body (partial update example):**
```json
{
  "price": 12999.99,
  "stockQuantity": 40
}
```

---

#### `DELETE /api/products/{id}`
**Soft-delete a product (sets `active = false`). The product is NOT physically removed from the database.**

| Detail | Value |
|---|---|
| Method | `DELETE` |
| URL | `/api/products/{id}` |
| Path Variable | `id` — Product's database ID |
| Response `200` | `"Product deleted successfully"` |
| Response `404` | `"Product not found"` |

---

### 🛒 Cart — `/api/cart`

> All cart endpoints use the `X-User-ID` header to identify the user.

#### `POST /api/cart/`
**Add a product to the user's cart. If the product already exists in the cart, quantity is incremented instead.**

| Detail | Value |
|---|---|
| Method | `POST` |
| URL | `/api/cart/` |
| Header | `X-User-ID: {userId}` (required) |
| Content-Type | `application/json` |
| Response | `200 OK` — String message |

**Request Body:**
```json
{
  "product": { "id": 5 },
  "quantity": 2
}
```

> **Business Logic:**
> - Validates product exists and has sufficient stock (`stockQuantity >= requested quantity`).
> - Validates user exists.
> - If product already in cart → quantity is incremented and price is recalculated.
> - If new item → new cart entry created.
> - Price is **server-calculated** as `product.price x quantity`. Client-provided `price` is **ignored**.
> - Client-provided `user` field is also **ignored** — user is resolved from the `X-User-ID` header.

**Response Messages:**

| Response | Meaning |
|---|---|
| `"Item added successfully"` | Success |
| `"Product not found"` | Product ID does not exist |
| `"Stock Limit exceed"` | Requested quantity exceeds available stock |
| `"User not found"` | User ID from header does not exist |

---

#### `DELETE /api/cart/{productId}`
**Remove a specific product from the user's cart.**

| Detail | Value |
|---|---|
| Method | `DELETE` |
| URL | `/api/cart/{productId}` |
| Header | `X-User-ID: {userId}` (required) |
| Path Variable | `productId` — The product ID to remove |
| Response | `200 OK` — String message |

**Response Messages:**

| Response | Meaning |
|---|---|
| `"Item removed successfully"` | Success |
| `"User not found"` | User ID from header does not exist |
| `"Product not found"` | Product ID does not exist |
| `"Item not found in cart"` | This product is not in the user's cart |

---

#### `GET /api/cart/`
**Get ALL cart items across all users. Intended for admin/debug use.**

| Detail | Value |
|---|---|
| Method | `GET` |
| URL | `/api/cart/` |
| Headers | None required |
| Response | `200 OK` — Array of all `CartItem` objects |

**Response Example:**
```json
[
  {
    "id": 10,
    "user": { "id": 1, "firstName": "Ravindu", "lastName": "Perera", "..." : "..." },
    "product": { "id": 5, "name": "Wireless Headphones", "price": 14999.99, "..." : "..." },
    "quantity": 2,
    "price": 29999.98
  }
]
```

---

#### `GET /api/cart/items`
**Get all cart items for a specific user.**

| Detail | Value |
|---|---|
| Method | `GET` |
| URL | `/api/cart/items` |
| Header | `X-User-ID: {userId}` (required) |
| Response | `200 OK` — Array of `CartItem` objects for that user |

---

### 📋 Orders — `/api/order`

#### `POST /api/order/`
**Place an order from the user's current cart contents.**

| Detail | Value |
|---|---|
| Method | `POST` |
| URL | `/api/order/` |
| Header | `X-User-ID: {userId}` (required) |
| Request Body | None |
| Response `201` | Created `Order` object |
| Response `400` | Empty body — cart is empty or user not found |

> **Business Logic (runs in a single @Transactional block):**
> 1. Fetches all cart items for the user.
> 2. Returns `400 Bad Request` if the cart is empty or the user is not found.
> 3. Calculates total order price by summing all cart item prices.
> 4. Creates a new `OrderEntity` with status immediately set to `CONFIRMED`.
> 5. Converts each cart item into an `OrderItemEntity` (price is snapshotted at checkout time).
> 6. Saves the order and all order items (cascade).
> 7. **Automatically clears the user's cart** after the order is successfully saved.

**Response Example:**
```json
{
  "id": 3,
  "user": {
    "id": 1,
    "firstName": "Ravindu",
    "lastName": "Perera",
    "email": "ravindu@example.com",
    "phone": "0771234567",
    "role": "CUSTOMER",
    "address": {
      "id": 1,
      "street": "123 Main St",
      "city": "Colombo",
      "state": "Western",
      "country": "Sri Lanka",
      "zipcode": "10100"
    },
    "createdAt": "2026-08-16T10:00:00",
    "updatedAt": "2026-08-16T10:00:00"
  },
  "totalAmount": 29999.98,
  "status": "CONFIRMED",
  "items": [
    {
      "id": 7,
      "product": {
        "id": 5,
        "name": "Wireless Headphones",
        "description": "Premium noise-cancelling headphones",
        "price": 14999.99,
        "stockQuantity": 50,
        "category": "Electronics",
        "imageUrl": "https://example.com/headphones.jpg",
        "active": true
      },
      "quantity": 2,
      "price": 29999.98
    }
  ]
}
```

---

## 🔄 Enumerations

### `UserRole`
| Value | Description |
|---|---|
| `CUSTOMER` | Default role for all users |
| `ADMIN` | Administrative access |

### `OrderStatus`
| Value | Description |
|---|---|
| `PENDING` | Default status (not yet assigned at order creation) |
| `CONFIRMED` | Set immediately when an order is created via checkout |
| `SHIPPED` | Order has been dispatched |
| `DELIVERED` | Order received by the customer |

---

## 🔄 Typical User Flow

```
1. Create User
   POST /api/users/
        |
        v
2. Browse Products
   GET  /api/products/active
   GET  /api/products/search?keyword=...
        |
        v
3. Add to Cart (repeat as needed)
   POST /api/cart/    [Header: X-User-ID: 1]
        |
        v
4. View Cart
   GET  /api/cart/items    [Header: X-User-ID: 1]
        |
        v
5. Remove from Cart (optional)
   DELETE /api/cart/{productId}    [Header: X-User-ID: 1]
        |
        v
6. Place Order
   POST /api/order/    [Header: X-User-ID: 1]
   +----------------------------------------------+
   |  Cart is cleared automatically               |
   |  Order status is set to CONFIRMED            |
   |  Prices are snapshotted in order items       |
   +----------------------------------------------+
```

---

## 🏗️ Architecture Layers

```
HTTP Request
     |
     v
+------------------------------------------------------------+
|                     Controller Layer                      |
|  CartController / OrderController /                       |
|  ProductController / UserController                       |
|  (Handles HTTP, headers, path/query params, responses)    |
+------------------------------------------------------------+
     |
     v  (passes/receives DTOs: User, Product, CartItem, Order)
+------------------------------------------------------------+
|                      Service Layer                        |
|  CartService / OrderService /                             |
|  ProductService / UserService                             |
|  (Business logic, validation, orchestration)              |
+------------------------------------------------------------+
     |                            |
     v  (uses Entities)           v  (uses MapStruct Mappers)
+------------------+    +-------------------------------+
|  Repository      |    | CartItemMapper / OrderMapper /|
|  Layer           |    | ProductMapper / UserMapper    |
|  (JPA CRUD)      |    | (Entity <-> DTO conversion)   |
+------------------+    +-------------------------------+
     |
     v
+------------------------------------------------------------+
|                      MySQL Database                       |
+------------------------------------------------------------+
```

---

## ⚠️ Notable Behaviors & Design Decisions

| Behavior | Detail |
|---|---|
| **Soft Delete (Products)** | `DELETE /api/products/{id}` sets `active = false`. Products are never physically removed from the DB. |
| **Price Calculation (Cart)** | Price is **server-calculated** as `product.price x quantity`. Any `price` sent by the client in the cart request body is **ignored**. |
| **Price Snapshot (Orders)** | When an order is placed, the price from each cart item is copied into the order item. Future product price changes do **not** affect past orders. |
| **Cart Accumulation** | Adding the same product twice increments the quantity — does not create a duplicate cart entry. |
| **Cart Auto-Clear** | After a successful order is placed (`POST /api/order/`), the user's entire cart is automatically cleared within the same transaction. |
| **Null-Safe Updates** | `PUT` on users and products uses `NullValuePropertyMappingStrategy.IGNORE` — null fields in the request body are **not** overwritten. |
| **User Identity** | No JWT or session-based auth. User is identified via `X-User-ID` header on cart and order endpoints. |
| **Order Status on Creation** | New orders are immediately set to `CONFIRMED` (not `PENDING`) upon successful checkout. |
| **Search Filters** | Product search only returns results where `active = true` AND `stockQuantity > 0`. |

---

## 📋 Quick API Summary Table

| Method | Endpoint | Header | Body | Response | Description |
|---|---|---|---|---|---|
| `GET` | `/api/users/` | — | — | `User[]` | Get all users |
| `GET` | `/api/users/{id}` | — | — | `User` | Get user by ID |
| `POST` | `/api/users/` | — | `User` | `User` | Create user |
| `PUT` | `/api/users/{id}` | — | `User` | `String` | Update user |
| `GET` | `/api/products/` | — | — | `Product[]` | Get all products |
| `GET` | `/api/products/active` | — | — | `Product[]` | Get active products |
| `GET` | `/api/products/search?keyword=` | — | — | `Product[]` | Search products by name |
| `POST` | `/api/products/` | — | `Product` | `Product` | Create product |
| `PUT` | `/api/products/{id}` | — | `Product` | `String` | Update product |
| `DELETE` | `/api/products/{id}` | — | — | `String` | Soft-delete product |
| `POST` | `/api/cart/` | `X-User-ID` | `CartItem` | `String` | Add item to cart |
| `DELETE` | `/api/cart/{productId}` | `X-User-ID` | — | `String` | Remove item from cart |
| `GET` | `/api/cart/` | — | — | `CartItem[]` | Get all cart items (all users) |
| `GET` | `/api/cart/items` | `X-User-ID` | — | `CartItem[]` | Get cart for specific user |
| `POST` | `/api/order/` | `X-User-ID` | — | `Order` | Place order from cart |
