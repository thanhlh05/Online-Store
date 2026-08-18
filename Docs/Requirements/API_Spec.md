# API SPEC — SpringBoot-Angular7 Online Shopping Store

**Base URL:** `http://localhost:8080/api`
**Cơ chế xác thực:** JWT Bearer Token, thuật toán HS512, hết hạn sau `jwtExpiration` giây (mặc định `86400` giây = 24 giờ). Gửi kèm header `Authorization: Bearer <token>` cho các API cần đăng nhập. Không dùng session (`SessionCreationPolicy.STATELESS`).

Tài liệu này tổng hợp từ source code thật (5 Controller, SpringSecurityConfig, JWT, DTO/Entity, dữ liệu mẫu `import.sql`) — dùng làm tài liệu tham chiếu chính thức cho cả Dev và QA/Tester.

---

## 0. MA TRẬN PHÂN QUYỀN (Authorization Matrix)

Hệ thống có **2 tầng phân quyền độc lập**:

### Tầng 1 — Security Filter (`SpringSecurityConfig.java`)
Chặn request **trước khi vào Controller**. Không cấu hình `AccessDeniedHandler` riêng nên khi sai role (đã đăng nhập nhưng không đủ quyền) sẽ nhận **403 Forbidden** (mặc định của Spring Security); khi chưa đăng nhập/token không hợp lệ sẽ nhận **401 Unauthorized** (qua `JwtEntryPoint`).

| Path pattern | Yêu cầu | Ghi chú |
|---|---|---|
| `/login`, `/register`, `/product/**`, `/category/**` | Public | Không cần token |
| `/profile/**` | Đã đăng nhập (bất kỳ role) | — |
| `/cart/**` | **CHỈ `ROLE_CUSTOMER`** | Employee/Manager gọi → `403` |
| `/order/finish/**` | `ROLE_EMPLOYEE` hoặc `ROLE_MANAGER` | Customer gọi → `403` (chặn tại đây, không tới được Controller) |
| `/order/**` (còn lại) | Đã đăng nhập (bất kỳ role) | — |
| `/seller/product/new` | **CHỈ `ROLE_MANAGER`** | Employee gọi → `403` |
| `/seller/**/delete` | **CHỈ `ROLE_MANAGER`** | Employee gọi → `403` |
| `/seller/**` (còn lại, vd `/edit`) | `ROLE_EMPLOYEE` hoặc `ROLE_MANAGER` | Cả 2 role được phép |

### Tầng 2 — Logic thủ công trong Controller (kiểm tra quyền sở hữu)
Chỉ chạy khi đã vượt qua Tầng 1.

| Endpoint | Logic | Kết quả khi vi phạm |
|---|---|---|
| `PUT /profile` | `principal.email` phải khớp `body.email` | `400 Bad Request` |
| `GET /profile/{email}` | `principal.email` phải khớp `path.email` | `400 Bad Request` |
| `GET /order/{id}` | Nếu là Customer: `principal.email` phải khớp `order.buyerEmail` | `401 Unauthorized` |
| `PATCH /order/cancel/{id}` | Nếu là Customer: `principal.email` phải khớp `order.buyerEmail`. Employee/Manager: luôn được phép, không phân biệt chủ đơn | `401 Unauthorized` (chỉ Customer) |
| `PATCH /order/finish/{id}` | Có đoạn code kiểm tra `ROLE_CUSTOMER` → 401, nhưng **không bao giờ chạy tới** vì Tầng 1 đã chặn Customer bằng 403 trước đó | — (dead code) |

---

## 1. UserController — không có prefix riêng

| Method | Path | Auth | Input | Output | Ghi chú |
|---|---|---|---|---|---|
| **POST** | `/login` | Public | `LoginForm`: `{ username, password }` | `JwtResponse`: `{ token, type: "Bearer", account, name, role }` | `username` thực chất là email. Sai thông tin đăng nhập → `401`. |
| **POST** | `/register` | Public | `User` object đầy đủ | `User` object đã lưu | Có try-catch trả `400` khi lỗi. |
| **PUT** | `/profile` | Đã đăng nhập | `User` object (email phải khớp token) | `User` object đã cập nhật | Email lệch → `400`. |
| **GET** | `/profile/{email}` | Đã đăng nhập | path: `email` | `User` object | Xem profile người khác → `400` (không phải 401/403). |

---

## 2. ProductController & CategoryController — không có prefix riêng

