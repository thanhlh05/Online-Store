# Kiểm Chứng Phần Mềm — Online Shopping Store

---

## 1. Tổng quan dự án & Các chức năng chính

### 🔹 Tổng quan
Kiểm Chứng Phần Mềm — Online Shopping Store là ứng dụng web bán hàng trực tuyến được xây dựng theo mô hình Full-stack (Backend REST API + Frontend Angular UI). Dự án phục vụ công tác kiểm thử phần mềm toàn diện, tích hợp quy trình kiểm thử tự động (API/UI), quản lý tài liệu QA/QC chuyên nghiệp và triển khai CI/CD qua GitHub Actions. Ứng dụng có thể chạy trực tiếp ở môi trường Local hoặc triển khai đóng gói bằng Docker.

### 🔹 Các chức năng chính
* **Đăng nhập & Xác thực người dùng:** Đăng ký, đăng nhập bảo mật qua JWT Authentication.
* **Phân quyền người dùng (RBAC):** Phân quyền truy cập các chức năng tương ứng cho Customer, Employee, Manager.
* **Quản lý danh mục & Sản phẩm:** Xem danh sách, chi tiết, thêm/sửa/xóa sản phẩm và danh mục (hỗ trợ phân trang dữ liệu).
* **Giỏ hàng (Cart):** Thêm, xóa, cập nhật số lượng sản phẩm, tự động gộp giỏ hàng Local khi đăng nhập (Guest Cart).
* **Quản lý đơn hàng & Thanh toán:** Đặt hàng (Checkout), xem danh sách đơn hàng, cập nhật trạng thái và hủy đơn.
* **Hệ thống REST API:** Cung cấp đầy đủ các API endpoint chuẩn hóa phục vụ giao tiếp Frontend - Backend.

---

## 2. Cấu trúc thư mục dự án

```text
KiemChungPhanMem/
│
├── .github/
│   └── workflows/
│       ├── ci.yml                    
│       └── deploy.yml             
│
├── backend/                         
├── frontend/                         
│
├── Docs/                            
│   ├── Requirements/                
│   │   ├── API_Spec.md              
│   │   └── SRS.docx                
│   │
│   └── QA-Testing/                  
│       ├── Test_Plan.docx            
│       ├── RTM.xlsx                  
│       ├── Test_Case_Report_API.xlsx
│       ├── Test_Case_Report_FE.xlsx  
│       ├── Bug_Report.xlsx           
│       └── Test_Summary_Report.docx  
│
├── postman/                         
│   ├── OnlineShoppingStore.postman_collection.json
│   ├── environment.dev.postman_environment.json
│   └── newman-report/                
│
├── .gitattributes
├── .gitignore
├── docker-compose.yml                
└── README.md                        

```

---

## 3. Yêu cầu môi trường & Công nghệ sử dụng

### 🔹 Yêu cầu môi trường (Prerequisites)

Trước khi chạy ứng dụng, cần cài đặt các phần mềm sau:

* **Chạy Local:** Java 11, Maven, Node.js (v12.22.12), npm, Angular CLI, PostgreSQL.
* **Chạy Docker:** Docker Desktop (đã cung cấp sẵn Docker Engine và Docker Compose để build và chạy container).

### 🔹 Công nghệ sử dụng

* **Backend:** Java 11, Spring Boot 2.2, Spring Security, JWT Authentication, Spring Data JPA, Hibernate, PostgreSQL, Maven.
* **Frontend:** Angular 7, Angular CLI, Bootstrap 4.
* **Triển khai & CI/CD:** Docker, Docker Compose, GitHub Actions.
* **Kiểm thử & Automation:** Postman, Newman (API Testing), CodeceptJS (UI Automation), JUnit/Mockito (Unit Test).

### 🔹 Kiến trúc ứng dụng

```text
┌──────────────────────┐        REST API         ┌──────────────────────┐
│   Frontend Angular   │ ──────────────────────> │  Backend Spring Boot │
└──────────────────────┘                         └──────────┬───────────┘
                                                            │ JPA/Hibernate
                                                            ▼
                                                 ┌──────────────────────┐
                                                 │ PostgreSQL Database  │
                                                 └──────────────────────┘

```

---

## 4. Hướng dẫn khởi chạy ứng dụng

### Cách 1: Chạy trực tiếp qua Local Environment

**1. Backend (Spring Boot)**
*Yêu cầu: Java 11, Maven, PostgreSQL đang hoạt động.*

```bash
cd backend
mvn clean install
mvn spring-boot:run

```

