# 🚀 Game Shop Backend - Quick Start Guide

## ✅ Pre-requisites Check

Trước khi chạy, kiểm tra các yêu cầu:

```bash
# Kiểm tra Java
java -version
# Output: java version "17" or higher

# Kiểm tra Maven
mvn -version
# Output: Apache Maven 3.6.0 or higher

# Kiểm tra MySQL
mysql --version
# Output: Ver 8.0.0 or higher
```

---

## 🗄️ Database Setup

### 1. Tạo Database

```bash
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE gameshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'gameshop'@'localhost' IDENTIFIED BY 'gameshop123';
GRANT ALL PRIVILEGES ON gameshop.* TO 'gameshop'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 2. Cập nhật application.yml

File: `src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gameshop?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: gameshop
    password: gameshop123
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

---

## 🎁 MinIO Setup (Optional nhưng Recommended)

### Option 1: Docker (Recommended)

```bash
# Run MinIO container
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin123 \
  minio/minio server /data --console-address ":9001"

# Access MinIO Console: http://localhost:9001
# Username: minioadmin
# Password: minioadmin123
```

### Option 2: Windows Installer

1. Download từ https://min.io/download
2. Run installer
3. Cấu hình credentials (minioadmin / minioadmin123)

### Option 3: Disable MinIO (For Development)

Nếu không cần lưu trữ hình ảnh, comment out MinIO config trong `application.yml`

---

## 📦 Build & Run

### Method 1: Maven Command

```bash
# Clean build
cd game-shop-backend
mvn clean

# Compile
mvn compile

# Test
mvn test

# Build JAR
mvn package

# Run from JAR
java -jar target/game-shop-backend-0.0.1-SNAPSHOT.jar
```

### Method 2: Spring Boot Maven Plugin

```bash
# Direct run (recommended for development)
cd game-shop-backend
mvn spring-boot:run
```

### Method 3: IDE (IntelliJ/Eclipse)

1. Open project
2. Right-click on `GameShopBackendApplication.java`
3. Click "Run"

---

## ✅ Verification

Sau khi start server, kiểm tra:

```bash
# Health check
curl http://localhost:8080/api/orders/ping

# Response:
# "BE is running OK bây bi nháaa🚀"
```

---

## 🧪 Quick API Tests

### 1. Đăng Ký User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@123"
  }'

# Response:
# {
#   "id": 1,
#   "username": "testuser",
#   "email": "test@example.com",
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "role": "USER"
# }
```

### 2. Lưu Token

```bash
# Windows PowerShell
$TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
$USERID = 1

# Linux/Mac Bash
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
export USERID=1
```

### 3. Tạo Game

```bash
curl -X POST http://localhost:8080/api/games \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F "data={\"name\":\"Test Game\",\"slug\":\"test-game\",\"description\":\"A test game\"}" \
  -F "file=@/path/to/image.jpg"

# Response:
# {
#   "id": 1,
#   "name": "Test Game",
#   "slug": "test-game",
#   "description": "A test game",
#   "status": true,
#   "createdAt": "2026-02-21T..."
# }
```

### 4. Tạo Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F "data={\"gameId\":1,\"type\":\"KEY\",\"platform\":\"ALL\",\"title\":\"Test KEY\",\"description\":\"Test\",\"price\":50000,\"slug\":\"test-key\"}" \
  -F "file=@/path/to/image.jpg"
```

### 5. Lấy Danh Sách Game

```bash
curl -X GET "http://localhost:8080/api/games?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

### 6. Nạp Tiền

```bash
curl -X POST http://localhost:8080/api/wallet/topup \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"userId\":$USERID,\"amount\":100000,\"method\":\"PAYPAL\"}"
```

### 7. Nhập Keys

```bash
curl -X POST http://localhost:8080/api/inventory/keys/import \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "keys": [
      "KEY-12345-ABCDE",
      "KEY-12345-BCDEF",
      "KEY-12345-CDEFG"
    ]
  }'
```

### 8. Mua Hàng

```bash
curl -X POST "http://localhost:8080/api/orders/buy-now?userId=$USERID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 1
      }
    ]
  }'