| Method | Path | Auth | Input | Output | Ghi chú |
|---|---|---|---|---|---|
| **GET** | `/category/{type}` | Public | path: `type` (Integer), query: `page` (mặc định 1), `size` (mặc định 3) | `CategoryPage`: `{ category, page }` | `page` ở đây là object `Page<ProductInfo>` chuẩn Spring Data. |
| **GET** | `/product` | Public | query: `page` (mặc định 1), `size` (mặc định 3) | `Page<ProductInfo>` | — |
| **GET** | `/product/{id}` | Public | path: `id` (String) | `ProductInfo` | — |
| **POST** | `/seller/product/new` | MANAGER | `ProductInfo` object (`@Valid`) | `ProductInfo` đã lưu | Check trùng `productId` thủ công trước khi lưu. Lỗi validate → `400`. |
| **PUT** | `/seller/product/{id}/edit` | EMPLOYEE hoặc MANAGER | `ProductInfo` object (`@Valid`) | `ProductInfo` đã cập nhật | Path `id` phải khớp `body.productId`, nếu không → `400` ("Id Not Matched"). |
| **DELETE** | `/seller/product/{id}/delete` | MANAGER | path: `id` | `200 OK`, body rỗng | — |

---

## 3. CartController — prefix `/cart`, toàn bộ API chỉ dành cho `ROLE_CUSTOMER`

| Method | Path | Input | Output | Ghi chú |
|---|---|---|---|---|
| **GET** | `/cart` | — | `Cart` object (chứa danh sách sản phẩm trong giỏ) | — |
| **POST** | `/cart` | Mảng `ProductInOrder`: `[{ "productId": "B0001", "count": 1 }]` | `Cart` object | Gộp giỏ hàng local (vd từ localStorage khi chưa đăng nhập) vào giỏ hàng server. |
| **POST** | `/cart/add` | `ItemForm`: `{ "productId": "B0001", "quantity": 2 }` | `true` / `false` (boolean thô, không phải JSON object) | Thêm 1 loại sản phẩm vào giỏ. |
| **PUT** | `/cart/{itemId}` | Body raw là **số nguyên** (vd `2`), KHÔNG phải JSON object | `ProductInOrder` đã cập nhật | Gửi JSON object thay vì số nguyên thô sẽ lỗi `400`. |
| **DELETE** | `/cart/{itemId}` | path: `itemId` | `200 OK`, body rỗng | Xoá 1 item khỏi giỏ. |
| **POST** | `/cart/checkout` | — | `200 OK`, body rỗng | Chuyển giỏ hàng hiện tại thành 1 Order mới. |

---

## 4. OrderController — không có prefix riêng

| Method | Path | Auth | Input | Output / Logic phân quyền |
|---|---|---|---|---|
| **GET** | `/order` | Đã đăng nhập | query: `page` (mặc định 1), `size` (mặc định 10) | Customer: chỉ thấy đơn của mình. Employee/Manager: thấy TẤT CẢ đơn. |
| **GET** | `/order/{id}` | Đã đăng nhập | path: `id` (Long) | Customer: chỉ xem được đơn của mình (khác → `401`). Employee/Manager: xem được mọi đơn. |
| **PATCH** | `/order/cancel/{id}` | Đã đăng nhập | path: `id` (Long) | Customer: chỉ huỷ được đơn của mình (khác → `401`). Employee/Manager: huỷ được bất kỳ đơn nào. |
| **PATCH** | `/order/finish/{id}` | CHỈ EMPLOYEE hoặc MANAGER (chặn ở tầng Security) | path: `id` (Long) | Customer gọi → `403` (chặn trước khi vào Controller). |

---

## 5. Cấu trúc DTO / Entity chi tiết

**LoginForm** (request `/login`):
```json
{ "username": "string (bắt buộc)", "password": "string (bắt buộc)" }
```

**ItemForm** (request `/cart/add`):
```json
{ "productId": "string (bắt buộc)", "quantity": "integer (>= 1)" }
```

**JwtResponse** (response `/login`):
```json
{ "token": "string", "type": "Bearer", "account": "string (email)", "name": "string", "role": "string (vd ROLE_CUSTOMER)" }
```

**CategoryPage** (response `/category/{type}`):
```json
{ "category": "string (tên danh mục)", "page": { "content": [], "totalElements": 0, "totalPages": 0 } }
```