* API Server sẽ khởi chạy tại: `http://localhost:8080`

**2. Frontend (Angular)**
*Yêu cầu: Node.js, npm, Angular CLI.*

```bash
cd frontend
npm install
npm start

```

* Web App sẽ chạy tại: `http://localhost:4200`

---

### Cách 2: Triển khai nhanh bằng Docker Desktop

*Yêu cầu: Đã cài đặt và bật Docker Desktop.*

```bash
# Khởi động toàn bộ ứng dụng (Database, Backend, Frontend)
docker compose up --build -d

# Kiểm tra trạng thái các Container
docker ps

# Dừng hệ thống
docker compose down

```

---

## 5. Chạy Automation Test API (Newman)

Trước khi thực hiện, cài đặt Newman và di chuyển vào thư mục chứa bài test:

```bash
# Cài đặt Newman và Reporter HTML (nếu chưa có)
npm install -g newman
npm install -g newman-reporter-htmlextra

# Di chuyển vào thư mục postman
cd postman

```

Lựa chọn 1 trong 3 cách dưới đây để chạy bộ kiểm thử API:

* **Cách 1: Chạy và xuất báo cáo file HTML (Khuyến nghị)**

```bash
newman run OnlineShoppingStore.postman_collection.json \
  -e environment.dev.postman_environment.json \
  -r htmlextra --reporter-htmlextra-export newman-report/report.html

```

* **Cách 2: Chạy trực tiếp trên Terminal (Không xuất file HTML)**

```bash
newman run OnlineShoppingStore.postman_collection.json \
  -e environment.dev.postman_environment.json

```

* **Cách 3: Chạy với định dạng giao diện dòng lệnh CLI chuẩn**

```bash
newman run OnlineShoppingStore.postman_collection.json \
  -e environment.dev.postman_environment.json \
  -r cli

```

---

## 6. Quy trình CI/CD Pipeline

Dự án được cấu hình GitHub Actions tự động (`.github/workflows/`):

**1. ci.yml**: Tự động kích hoạt khi có Push hoặc Pull Request vào nhánh **main**:
* Build Backend & Frontend.
* Chạy Unit Test.
* Khởi chạy hệ thống qua Docker và chạy Newman API Automation Test.

**2. deploy.yml**: Tự động kích hoạt khi các thay đổi được Merge chính thức vào nhánh **main**.

---

## 7. Nhật ký công việc đã hoàn thành (Sprints)

Dự án được quản lý tiến độ và theo dõi lỗi trên Jira. Dưới đây là tóm tắt các hạng mục công việc đã được hoàn thành qua từng giai đoạn:

### 🔹 Sprint 0 (29 Jul – 5 Aug): Thiết lập hạ tầng & Kiểm thử API cơ bản

* Khởi tạo cấu trúc Git, kết nối Jira và thiết lập luồng CI/CD qua GitHub Actions.
* Phân tích các API Controller (Auth, Product, Cart, Order) và rà soát cơ chế phân quyền (Role/User).
* Xây dựng bộ Postman Collection cho các kịch bản chức năng, dữ liệu và bảo mật.
* Tích hợp tự động hóa kiểm thử API bằng Newman và cấu hình xuất HTML Report.

### 🔹 Sprint 1 (8 Aug – 12 Aug): Tài liệu QA & Thiết kế Test Case

* Hoàn thiện tài liệu Đặc tả yêu cầu (SRS v1.0), Use Case Diagram và bộ khung RTM.
* Soạn thảo Kế hoạch kiểm thử (Test Plan v1.0).
* Thiết kế chi tiết Test Case cho UI/UX (Customer, Employee, Manager) và API (Authorization).
* Khởi tạo mã nguồn Unit Test cho tầng Service layer.

### 🔹 Sprint 2 (12 Aug – 19 Aug): Thực thi Kiểm thử, Automation & Báo cáo Bug

* Bổ sung các kịch bản kiểm thử giá trị biên (BVA) vào hệ thống Postman.
* Thiết lập CodeceptJS, tiến hành tự động hóa các luồng thao tác Frontend quan trọng nhất và cập nhật lại Test Plan.
* Thực thi kiểm thử thủ công trên Frontend (các Role), đánh giá kết quả, log Bug lên Jira.
* Chạy bộ API Test qua Newman (bao gồm các case BVA mới cập nhật).
* Cập nhật và hoàn thiện RTM (ánh xạ đầy đủ Requirement ↔ Test Case ↔ Bug).
* Tiếp tục hoàn thiện độ phủ (coverage) cho Unit Test Service layer.
