# Game Shop Backend - API Documentation

## 🚀 Tổng Quan Hệ Thống

Hệ thống game shop backend hoàn chỉnh với các tính năng:
- ✅ Xác thực & phân quyền (JWT Auth)
- ✅ Quản lý sản phẩm & game
- ✅ Hệ thống ví & thanh toán
- ✅ Đặt hàng và giao hàng tự động
- ✅ Quản lý kho hàng (KEY/ACCOUNT)
- ✅ Quản lý admin
- ✅ Tích hợp MinIO (lưu trữ ảnh)
- ✅ Tích hợp PayPal + Card/ATM/Momo

---

## 📋 API Endpoints

### 1️⃣ AUTHENTICATION & USER

#### Đăng ký
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "id": 1,
  "username": "user123",
  "email": "user@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "USER"
}
```

#### Đăng nhập
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "user123",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "user123",
  "role": "USER"
}
```

#### Đăng xuất
```
POST /api/auth/logout
Authorization: Bearer <token>

Response:
{
  "message": "Đăng xuất thành công"
}
```

#### Làm mới Token
```
POST /api/auth/refresh-token
Authorization: Bearer <token>

Response:
{
  "message": "Token được làm mới",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Lấy Profile User
```
GET /api/user/profile/{userId}
Authorization: Bearer <token>

Response:
{
  "id": 1,
  "username": "user123",
  "email": "user@example.com",
  "role": "USER",
  "status": true,
  "createdAt": "2026-02-21T10:30:00"
}
```

#### Kiểm tra User Tồn Tại
```
GET /api/user/exists/{username}

Response:
{
  "username": "user123",
  "exists": true
}
```

---

### 2️⃣ GAMES (Quản lý Game)

#### Lấy Danh Sách Game
```
GET /api/games?page=0&size=10&sortBy=id&direction=asc
Authorization: Bearer <token>

Response: (Paginated)
{
  "content": [
    {
      "id": 1,
      "name": "Game Name",
      "slug": "game-name",
      "thumbnail": "http://localhost:9000/product-images/...",
      "description": "Game description",
      "status": true,
      "createdAt": "2026-02-21T10:30:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 5
}
```

#### Lấy Game Theo ID
```
GET /api/games/{id}
Authorization: Bearer <token>

Response:
{
  "id": 1,
  "name": "Game Name",
  "slug": "game-name",
  "thumbnail": "...",
  "description": "...",
  "status": true,
  "createdAt": "2026-02-21T10:30:00"
}
```

#### Lấy Game Theo Slug
```
GET /api/games/slug/{slug}
Authorization: Bearer <token>
```

#### Tạo Game (Admin Only)
```
POST /api/games
Authorization: Bearer <admin-token>
Content-Type: multipart/form-data

data: {
  "name": "New Game",
  "slug": "new-game",
  "description": "Game description",
  "status": true
}
file: <image_file>

Response: (Same as GET)
```

#### Cập Nhật Game (Admin Only)
```
PATCH /api/games/{id}
Authorization: Bearer <admin-token>
Content-Type: multipart/form-data

data: {
  "name": "Updated Name",
  "description": "..."
}
file: <image_file> (optional)
```

#### Thay Đổi Trạng Thái Game (Admin Only)
```
PATCH /api/games/{id}/status?status=false
Authorization: Bearer <admin-token>

Response:
{
  "message": "Cập nhật trạng thái game thành công"
}
```

#### Xóa Game (Admin Only)
```
DELETE /api/games/{id}
Authorization: Bearer <admin-token>

Response:
{
  "message": "Xóa game thành công"
}
```

---

### 3️⃣ PRODUCTS (Quản lý Sản Phẩm)

#### Lấy Danh Sách Sản Phẩm
```
GET /api/products?page=0&size=10&sortBy=id&direction=asc
Authorization: Bearer <token>

Response: (Paginated)
{
  "content": [
    {
      "id": 1,
      "game": { "id": 1, "name": "Game Name" },
      "type": "KEY",
      "platform": "ALL",
      "title": "Game KEY",
      "shortDescription": "...",
      "description": "...",
      "price": 50000,
      "thumbnail": "...",
      "slug": "game-key",
      "status": true,
      "createdAt": "2026-02-21T10:30:00"
    }
  ]
}
```

#### Lấy Sản Phẩm Theo Slug
```
GET /api/products/slug/{slug}
Authorization: Bearer <token>
```

#### Lấy Sản Phẩm Theo Game
```
GET /api/products/game/{gameId}
Authorization: Bearer <token>

Response: Array of products
```

#### Tạo Sản Phẩm (Admin Only)
```
POST /api/products
Authorization: Bearer <admin-token>
Content-Type: multipart/form-data

data: {
  "gameId": 1,
  "type": "KEY",
  "platform": "ALL",
  "title": "Game KEY",
  "shortDescription": "...",
  "description": "...",
  "price": 50000,
  "slug": "game-key",
  "status": true
}
file: <image_file>
```

#### Thay Đổi Trạng Thái Sản Phẩm (Admin Only)
```
PATCH /api/products/{id}/status?status=false
Authorization: Bearer <admin-token>
```

---

### 4️⃣ ORDERS (Đặt Hàng)

#### Tạo Đơn Hàng (Cart)
```
POST /api/orders
Authorization: Bearer <token>
Content-Type: application/json
?userId=1

{
  "items": [
    {
      "productId": 1,
      "quantity": 1
    }
  ]
}

Response:
{
  "id": 1,
  "userId": 1,
  "totalAmount": 50000,
  "status": "PENDING",
  "createdAt": "2026-02-21T10:30:00",
  "items": [...]
}
```

#### Lấy Cart
```
GET /api/orders/cart?userId=1
Authorization: Bearer <token>

Response: Array of PENDING orders
```

#### Mua Ngay (Buy Now)
```
POST /api/orders/buy-now
Authorization: Bearer <token>
Content-Type: application/json
?userId=1

{
  "items": [
    {
      "productId": 1,
      "quantity": 1
    }
  ]
}

Response:
{
  "id": 1,
  "status": "SUCCESS",
  "items": [
    {
      "type": "KEY",
      "deliveryKey": "KEY-12345-ABCDE",
      "deliveryValue": null,
      "note": "Key sẽ hết hạn trong 7 ngày"
    }
  ]
}
```

#### Hủy Đơn Hàng
```
DELETE /api/orders/{orderId}/cancel
Authorization: Bearer <token>

Response:
{
  "message": "Order cancelled"
}
```

---

### 5️⃣ WALLET (Quản Lý Ví)

#### Lấy Số Dư Ví
```
GET /api/wallet/balance?userId=1
Authorization: Bearer <token>

Response:
{
  "userId": 1,
  "balance": 500000
}
```

#### Lấy Lịch Sử Ví
```
GET /api/wallet/logs?userId=1
Authorization: Bearer <token>

Response: Array of wallet logs
[
  {
    "id": 1,
    "type": "TOPUP",
    "amount": 100000,
    "balanceBefore": 400000,
    "balanceAfter": 500000,
    "createdAt": "2026-02-21T10:30:00"
  }
]
```

#### Nạp Tiền
```
POST /api/wallet/topup
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "amount": 100000,
  "method": "PAYPAL"
}

Response:
{
  "message": "Nạp tiền thành công",
  "newBalance": 500000
}
```

#### Chuyển Tiền
```
POST /api/wallet/transfer
Authorization: Bearer <token>
Content-Type: application/json

{
  "fromUserId": 1,
  "toUserId": 2,
  "amount": 50000
}

Response:
{
  "message": "Chuyển tiền thành công"
}
```

---

### 6️⃣ PAYMENTS (Thanh Toán)

#### Thanh Toán PayPal
```
POST /api/payment/paypal/create-order
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "amount": 50000
}

