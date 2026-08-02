# API SPEC — SpringBoot-Angular7 Online Shopping Store
**Base URL:** `http://localhost:8080/api`

Tài liệu này được tổng hợp để phục vụ cả quá trình Tích hợp (Dev) và Kiểm chứng phần mềm (QA/Tester). Bao gồm cấu trúc dữ liệu chuẩn và ma trận phân quyền thực tế từ hệ thống.

---

## 0. MA TRẬN PHÂN QUYỀN (Authorization Matrix)
> **Lưu ý cho Tester:** Hệ thống có 2 tầng phân quyền độc lập. Tầng 1 chặn trước Controller (trả 403 hoặc 401). Tầng 2 kiểm tra logic bên trong Controller (chủ yếu check quyền sở hữu, có thể trả 400 hoặc 401 tùy code).

**Tầng 1 — Security Filter (SpringSecurityConfig.java):**
* `/login`, `/register`, `/product/**`, `/category/**`: **Public** (Không cần token).
* `/profile/**`: **Authenticated** (Bất kỳ user nào đăng nhập).
* `/cart/**`: **CHỈ CUSTOMER** (Employee/Manager gọi sẽ bị 403).
* `/order/finish/**`: **EMPLOYEE hoặc MANAGER** (Customer gọi bị 403).
* `/order/**`: **Authenticated** (Bất kỳ user nào đăng nhập).
* `/seller/product/new`, `/seller/**/delete`: **CHỈ MANAGER** (Employee/Customer gọi bị 403).
* `/seller/**` (còn lại): **EMPLOYEE hoặc MANAGER**.

**Tầng 2 — Logic Controller (Kiểm tra quyền sở hữu):**
* `GET/PUT /profile/{email}`: Báo lỗi `400 Bad Request` nếu xem/sửa profile người khác.
* `GET/PATCH /order/{id}`: Báo lỗi `401 Unauthorized` nếu Customer xem/hủy đơn người khác.

---

## 1. UserController (Auth + Profile)
| Method | Path | Auth | Input | Output | Ghi chú QA |
|---|---|---|---|---|---|
| **POST** | `/login` | Public | `{ "username": "...", "password": "..." }` | `JwtResponse` (jwt, email, role) | Username thực chất là email. Sai trả `401`. |
| **POST** | `/register` | Public | `User` object | `User` object | Lỗi trả `400`. |
| **PUT** | `/profile` | Có | `User` object | `User` object | ⚠️ Trả `400` (không phải 401/403) nếu email body khác email token. |
| **GET** | `/profile/{email}`| Có | path: `email` | `User` object | ⚠️ Trả `400` nếu xem profile người khác. |

---

## 2. ProductController & CategoryController
| Method | Path | Auth | Input | Output | Ghi chú QA |
|---|---|---|---|---|---|
| **GET** | `/category/{type}` | Public | path: `type`, query: `page`, `size` | `CategoryPage` | — |
| **GET** | `/product` | Public | query: `page`, `size` | `Page<ProductInfo>` | — |
| **GET** | `/product/{id}` | Public | path: `id` | `ProductInfo` | — |
| **POST** | `/seller/product/new`| MANAGER | `ProductInfo` object | `ProductInfo` | Trả `400` nếu trùng ID. `403` nếu sai Role. |
| **PUT** | `/seller/product/{id}/edit`| EMP/MGR | `ProductInfo` object | `ProductInfo` | Trả `400` nếu sai ID. |
| **DELETE**| `/seller/product/{id}/delete`| MANAGER | path: `id` | `200 OK` | Employee gọi sẽ bị `403`. |

---

## 3. CartController (⚠️ CHỈ CUSTOMER)
> Mọi role khác (Employee/Manager) gọi vào các API này đều nhận `403 Forbidden`.

| Method | Path | Input (Ví dụ) | Output | Ghi chú QA (Bug tiềm ẩn) |
|---|---|---|---|---|
| **GET** | `/cart` | — | `Cart` (gồm mảng `products`) | Xem giỏ hàng. |
| **POST** | `/cart` | `[{"productId": "B0001", "count": 1}]` | `Cart` object | ⚠️ Merge cart. Lỗi catch trả `200 OK` (Cần test kỹ). |
| **POST** | `/cart/add` | `{"productId": "B0001", "quantity": 2}`| `true` / `false` | Thêm 1 loại item. |
| **PUT** | `/cart/{itemId}`| **Số nguyên (vd: `2`)** (Không phải JSON) | `ProductInOrder` | ⚠️ Body raw là số, nếu gửi JSON sẽ lỗi `400`. |
| **DELETE**| `/cart/{itemId}`| path: `itemId` (vd: `B0001`) | `200 OK` | Xóa item. |
| **POST** | `/cart/checkout`| — | `200 OK` | Đổi Cart thành Order. Test kỹ case hết hàng. |

---

## 4. OrderController
> Phân quyền cứng trong code, sai chủ đơn sẽ trả `401 Unauthorized` (Thay vì 403 chuẩn REST).

| Method | Path | Input | Output / Logic Phân Quyền |
|---|---|---|---|
| **GET** | `/order` | query: `page`, `size` | **Customer:** Thấy đơn của mình. <br>**Emp/Mgr:** Thấy TẤT CẢ đơn. |
| **GET** | `/order/{id}` | path: `id` | **Customer:** Chỉ xem đơn mình (sai -> `401`). <br>**Emp/Mgr:** Xem mọi đơn. |
| **PATCH** | `/order/cancel/{id}` | path: `id` | Hủy đơn. Customer chỉ hủy đơn mình (sai -> `401`). Trả lại stock. |
| **PATCH** | `/order/finish/{id}` | path: `id` | **CHỈ EMP/MGR.** Customer gọi bị chặn từ Tầng 1 (`403`). |