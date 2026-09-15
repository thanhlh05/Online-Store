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
├── backend/                    # Spring Boot
│   ├── src/main/java/...
│   ├── src/test/java/...       # Unit test (181 tests)
│   └── target/site/jacoco/     # JaCoCo Unit (sau mvn test)
├── frontend/                   # Angular 7
├── frontend-e2e/               # CodeceptJS + Playwright
├── postman/
│   ├── OnlineShoppingStore.postman_collection.json
│   ├── environment.dev.postman_environment.json
│   └── newman-report/          # HTML Newman
├── jacoco/                     # agent.jar, cli.jar, jacoco.exec (BB dump)
├── jacoco-bb-report/           # JaCoCo HTML khi chạy Newman (BB)
├── Docs/
│   ├── Requirements/
│   │   ├── SRS.docx
│   │   └── API_Spec.md
│   └── QA-Testing/
│       ├── TEST PLAN.docx
│       ├── Test_Summary_Report.docx
│       ├── Coverage_Report.md
│       ├── RTM.xlsx
│       ├── Bug_Report.xlsx
│       ├── Test_Case_Report_API.xlsx
│       ├── Test_Case_Report_FE.xlsx
│       ├── Test_Case_BVA.xlsx
│       ├── Test_Case_DecisionTable.xlsx
│       ├── Test_Case_StateTransition.xlsx
│       ├── Test_Case_WhiteBox.xlsx
│       └── images/             # Ảnh JaCoCo, CFG…          
│
├── .dockerignore
├── .gitattributes
├── .gitignore
├── docker-compose.yml                
└── README.md                        

```

---

## 3. Yêu cầu môi trường

- **Docker Desktop** (khuyến nghị chạy full stack)
- Hoặc local: JDK 11, Maven, Node.js (FE/E2E), PostgreSQL
- Newman + reporter: `npm i -g newman newman-reporter-htmlextra`
- Codecept: trong `frontend-e2e` → `npm install`

---

## 4. Hướng dẫn khởi chạy ứng dụng

### Docker (khuyến nghị)

```bash
docker compose down -v
docker compose up -d --build
docker ps
```

| Service | URL |
|----------|-----|
| Frontend | http://localhost:3000 |
| Backend | http://localhost:8080/api |
| DB | localhost:5432 |

Backend có thể gắn **JaCoCo agent** (port **6300**, volume `./jacoco`) — xem `docker-compose.yml`.

### Local (không Docker)

```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend
cd frontend && npm install && npm start
# thường http://localhost:4200
```

**Tài khoản mẫu** (password: `123`):

| Email | Role |
|-------|------|
| customer1@email.com | CUSTOMER |
| customer2@email.com | CUSTOMER |
| employee1@email.com | EMPLOYEE |
| manager1@email.com | MANAGER |

---

## 5. Chạy kiểm thử

### 5.1 Unit test + JaCoCo Unit (White-box)

```bash
cd backend
mvn test
mvn jacoco:report
```

- Kỳ vọng: **Tests run: 181, Failures: 0**
- HTML: `backend/target/site/jacoco/index.html`
- Service layer: **100% Statement / 100% Branch** (chi tiết `Docs/QA-Testing/Coverage_Report.md`)

Một class:

```bash
mvn test -Dtest=BvaBoundaryTest
mvn test -Dtest=CartControllerTest
```

### 5.2 API — Newman (Black-box)

```bash
cd postman
newman run OnlineShoppingStore.postman_collection.json ^
  -e environment.dev.postman_environment.json ^
  -r htmlextra --reporter-htmlextra-export newman-report/API_Test_Report.html
```

*(Linux/macOS: dùng `\` thay `^`)*

### 5.3 JaCoCo khi chạy Newman (BB coverage)

Thứ tự:

1. Backend Docker **Up** (agent port 6300)  
2. Chạy Newman  
3. Dump (backend vẫn chạy):

```bash
java -jar jacoco/jacococli.jar dump --address localhost --port 6300 --destfile jacoco/jacoco.exec
```

4. Report:

```bash
java -jar jacoco/jacococli.jar report jacoco/jacoco.exec ^
  --classfiles backend/target/classes ^
  --sourcefiles backend/src/main/java ^
  --html jacoco-bb-report --xml jacoco-bb-report/jacoco.xml
