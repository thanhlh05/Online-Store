# Kiểm Chứng Phần Mềm - Online Shopping Store

## 1. Tổng quan dự án

Kiểm Chứng Phần Mềm - Online Shopping Store là một ứng dụng web bán hàng trực tuyến được xây dựng theo mô hình Full-stack, bao gồm phần Backend cung cấp REST API và phần Frontend cung cấp giao diện web cho người dùng.

Dự án được tổ chức thành hai thành phần chính là Backend và Frontend. Ứng dụng có thể được chạy trực tiếp trên môi trường local hoặc triển khai bằng Docker.

## 2. Các chức năng chính

Ứng dụng cung cấp các chức năng cơ bản của một hệ thống bán hàng trực tuyến, bao gồm

 Đăng nhập và xác thực người dùng
 Phân quyền người dùng
 Quản lý và hiển thị danh mục sản phẩm
 Xem sản phẩm
 Giỏ hàng
 Quản lý đơn hàng
 Thanh toán
 Phân trang dữ liệu
 REST API

## 3. Công nghệ sử dụng

### Backend

 Java 11
 Spring Boot 2.2
 Spring Security
 JWT Authentication
 Spring Data JPA
 Hibernate
 PostgreSQL
 Maven

### Frontend

 Angular 7
 Angular CLI
 Bootstrap

### Triển khai

 Docker
 Docker Compose

## 4. Kiến trúc hệ thống

Ứng dụng được xây dựng theo mô hình kiến trúc Frontend - Backend

```text
┌──────────────────────┐
│       Frontend       │
│      Angular 7       │
└──────────┬───────────┘
           │
           │ REST API
           ▼
┌──────────────────────┐
│       Backend        │
│ Spring Boot 2.2 API │
└──────────┬───────────┘
           │
           │ JPA  Hibernate
           ▼
┌──────────────────────┐
│      PostgreSQL      │
│       Database       │
└──────────────────────┘
```

Frontend Angular giao tiếp với Backend Spring Boot thông qua REST API.

Backend sử dụng Spring Data JPA và Hibernate để tương tác với cơ sở dữ liệu PostgreSQL.

## 5. Cấu trúc dự án

```text
KiemChungPhanMem
│
├── backend
│   └── Mã nguồn Backend Spring Boot
│
├── frontend
│   └── Mã nguồn Frontend Angular
│
├── Docs
│   ├── SRS
│   ├── Test-Plan
│   ├── Test-Case
│   ├── Test-Report
│   └── API-Spec
│
├── postman
│   ├── OnlineShoppingStore.postman_collection.json
│   ├── environment.dev.postman_environment.json
│   └── newman-report
│
├── .gitignore
├── docker-compose.yml
└── README.md
```

Các thư mục `Docs` và `postman` được chuẩn bị để phục vụ việc lưu trữ tài liệu của dự án và sẽ được cập nhật trong quá trình thực hiện.

## 6. Yêu cầu môi trường

Trước khi chạy ứng dụng, cần cài đặt các phần mềm sau

 Java 11
 Maven
 Node.js
 npm
 Angular CLI
 PostgreSQL

Nếu sử dụng Docker

 Docker Desktop

Docker Desktop cung cấp sẵn Docker Engine và Docker Compose để build và chạy các container của dự án.

## 7. Hướng dẫn chạy ứng dụng

Cần khởi động Backend trước Frontend.

### Backend

1. Đảm bảo PostgreSQL đã được cài đặt và đang hoạt động.
2. Cấu hình thông tin kết nối cơ sở dữ liệu trong file cấu hình của Backend.
3. Mở Terminal và di chuyển vào thư mục Backend

```bash
cd backend
```

4. Cài đặt và build project

```bash
mvn install
```

5. Khởi động ứng dụng Spring Boot

```bash
mvn spring-bootrun
```

Backend sẽ chạy tại

```text
httplocalhost8080
```

### Frontend

1. Mở một Terminal khác.
2. Di chuyển vào thư mục Frontend

```bash
cd frontend
```

3. Cài đặt các thư viện cần thiết

```bash
npm install
```

4. Khởi động Angular

```bash
ng serve
```

Frontend sẽ chạy tại

```text
httplocalhost4200
```

## 8. Triển khai bằng Docker Desktop

Ứng dụng có thể được build và chạy bằng Docker Desktop thông qua file `docker-compose.yml`.

### Bước 1 Khởi động Docker Desktop

Mở Docker Desktop và đảm bảo Docker Engine đang hoạt động.

### Bước 2 Mở Terminal tại thư mục dự án

Di chuyển đến thư mục gốc của dự án

```
cd Thu_muc_goc_cua_ban
```

### Bước 3 Build và khởi động ứng dụng

Chạy lệnh

```
docker compose up --build
```

Lệnh này sẽ build các image cần thiết và khởi động các container được định nghĩa trong file `docker-compose.yml`.

### Bước 4 Kiểm tra container

Có thể kiểm tra các container đang chạy trực tiếp trong giao diện Docker Desktop hoặc sử dụng lệnh

```
docker ps
```

### Bước 5 Dừng ứng dụng

Để dừng các container

```
docker compose down
```

 Lưu ý Docker Desktop cần được mở và Docker Engine phải đang hoạt động trước khi thực hiện các lệnh Docker.

## 9. Giấy phép

Dự án được phân phối theo giấy phép MIT License.