**User** (entity — dùng cho `/register`, `PUT /profile`, response `/profile/{email}`):
```json
{
  "id": 0,
  "email": "string",
  "password": "string",
  "name": "string",
  "phone": "string",
  "address": "string",
  "active": true,
  "role": "ROLE_CUSTOMER | ROLE_EMPLOYEE | ROLE_MANAGER",
  "authorities": [{ "authority": "ROLE_CUSTOMER" }]
}
```

**ProductInfo** (entity — dùng cho `/product`, `/seller/product/**`):
```json
{
  "productId": "string (vd B0001)",
  "categoryType": 0,
  "productName": "string",
  "productDescription": "string",
  "productIcon": "string (URL ảnh)",
  "productPrice": 0.0,
  "productStock": 0,
  "productStatus": 0
}
```

**ProductInOrder** (entity — đại diện 1 dòng sản phẩm trong giỏ hàng hoặc trong đơn hàng):
```json
{
  "productId": "string",
  "productName": "string",
  "productPrice": 0.0,
  "productDescription": "string",
  "productIcon": "string",
  "count": 0
}
```

**OrderMain** (entity — response `/order`, `/order/{id}`):
```json
{
  "orderId": 0,
  "products": [],
  "buyerEmail": "string",
  "buyerName": "string",
  "buyerPhone": "string",
  "buyerAddress": "string",
  "orderAmount": 0.0,
  "orderStatus": 0,
  "createTime": "datetime",
  "updateTime": "datetime"
}
```
`orderStatus` mặc định là `0` khi tạo đơn mới ("new order").

---

## 6. Dữ liệu mẫu có sẵn (`import.sql`)

**Tài khoản test** (mật khẩu tất cả đều là `123`, trừ khi báo lỗi thì kiểm tra lại):

| Email | Role |
|---|---|
| `customer1@email.com` | ROLE_CUSTOMER |
| `customer2@email.com` | ROLE_CUSTOMER |
| `employee1@email.com` | ROLE_EMPLOYEE |
| `manager1@email.com` | ROLE_MANAGER |

**Sản phẩm có sẵn** (12 sản phẩm, mã: `B0001`–`B0005`, `C0001`–`C0003`, `D0001`–`D0002`, `F0001`–`F0002`), thuộc 4 danh mục: `Books` (type 0), `Clothes` (type 2), `Food` (type 1), `Drink` (type 3).

**Đơn hàng có sẵn** (`orderId`, `buyerEmail`, `orderStatus`):

| orderId | Chủ đơn | Status |
|---|---|---|
| 2147483642 | customer1@email.com | 2 |
| 2147483640 | customer1@email.com | 2 |
| 2147483648 | customer1@email.com | 1 |
| 2147483643 | customer2@email.com | 0 |
| 2147483645 | customer2@email.com | 0 |
| 2147483649 | customer2@email.com | 0 |
| 2147483641 | customer2@email.com | 2 |
| 2147483647 | customer2@email.com | 2 |

---

## 7. Ghi chú kỹ thuật quan trọng cho QA

- **`POST /cart` (merge cart) có bug xử lý lỗi:** nhánh catch exception chỉ tạo response lỗi nhưng không `return` nó, nên API luôn trả `200 OK` kể cả khi việc gộp giỏ hàng thất bại bên trong.
- **Nhiều endpoint không có try-catch hoặc không check null** trước khi truy cập dữ liệu tìm được (`OrderController`, `CategoryController`, `ProductController.delete`, `CartController.checkout`) — khi gặp ID không tồn tại hoặc dữ liệu ở trạng thái không hợp lệ, các endpoint này có rủi ro trả về `500 Internal Server Error` thay vì mã lỗi nghiệp vụ rõ ràng (400/404). Cần thiết kế Test Case với dữ liệu **luôn hợp lệ, có thật** khi test nhánh thành công, và test riêng các trường hợp dữ liệu không hợp lệ như 1 nhóm case độc lập.
- **`POST /seller/product/new` và `PUT /seller/product/{id}/edit`** trả lỗi validate bằng cách trả thẳng object `BindingResult` làm response body — đây là 1 lỗi phổ biến trong Spring vì `BindingResult` không luôn serialize JSON được sạch sẽ, có rủi ro gây lỗi `500` thay vì `400` dù logic nghiệp vụ đúng.
- **`itemId` trong `PUT/DELETE /cart/{itemId}`** được khai báo kiểu `String` trong code — chưa xác định chắc chắn giá trị thực nhận vào là `productId` (vd `B0001`) hay 1 ID số riêng của giỏ hàng, cần thêm `CartService.java`/`ProductInOrderService.java` để xác nhận.
