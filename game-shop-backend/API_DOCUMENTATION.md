# API Documentation - Game Shop Backend

## 1. Tổng quan

- Base URL (local): `http://localhost:8080`
- Content-Type mặc định: `application/json`
- File upload: `multipart/form-data`

### Lưu ý về authentication

- Backend đã bật JWT filter và phân quyền theo role.
- Header bắt buộc với endpoint cần đăng nhập:

```http
Authorization: Bearer <token>
```

- Role trong hệ thống:
- `ADMIN`: quản trị hệ thống, quản lý game/product/inventory/users/orders.
- `CLIENT`: nhóm người dùng sử dụng ứng dụng, tương ứng role `USER` và `RESELLER`.

---

## 2. Enum dùng trong API

- `Role`: `USER | ADMIN | RESELLER`
- `ProductType`: `KEY | ACCOUNT`
- `Platform`: `ANDROID | IOS | ALL`
- `DurationUnit`: `MINUTE | HOUR | DAY | MONTH`
- `OrderStatus`: `PENDING | SUCCESS | FAILED`

---

## 3. Authentication APIs (`/api/auth`)

### 3.1 Register

- `POST /api/auth/register`
- Body:

```json
{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123"
}
```

### 3.2 Login

- `POST /api/auth/login`
- Body:

```json
{
  "username": "user123",
  "password": "password123"
}
```

- Response mẫu:

```json
{
  "token": "<jwt>",
  "userId": 1,
  "username": "user123",
  "role": "USER",
  "balance": 0
}
```

### 3.3 Logout

- `POST /api/auth/logout`

### 3.4 Refresh token

- `POST /api/auth/refresh-token`
- Header: `Authorization: Bearer <token>`
- Response: trả về access token mới (không trả lại token cũ).

---

## 4. User APIs (`/api/user`)

### 4.1 Lấy profile theo userId

- `GET /api/user/profile/{userId}`
- `CLIENT`: chỉ xem được profile của chính mình.
- `ADMIN`: xem được mọi user.

### 4.2 Kiểm tra username tồn tại

- `GET /api/user/exists/{username}`
- Response mẫu:

```json
{
  "username": "user123",
  "exists": true
}
```

---

## 5. Game APIs (`/api/games`)

### 5.1 Danh sách game active (phân trang)

- `GET /api/games?page=0&size=10&sortBy=id&direction=asc`

### 5.2 Chi tiết game theo id

- `GET /api/games/{id}`

### 5.3 Chi tiết game theo slug

- `GET /api/games/slug/{slug}`

### 5.4 Tạo game (multipart)

- `POST /api/games`
- Content-Type: `multipart/form-data`
- Parts:
- `data`: JSON string của `GameRequest`
- `file` (optional): ảnh thumbnail

`GameRequest`:

```json
{
  "name": "Valorant",
  "slug": "valorant",
  "thumbnail": "https://...",
  "description": "FPS Game",
  "status": true
}
```

### 5.5 Cập nhật game (multipart)

- `PATCH /api/games/{id}`
- Content-Type: `multipart/form-data`
- Parts giống create

### 5.6 Đổi trạng thái game

- `PATCH /api/games/{id}/status?status=true`

### 5.7 Xóa game

- `DELETE /api/games/{id}`

---

## 6. Product APIs (`/api/products`)

### 6.1 Tạo sản phẩm (multipart)

- `POST /api/products`
- Quyền: `ADMIN`
- Content-Type: `multipart/form-data`
- Form-data:
- `data` (bắt buộc): JSON string theo `ProductRequest`
- `file` (không bắt buộc): ảnh thumbnail. Nếu có file, BE sẽ upload và tự gán vào `thumbnail`.
- Validate bắt buộc trong BE: `gameId`, `title`, `slug`

Ví dụ `data`:

```json
{
  "gameId": 1,
  "type": "KEY",
  "platform": "ALL",
  "title": "Valorant Points 475",
  "shortDescription": "Gói nạp nhanh",
  "description": "Mô tả chi tiết",
  "thumbnail": "https://...",
  "slug": "valorant-points-475",
  "status": true,
  "packages": [
    {
      "name": "Mặc định",
      "price": 120000,
      "durationValue": 30,
      "durationUnit": "DAY"
    }
  ]
}
```