Response:
{
  "id": "paypal-order-id",
  "status": "CREATED"
}
```

#### Xác Nhận Thanh Toán PayPal
```
POST /api/payment/paypal/capture
Authorization: Bearer <token>
Content-Type: application/json

{
  "orderId": "paypal-order-id"
}

Response:
{
  "status": "COMPLETED",
  "message": "Thanh toán thành công"
}
```

#### Thanh Toán Bằng Thẻ (Card)
```
POST /api/payment/card
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "amount": 50000,
  "cardNumber": "4532 1234 5678 9010",
  "cardHolderName": "JOHN DOE",
  "expiryDate": "12/25",
  "cvv": "123",
  "bankCode": "VISA"
}

Response:
{
  "message": "Thanh toán bằng thẻ thành công",
  "transactionId": "CARD_...",
  "status": "SUCCESS"
}
```

#### Thanh Toán Bằng ATM/Ngân Hàng
```
POST /api/payment/atm
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "amount": 50000,
  "bankCode": "VIETCOMBANK",
  "bankAccountNumber": "0123456789",
  "bankAccountName": "GAME SHOP",
  "description": "Nạp tiền game shop"
}

Response:
{
  "message": "Yêu cầu thanh toán ATM được tạo",
  "transactionId": "ATM_...",
  "status": "PENDING",
  "instruction": "Vui lòng chuyển khoản đến tài khoản được cung cấp"
}
```

#### Thanh Toán Bằng Momo
```
POST /api/payment/momo
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "amount": 50000,
  "phoneNumber": "0912345678",
  "description": "Nạp tiền game shop"
}

Response:
{
  "message": "Thanh toán Momo thành công",
  "transactionId": "MOMO_...",
  "status": "SUCCESS"
}
```

---

### 7️⃣ INVENTORY (Quản Lý Kho)

#### Nhập Keys Hàng Loạt
```
POST /api/inventory/keys/import
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "productId": 1,
  "keys": [
    "KEY-12345-ABCDE",
    "KEY-12345-BCDEF",
    "KEY-12345-CDEFG"
  ]
}

