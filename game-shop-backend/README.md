# Game Shop Backend - README

## 🎮 Giới Thiệu

**Game Shop Backend** là một hệ thống backend hoàn chỉnh cho ứng dụng bán game online, được xây dựng bằng **Spring Boot 3.2.2** với MySQL database.

Hệ thống hỗ trợ:
- 🛒 Mua bán game keys & accounts
- 💳 Thanh toán đa phương thức (PayPal, Card, ATM, Momo)
- 💰 Quản lý ví điện tử
- 🔐 Xác thực JWT & phân quyền
- 📦 Quản lý kho hàng (tự động giao hàng)
- 👨‍💼 Admin dashboard
- 📁 Lưu trữ hình ảnh MinIO

---

## ⚙️ Yêu Cầu Hệ Thống

- **Java 17+**
- **Maven 3.6+**
- **MySQL 8.0+**
- **MinIO** (cho lưu trữ hình ảnh)
- **Node.js 18+** (nếu chạy frontend)

---

## 🚀 Cài Đặt & Chạy

### 1. Clone Project
```bash
git clone <repo-url>
cd gameshop/game-shop-backend
```

### 2. Cấu Hình Database

Tạo database MySQL:
```sql
CREATE DATABASE gameshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Cập nhật `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gameshop?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: your_password
```

### 3. Cấu Hình MinIO

Chạy MinIO bằng Docker:
```bash
docker run -d \
  -p 9000:9000 \
  -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin123 \
  minio/minio server /data --console-address ":9001"
```

### 4. Cấu Hình PayPal (Optional)

Cập nhật PayPal credentials trong `application.yml`:
```yaml
spring:
  paypal:
    client-id: your_paypal_client_id
    client-secret: your_paypal_client_secret
    base-url: https://api-m.sandbox.paypal.com
```

### 5. Build & Chạy

```bash
# Build project
mvn clean package

# Run application
mvn spring-boot:run

# Hoặc chạy JAR file
java -jar target/game-shop-backend-0.0.1-SNAPSHOT.jar
```

Server sẽ chạy tại: `http://localhost:8080`

---

## 📡 API Documentation

Xem file `API_DOCUMENTATION.md` để biết chi tiết tất cả endpoints.

### Quick Examples:

#### Đăng ký
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user123",
    "email": "user@example.com",
    "password": "password123"
  }'
```

#### Đăng nhập
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user123",
    "password": "password123"
  }'
```

#### Mua Hàng
```bash
curl -X POST http://localhost:8080/api/orders/buy-now?userId=1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 1
      }
    ]
  }'
```

---

## 📁 Cấu Trúc Project

```
game-shop-backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/gameshopbackend/
│   │   │   ├── controller/           # REST Controllers
│   │   │   ├── service/              # Business Logic
│   │   │   ├── repository/           # Data Access
│   │   │   ├── entity/               # JPA Entities
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   ├── mapper/               # MapStruct Mappers
│   │   │   ├── exception/            # Custom Exceptions
│   │   │   ├── config/               # Spring Configurations
│   │   │   ├── jwt/                  # JWT Authentication
│   │   │   ├── security/             # Security Config
│   │   │   ├── util/                 # Enums & Utilities
│   │   │   └── GameShopBackendApplication.java
│   │   └── resources/
│   │       ├── application.yml       # Application Config
│   │       ├── static/
│   │       └── templates/
│   └── test/
├── pom.xml                           # Maven Dependencies
├── docker-compose.yml                # Docker Compose
├── Dockerfile                        # Docker Image
├── API_DOCUMENTATION.md              # API Docs
└── README.md                         # This file
```

---

## 🛒 Order Flow

```
1. User Đăng Ký/Đăng Nhập
   ↓
2. Xem Danh Sách Game & Sản Phẩm
   ↓
3. Chọn Sản Phẩm Mua
   ↓
4. Thanh Toán (Nạp Tiền Ví)
   ↓
5. Mua Hàng (Buy Now)
   ├─→ Kiểm Tra Ví Có Tiền
   ├─→ Kiểm Tra Kho Có Hàng
   ├─→ Trừ Tiền Ví
   ├─→ Tự Động Giao Hàng (KEY/ACCOUNT)
   └─→ Ghi Log Giao Dịch
```

---

## 🔐 Authentication

Tất cả endpoints (trừ register/login) yêu cầu JWT token:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Token được cấp khi đăng nhập và hết hạn sau **24 giờ**.

---

## 👥 Roles & Permissions

| Role | Permissions |
|------|-------------|
| USER | Xem game, mua hàng, quản lý ví, xem profile |
| ADMIN | Quản lý game, sản phẩm, users, orders, inventory |
| RESELLER | USER permissions + bán hàng riêng |

---

## 🗄️ Database Schema

### Main Tables:
- `users` - Tài khoản người dùng
- `products` - Sản phẩm bán
- `games` - Danh sách game
- `orders` - Đơn hàng
- `order_details` - Chi tiết đơn hàng
- `wallets` - Ví người dùng
- `wallet_logs` - Lịch sử ví
- `game_keys` - Kho key
- `game_accounts` - Kho account
- `payment_transactions` - Lịch sử thanh toán

---

## 💾 Deployment

### Production Checklist:

- [ ] Cấu hình SSL/TLS (HTTPS)
- [ ] Cấu hình environment variables an toàn
- [ ] Backup database thường xuyên
- [ ] Setup monitoring & logging
- [ ] Implement rate limiting
- [ ] Enable CORS chỉ cho trusted domains
- [ ] Mã hóa sensitive data
- [ ] Setup CI/CD pipeline

### Docker Deployment:

```bash
# Build image
docker build -t game-shop-backend:latest .

# Run container
docker run -d \
  --name game-shop \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/gameshop \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  game-shop-backend:latest
```

---

## 🧪 Testing

```bash
# Run tests
mvn test

# Test with coverage
mvn test jacoco:report

# Integration tests
mvn verify
```

---

## 📊 Monitoring & Logging

Logging được cấu hình tự động qua Spring Boot.

View logs:
```bash
# Live logs
tail -f logs/application.log

# Docker logs
docker logs -f game-shop
```

---

## 🐛 Troubleshooting

### Port 8080 đang được sử dụng
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### Database connection error
```bash
# Kiểm tra MySQL chạy
mysql -u root -p
```

### MinIO connection error
```bash
# Kiểm tra MinIO chạy
curl http://localhost:9000/minio/health/live
```

---

## 📚 References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT Guide](https://jwt.io/introduction)
- [PayPal API](https://developer.paypal.com)
- [MinIO Documentation](https://docs.min.io)

---

## 📝 License

Dự án này có giấy phép MIT.

---

## 👨‍💻 Support

Nếu gặp vấn đề:
1. Kiểm tra `API_DOCUMENTATION.md`
2. Xem logs trong `logs/` folder
3. Kiểm tra database connection
4. Verify environment variables

---

## 🎉 Thank You!

Cảm ơn bạn đã sử dụng Game Shop Backend!

Nếu bạn thích project này, xin vui lòng ⭐ Star repo!

---

**Last Updated:** 2026-02-21
**Version:** 1.0.0
**Status:** Production Ready ✅