Ví dụ cURL:

```bash
curl -X POST 'http://localhost:8080/api/products' \
  -H 'Authorization: Bearer <admin_token>' \
  -F 'data={
    "gameId":1,
    "type":"KEY",
    "platform":"ALL",
    "title":"Valorant Points 475",
    "shortDescription":"Gói nạp nhanh",
    "description":"Mô tả chi tiết",
    "slug":"valorant-points-475",
    "status":true,
    "packages":[{"name":"Mặc định","price":120000,"durationValue":30,"durationUnit":"DAY"}]
  }' \
  -F 'file=@/path/to/image.png'
```

Response thành công: `201 Created` (trả về `ProductResponse`).

### 6.2 Danh sách sản phẩm active (phân trang)

- `GET /api/products?page=0&size=10&sortBy=id&direction=asc`

### 6.3 Danh sách sản phẩm theo game

- `GET /api/products/game/{gameId}`

### 6.4 Chi tiết sản phẩm theo slug

- `GET /api/products/slug/{slug}`

### 6.5 Đổi trạng thái sản phẩm

- `PATCH /api/products/{id}/status?status=true`

---

## 7. Order APIs (`/api/orders`)

### 7.1 Health check order module

- `GET /api/orders/ping`

### 7.2 Lịch sử mua hàng (phân trang)

- `GET /api/orders/history?page=0&size=5`
- API dùng `@AuthenticationPrincipal` để lấy user hiện tại.

### 7.3 Mua ngay

- `POST /api/orders/buy-now`
- `CLIENT`: mua cho chính mình (không cần `userId`).
- `ADMIN`: có thể truyền `?userId={id}` để mua thay user.
- Body (`CreateOrderRequest`):

```json
{
  "items": [
    {
      "productId": 1,
      "packageId": 1,
      "quantity": 1
    }
  ]
}
```

- Response mẫu (`OrderResponse`):

```json
{
  "id": 10,
  "userId": 1,
  "username": "user123",
  "totalAmount": 120000,
  "status": "SUCCESS",
  "createdAt": "2026-03-03T10:30:00",
  "items": [
    {
      "productId": 1,
      "productName": "Valorant Points 475",
      "quantity": 1,
      "price": 120000,
      "key": "XXXX-YYYY-ZZZZ",
      "username": null,
      "password": null
    }
  ]
}
```

---

## 8. Wallet APIs (`/api/wallet`)

Các API dưới đây đọc user từ `@AuthenticationPrincipal`.

### 8.1 Số dư ví

- `GET /api/wallet/balance`

### 8.2 Lịch sử ví

- `GET /api/wallet/logs`

### 8.3 Nạp tiền ví thủ công

- `POST /api/wallet/topup`
- Body:

```json
{
  "amount": 100000
}
```

### 8.4 Chuyển tiền

- `POST /api/wallet/transfer`
- Body:

```json
{
  "toUserId": 2,
  "amount": 50000
}
```

---

## 9. Payment APIs (`/api/payment`)

### 9.1 Thanh toán bằng thẻ

- `POST /api/payment/card`
- Body:

```json
{
  "userId": 1,
  "amount": 200000,
  "cardNumber": "4111111111111111",
  "cardHolderName": "NGUYEN VAN A",
  "expiryDate": "12/29",
  "cvv": "123",
  "bankCode": "VISA"
}
```

### 9.2 Thanh toán ATM (tạo yêu cầu)

- `POST /api/payment/atm`
- Body:

```json
{
  "userId": 1,
  "amount": 200000,
  "bankCode": "VCB",
  "bankAccountNumber": "0123456789",
  "bankAccountName": "NGUYEN VAN A",
  "description": "NAP TIEN"
}
```

### 9.3 Thanh toán Momo

- `POST /api/payment/momo`
- Body:

```json
{
  "userId": 1,
  "amount": 200000,
  "phoneNumber": "0901234567",
  "description": "Nap vi"
}
```

---

## 10. PayPal APIs (`/api/paypal`)

Các API này kiểm tra `@AuthenticationPrincipal` trước khi xử lý.

### 10.1 Tạo PayPal order

- `POST /api/paypal/create-order`
- Body:

```json
{
  "amount": 10
}
```

