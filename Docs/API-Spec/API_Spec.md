\# danh sách endpoint thật lấy từ backend

## SCRUM 14

**Không có Controller riêng cho User/Role.** `UserController` chỉ xử lý login/register/profile.

**Không dùng `@PreAuthorize`/`@Secured` trên controller.** Toàn bộ rule nằm tập trung tại
`backend/src/main/java/me/zhulin/shopapi/security/SpringSecurityConfig.java` (theo path pattern,
đánh giá theo thứ tự khai báo - pattern nào khớp trước thì áp dụng).

3 role, mỗi user chỉ 1 role: : `CUSTOMER`, `EMPLOYEE`, `MANAGER`

Có 1 rule chết trong SecurityConfig: `antMatchers("/profiles/**")`, Không API nào dùng path này.

### Bảng API — Role yêu cầu

| Controller | Method | Path | Input | Output | Role yêu cầu (Security Config) | Ghi chú phân quyền bổ sung trong code |
|---|---|---|---|---|---|---|
| UserController | POST | `/login` | LoginForm (username, password) | JwtResponse (jwt, email, name, role) | Public | — |
| UserController | POST | `/register` | User | User | Public | — |
| UserController | PUT | `/profile` | User | User | Authenticated (mọi role) | Chỉ sửa hồ sơ của chính mình (so email principal với email trong body) |
| UserController | GET | `/profile/{email}` | path: email | User | Authenticated (mọi role) | Chỉ xem hồ sơ của chính mình |
| ProductController | GET | `/product` | query: page, size | Page<ProductInfo> | Public | — |
| ProductController | GET | `/product/{productId}` | path: productId | ProductInfo | Public | — |
| ProductController | POST | `/seller/product/new` | ProductInfo | ProductInfo / 400 | MANAGER | — |
| ProductController | PUT | `/seller/product/{id}/edit` | path: id, body: ProductInfo | ProductInfo / 400 | EMPLOYEE, MANAGER | — |
| ProductController | DELETE | `/seller/product/{id}/delete` | path: id | 200 | MANAGER | — |
| CartController | GET | `/cart` | — | Cart | CUSTOMER | — |
| CartController | POST | `/cart` | Collection<ProductInOrder> | Cart | CUSTOMER | — |
| CartController | POST | `/cart/add` | ItemForm (productId, quantity) | boolean | CUSTOMER | — |
| CartController | PUT | `/cart/{itemId}` | path: itemId, body: quantity | ProductInOrder | CUSTOMER | — |
| CartController | DELETE | `/cart/{itemId}` | path: itemId | — | CUSTOMER | — |
| CartController | POST | `/cart/checkout` | — | 200 | CUSTOMER | — |
| OrderController | GET | `/order` | query: page, size | Page<OrderMain> | Authenticated (mọi role) | CUSTOMER: chỉ thấy đơn của mình. EMPLOYEE/MANAGER: thấy tất cả |
| OrderController | PATCH | `/order/cancel/{id}` | path: id | OrderMain / 401 | Authenticated (mọi role) | CUSTOMER: chỉ hủy đơn của mình, sai chủ đơn → 401. EMPLOYEE/MANAGER: hủy bất kỳ đơn |
| OrderController | PATCH | `/order/finish/{id}` | path: id | OrderMain / 401 | EMPLOYEE, MANAGER | Chặn CUSTOMER 2 lớp: Security Config chặn trước, code check lại lần 2 |
| OrderController | GET | `/order/{id}` | path: id | OrderMain / 401 | Authenticated (mọi role) | CUSTOMER: chỉ xem đơn của mình, sai chủ đơn → 401. EMPLOYEE/MANAGER: xem bất kỳ đơn |
| CategoryController | GET | `/category/{type}` | path: type, query: page, size | CategoryPage | Public | — |