```

- HTML BB: `jacoco-bb-report/index.html` → khoảng **39% Instruction / 10% Branch** (toàn project)  
- **Không trộn** với Unit: Unit = `backend/target/site/jacoco`; BB = `jacoco-bb-report`

### 5.4 E2E CodeceptJS

```bash
# FE Docker :3000 đang chạy
cd frontend-e2e
npm test
# hoặc
npx codeceptjs run --steps
npx codeceptjs run "tests/TC_FE_{CUS_LOG_03,EMP_03,MGR_03}.test.js" --steps
```

Kỳ vọng gần đây: **44 passed** (toàn suite npm test).

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
**Tóm tắt**

| Sprint | Nội dung chính |
|--------|----------------|
| 0 | Repo, Jira, Postman, Newman, CI |
| 1 | SRS, Test Plan, TC FE/API, Unit khởi đầu |
| 2 | Execute, BVA Postman, Codecept, log bug |
| 3–4 | ST, DT 6 bước, BVA 4n+1, White-box CFG |
| 5 | Fix validation, privilege escalation, 500→4xx, FE message |
| 6 | Unit (181, service 100%), JaCoCo BB, đồng bộ Docs, chốt nộp |

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

### 🔹 Sprint 3 (19 Aug – 25 Aug): Áp dụng kỹ thuật kiểm thử mới & Hoàn thiện tài liệu
* Áp dụng White-box Testing: Thiết lập JaCoCo, chuyển BVA thành Unit Test và đo độ phủ (coverage).
* Thực thi State Transition Testing cho các luồng nghiệp vụ Order và Cart.
* Thiết kế Decision Table Testing cho ma trận phân quyền hệ thống (Authorization).

### 🔹 Sprint 4 (26 Aug – 01 Sep): BVA đúng công thức, Decision Table đủ 6 bước,  bổ sung White-box
* Sửa lỗi trùng boundary phone + hoàn thiện Register (Standard BVA 21 TC + Robustness chọn lọc).
* Làm lại BVA cho Product (n=5 → 21 TC) và Cart (n=2 → 9 TC) + Robustness.
* State Transition Testing – chỉnh sửa nhỏ và xác nhận khớp với code thực tế.
* Làm lại Decision Table đúng đủ 6 bước (có rút gọn bảng).
* White-box – bổ sung phần còn thiếu (CFG, Cyclomatic Complexity, Condition / Branch-Condition), giữ nguyên unit test đã có.

### 🔹 Sprint 5 (4 Sep – 8 Sep): Fix lỗi nghiêm trọng & Hoàn thiện kiểm thử
* Fix Privilege Escalation (SCRUM-58): Ép cứng ROLE_CUSTOMER khi đăng ký, ngăn inject role.
* Fix Validation User (Password + Email – SCRUM-47, 66, 67, 68): Thêm @Valid + @Size, trả 400 khi vi phạm.
* Fix Validation Product Price + Stock (SCRUM-43, 44, 50, 51): Giá âm và stock âm bị từ chối (400).
* Fix Cart Quantity = 0 + Exception 500 (SCRUM-48, 46, 59): quantity = 0 → 400, trùng ID → 400/409, invalid transition → 400.
* Hoàn tất White-box Testing (OrderServiceImpl.cancel) và tích hợp vào SRS.
* Xác minh lại State Transition + Decision Table sau khi fix.
* Log thêm các bug còn lại (SCRUM-78 → 81) và cập nhật Postman, Bug_Report, Jira.

### 🔹 Sprint 6 (9 Sep – 15 Sep): Đồng bộ tài liệu
* Unit Test: 181 tests, 0 fail; Service Layer JaCoCo 100% Statement/Branch.
* Bổ sung path white-box (cancel productInfo null, mergeLocalCart empty, findUpAll/findAllInCategory).
* Đo BB coverage khi chạy Newman (JaCoCo agent) → jacoco-bb-report/ (39% Instruction / 10% Branch).
* Đồng bộ SRS, Test Plan, Test Summary, RTM, Bug_Report, Excel TC (Retest PASS các bug đã fix).
* Commit báo cáo độc lập: Coverage_Report.md, jacoco-bb-report, Docs/QA-Testing/*.