### 10.2 Capture PayPal order

- `POST /api/paypal/capture`
- Body:

```json
{
  "orderId": "5O190127TN364715T"
}
```

- Response mẫu:

```json
{
  "message": "Nạp tiền thành công",
  "amountVnd": 250000
}
```

---

## 11. SePay Webhook APIs (`/api/webhook`)

### 11.1 Nhận webhook nạp tiền từ SePay

- `POST /api/webhook/sepay`
- Body mẫu tối thiểu:

```json
{
  "id": "BANK_TX_001",
  "transferAmount": 100000,
  "content": "NAP123ABC"
}
```

### 11.2 Lấy thông tin nạp tiền/QR

- `GET /api/webhook/wallet/deposit-info`
- API dùng `Authentication auth` để lấy user hiện tại.

---

## 12. File Upload API (`/api/files`)

### 12.1 Upload file

- `POST /api/files/upload`
- Content-Type: `multipart/form-data`
- Param: `file`
- Response: URL public của file

---

## 13. Inventory APIs (`/api/inventory`)

### 13.1 Import key hàng loạt

- `POST /api/inventory/keys/import`
- Body:

```json
{
  "productId": 1,
  "keys": ["KEY-AAA-111", "KEY-BBB-222"]
}
```

### 13.2 Import account hàng loạt

- `POST /api/inventory/accounts/import`
- Body:

```json
{
  "productId": 2,
  "accounts": [
    {
      "username": "acc_1",
      "password": "pass_1"
    }
  ]
}
```

### 13.3 Thống kê kho theo sản phẩm

- `GET /api/inventory/stats/{productId}`

### 13.4 Thống kê kho toàn bộ sản phẩm

- `GET /api/inventory/stats`

### 13.5 Xóa key trong kho

- `DELETE /api/inventory/keys/{keyId}`

### 13.6 Xóa account trong kho

- `DELETE /api/inventory/accounts/{accountId}`

---

## 14. Admin APIs (`/api/admin`)

### 14.1 Quản lý user

- `GET /api/admin/users?page=0&size=20`
- `GET /api/admin/users/{id}`
- `PATCH /api/admin/users/{id}/status?status=true`
- `GET /api/admin/users/{id}/orders`
- `GET /api/admin/users/{id}/wallet`

### 14.2 Quản lý order

- `GET /api/admin/orders?page=0&size=20`
- `GET /api/admin/orders/{id}`

### 14.3 Quản lý inventory (admin namespace)

- `POST /api/admin/inventory/import-keys`
- `POST /api/admin/inventory/import-accounts`
- `GET /api/admin/inventory/stats/{productId}`
- `GET /api/admin/inventory/stats`
- `DELETE /api/admin/inventory/keys/{keyId}`
- `DELETE /api/admin/inventory/accounts/{accountId}`

Body import tương tự mục Inventory APIs.

---

## 15. Test API

- `GET /ping`

---

## 16. Mẫu lỗi thường gặp

- 400 Bad Request

```json
{
  "error": "Thông điệp validate/business"
}
```

- 404 Not Found

```json
{
  "error": "Không tìm thấy dữ liệu"
}
```

- 500 Internal Server Error

```json
{
  "error": "Mô tả lỗi tổng quát",
  "detail": "Chi tiết lỗi"
}
```

---

## 17. Ma trận phân quyền (Admin vs Client)

- Public (không cần token):
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /ping`
- `GET /api/orders/ping`
- `GET /api/games/**`
- `GET /api/products/**`
- `POST /api/webhook/sepay`

- Client + Admin (cần token):
- `POST /api/auth/logout`
- `POST /api/auth/refresh-token`
- `/api/user/**`
- `/api/orders/history`
- `/api/orders/buy-now` (owner-based, admin có thể chỉ định `userId`)
- `/api/wallet/**`
- `/api/payment/**`
- `/api/paypal/**`
- `GET /api/webhook/wallet/deposit-info`

- Admin only (cần token role `ADMIN`):
- `/api/admin/**`
- `POST /api/games`
- `PATCH /api/games/**`
- `DELETE /api/games/**`
- `POST /api/products`
- `PATCH /api/products/**`
- `POST /api/files/upload`
- `POST /api/inventory/**`
- `DELETE /api/inventory/**`