# Response:
# {
#   "id": 1,
#   "status": "SUCCESS",
#   "items": [
#     {
#       "type": "KEY",
#       "deliveryKey": "KEY-12345-ABCDE",
#       "note": "Key sẽ hết hạn trong 7 ngày"
#     }
#   ]
# }
```

---

## 📊 Admin Commands

### Lấy Danh Sách Users

```bash
curl -X GET "http://localhost:8080/api/admin/users?page=0&size=20" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### Xem Thống Kê Kho

```bash
curl -X GET "http://localhost:8080/api/inventory/stats" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### Khóa User

```bash
curl -X PATCH "http://localhost:8080/api/admin/users/2/status?status=false" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## 🔍 Logging & Debugging

### View Logs

```bash
# Real-time logs
tail -f logs/application.log

# Last 100 lines
tail -n 100 logs/application.log

# Search for errors
grep ERROR logs/application.log
```

### Enable Debug Logging

Thêm vào `application.yml`:

```yaml
logging:
  level:
    root: INFO
    com.example.gameshopbackend: DEBUG
  file:
    name: logs/application.log
```

---

## 🐳 Docker Deployment

### Option 1: Docker Compose

```bash
# Start all services
docker-compose up -d

# Check logs
docker-compose logs -f game-shop-backend

# Stop services
docker-compose down
```

### Option 2: Individual Docker Container

```bash
# Build image
docker build -t game-shop-backend:latest .

# Run container
docker run -d \
  --name game-shop \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/gameshop \
  -e SPRING_DATASOURCE_USERNAME=gameshop \
  -e SPRING_DATASOURCE_PASSWORD=gameshop123 \
  game-shop-backend:latest

# View logs
docker logs -f game-shop

# Stop container
docker stop game-shop
docker rm game-shop
```

---

## 🧹 Cleanup

### Reset Database

```bash
mysql -u gameshop -p gameshop123 gameshop < /dev/null

# Or via MySQL CLI
mysql -u root -p
DROP DATABASE gameshop;
CREATE DATABASE gameshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Clear Build

```bash
# Remove target directory
mvn clean

# Remove generated files
rm -rf target/
rm -rf logs/
```

---

## ⚠️ Common Issues & Solutions

### Issue 1: Port 8080 Already in Use

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### Issue 2: MySQL Connection Failed

```bash
# Check MySQL is running
mysql -u root -p -e "SELECT 1"

# Check credentials in application.yml
# Verify database exists
mysql -u root -p -e "SHOW DATABASES LIKE 'gameshop';"
```

### Issue 3: Compilation Error

```bash
# Clear cache
mvn clean install -U

# Rebuild
mvn clean compile
```

### Issue 4: MinIO Connection Error

```bash
# Test MinIO is running
curl -I http://localhost:9000/minio/health/live

# Check MinIO credentials in application.yml
```

---

## 📱 Frontend Integration

Frontend URL trong CORS config (để modify nếu cần):

```yaml
# application.yml
web:
  cors:
    allowedOrigins: http://localhost:3000,http://localhost:5173
```

---

## 📚 Useful Commands

```bash
# List all endpoints
curl http://localhost:8080/v3/api-docs | jq '.paths | keys'

# Check database
mysql -u gameshop -p gameshop123 -e "USE gameshop; SHOW TABLES;"

# Monitor disk usage
docker ps -a --format 'table {{.Names}}\t{{.Size}}'

# View environment
env | grep SPRING
```

---

## 🎯 Next Steps

1. ✅ Cấu hình Database
2. ✅ Cấu hình MinIO (optional)
3. ✅ Build & Run Backend
4. ✅ Test API Endpoints
5. ⏭️ Setup Frontend
6. ⏭️ Deploy to Production

---

## 📞 Support

- 📖 Xem `API_DOCUMENTATION.md` cho chi tiết endpoints
- 📖 Xem `README.md` cho hướng dẫn chung
- 📖 Xem `IMPLEMENTATION_COMPLETE.md` cho danh sách features

---

**Happy Coding! 🚀**


