# APi Specification
URL: `http://localhost:8080/api`

---

## Card APIs
Inside Cards, you can:
1. Adding Items to the cart
2. **View cart** (What will buy)
3. Re-update product's **Quantities**
4. **Remove items** in cart
5. **Checkout** - which will create an order

### 1. View carts
```
GET /api/cart
```
Show everything in carts.

Output:
```json
{
  "cartId": 1,
  "products": [
    {
      "id": 1,
      "productId": "B0001",
      "productName": "Core Java",
      "productDescription": "Books for learning Java",
      "productIcon": "https://...",
      "categoryType": 0,
      "productPrice": 30.00,
      "productStock": 96,
      "count": 2
    }
  ]
}
```

- `cartId` — unique ID of your cart
- `products` — array of items in your cart
- `id` — database ID of this cart item
- `productId` — which product (e.g., "B0001")
- `count` — how many you want to buy
- `productPrice` — price per unit
- `productStock` — how many are available in store

### 2. Merge Local Cart
```
POST /api/cart
```
When a guest (not logged in) adds items to cart (stored in browser cookies), this endpoint merges those items into the server-side cart after login.

If a product already exists in the cart, quantities are **added together**. If it's new, it's appended.

Input: Array of products (JSON body)
```json
[
  {
    "productId": "B0001",
    "productName": "Core Java",
    "productDescription": "Books for learning Java",
    "productIcon": "https://...",
    "categoryType": 0,
    "productPrice": 30.00,
    "productStock": 96,
    "count": 1
  }
]
```

Output: Updated `Cart` object (same format as "View Cart")

### 3. Add Single Product
```
POST /api/cart/add
```

Adds one product to cart.
Input: `ItemForm` (JSON body)
```json
{
  "productId": "B0001",
  "quantity": 2
}
```

- productId: String (e.g., "B0001") |
- quantity`  Integer, Min=1

Output:
```json
true
```
Returns `true` if added successfully, `false` if error.


### 4. Update Item's Quantity
```
PUT /api/cart/{itemId}
```
Change quantity of a product.
Input: `itemId` and the new number quantity
Output: Updated cart item (and updated quantity)

### 5. Remove Item fron Cart
```
DELETE /api/cart/{itemId}
```

Remove an item from cart
Input: `itemId`, productId
EX:
```
DELETE /api/cart/B0001
```

If HTTP 200, It successfully removed item.


### 6. Checkout (Create Orders)
```
POST /api/cart/checkout
```

Change status from cart to an order
(This will use JWT )
Also change card into empty (storing the last cart but the ordered one will be deleted)

---

## Order APIs
Inside each order will contain:
- Buyer's info (Name, phone, email, address)
- Products (ProductID. Quantities)
- Order Status (New, finished or cancelled)

Rule:
- Customer can only see/cancel their order
- Employee and Manager can see all order of all customer and change the status

1. List Orders
2. View Order Detail
3. Cancel Order
4. Finish Order

### 1. List Orders
```
GET /api/order?page=*16*&size=*10*
```
Show a paginated list of orders.
Input
`page`: *16* - Page Number
`size`: *10* - How much product will show in a page

Output:
```json
{
  "content": [
    {
      "orderId": 1,
      "buyerEmail": "customer1@email.com",
      "buyerName": "Customer 1",
      "buyerPhone": "0123456789",
      "buyerAddress": "123 Main St",
      "orderAmount": 60.00,
      "orderStatus": 0,
      "createTime": "2024-01-15T10:30:00",
      "updateTime": "2024-01-15T10:30:00",
      "products": [...]
    }
  ],
  "totalElements": 25,
  "totalPages": 3,
  "size": 10,
  "number": 0
}
```

- `ROLE_CUSTOMER` → only orders where `buyerEmail` matches your email
- `ROLE_EMPLOYEE` or `ROLE_MANAGER` → all orders from all customers

### 2. View Order Details
```
GET /api/order/{id}
```

Show details of an order
Input: `id` = Order ID

Output: 
```json
{
  "orderId": 1,
  "buyerEmail": "customer1@email.com",
  "buyerName": "Customer 1",
  "buyerPhone": "0123456789",
  "buyerAddress": "123 Main St",
  "orderAmount": 60.00,
  "orderStatus": 0,
  "createTime": "2024-01-15T10:30:00",
  "updateTime": "2024-01-15T10:30:00",
  "products": [
    {
      "id": 1,
      "productId": "B0001",
      "productName": "Core Java",
      "productDescription": "Books for learning Java",
      "productIcon": "https://...",
      "categoryType": 0,
      "productPrice": 30.00,
      "productStock": 96,
      "count": 2
    }
  ]
}
```

- If you're a customer and this order belongs to someone else → HTTP 401 Unauthorized
- Employees/Managers can view any order

### 3. Cancel Order
```
PATCH /api/order/cancel/{id}
```

Cancel order, restores order products stock
Input: `id` = Order ID
Output: Update user order and order status.

- Customers can only cancel their own orders → 401 if not owner
- If order is not in NEW status → error

### 10. Finish Order
```
PATCH /api/order/finish/{id}
```
Mark an order as completed/ delivered

Input: `id` - Order ID
Output: Updated `OrderMain` with `orderStatus: 1`

- **Only** `ROLE_EMPLOYEE` or `ROLE_MANAGER` can use this
- Customers are completely blocked from this endpoint
- If order is not in NEW status → error