Response:
{
  "message": "Import keys thành công",
  "count": 3,
  "productId": 1
}
```

#### Nhập Accounts Hàng Loạt
```
POST /api/inventory/accounts/import
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "productId": 1,
  "accounts": [
    {
      "username": "user1",
      "password": "pass123"
    },
    {
      "username": "user2",
      "password": "pass456"
    }
  ]
}

Response:
{
  "message": "Import accounts thành công",
  "count": 2,
  "productId": 1
}
```

#### Lấy Thống Kê Kho Sản Phẩm
```
GET /api/inventory/stats/{productId}
Authorization: Bearer <admin-token>

Response:
{
  "productId": 1,
  "productName": "Game KEY",
  "availableKeys": 100,
  "soldKeys": 50,
  "availableAccounts": 0,
  "soldAccounts": 0,
  "totalInventory": 150
}
```

#### Lấy Thống Kê Kho Toàn Bộ
```
GET /api/inventory/stats
Authorization: Bearer <admin-token>

Response: Array of stats
```

#### Xóa Key Từ Kho
```
DELETE /api/inventory/keys/{keyId}
Authorization: Bearer <admin-token>

Response:
{
  "message": "Xóa key thành công"
}
```

#### Xóa Account Từ Kho
```
DELETE /api/inventory/accounts/{accountId}
Authorization: Bearer <admin-token>

Response:
{
  "message": "Xóa account thành công"
}
```

---

### 8️⃣ ADMIN (Quản Lý Admin)

#### Lấy Danh Sách Users
```
GET /api/admin/users?page=0&size=20
Authorization: Bearer <admin-token>

Response: (Paginated)
```

#### Lấy Chi Tiết User
```
GET /api/admin/users/{id}
Authorization: Bearer <admin-token>
```

#### Thay Đổi Trạng Thái User (Khóa/Mở)
```
PATCH /api/admin/users/{id}/status?status=false
Authorization: Bearer <admin-token>

Response:
{
  "message": "Cập nhật trạng thái user thành công",
  "userId": 1,
  "status": false
}
```

#### Lấy Lịch Sử Mua Của User
```
GET /api/admin/users/{id}/orders
Authorization: Bearer <admin-token>

Response: Array of orders
```

#### Xem Ví & Giao Dịch User
```
GET /api/admin/users/{id}/wallet
Authorization: Bearer <admin-token>

Response:
{
  "userId": 1,
  "balance": 500000
}
```

#### Lấy Danh Sách Đơn Hàng
```
GET /api/admin/orders?page=0&size=20
Authorization: Bearer <admin-token>

Response: (Paginated)
```

#### Lấy Chi Tiết Đơn Hàng
```
GET /api/admin/orders/{id}
Authorization: Bearer <admin-token>
```

---

## 🔑 Authentication

Tất cả các endpoint (trừ register/login) yêu cầu JWT token trong header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Token được cấp khi đăng nhập/đăng ký và có thời hạn 24 giờ.

---

## 👥 Roles & Permissions

- **USER**: Mua hàng, xem profile, quản lý ví
- **ADMIN**: Quản lý game, sản phẩm, users, orders, inventory
- **RESELLER**: Tương tự USER + thêm quyền bán hàng riêng

---

## 🛒 Order Flow Example

1. **Đăng ký/Đăng nhập**
   ```
   POST /api/auth/register → Get token
   ```

2. **Xem Game & Sản Phẩm**
   ```
   GET /api/games
   GET /api/products/game/{gameId}
   ```

3. **Mua Hàng (Mua Ngay)**
   ```
   POST /api/orders/buy-now
   → Kiểm tra ví
   → Trừ tiền
   → Tự động giao hàng (Key/Account)
   ```

4. **Hoặc Thêm Vào Cart Rồi Thanh Toán**
   ```
   POST /api/orders (add to cart)
   POST /api/payment/* (thanh toán)
   ```

---

## 📁 File Upload

Tất cả uploads ảnh được lưu trữ trên MinIO:
- Games: `/product-images/games/`
- Products: `/product-images/products/`

Response trả về public URL để download.

---

## ⚠️ Error Handling

Tất cả lỗi trả về format:
```json
{
  "error": "Error message",
  "detail": "Error details"
}
```

HTTP Status Codes:
- `200`: Success
- `201`: Created
- `400`: Bad Request
- `401`: Unauthorized
- `403`: Forbidden
- `404`: Not Found
- `500`: Internal Server Error

---

## 🔒 Security Notes

- Luôn sử dụng HTTPS trong production
- Bảo vệ CVV/Card details - luôn encrypt
- Validate input trên server
- Rate limiting nên được implement
- CORS được cấu hình cho frontend URLs

---

Chúc bạn phát triển thành công! 🚀

