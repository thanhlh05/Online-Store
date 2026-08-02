\# Hướng dẫn Kiểm thử API (Postman Collection) - OnlineShoppingStore



Tài liệu này mô tả kịch bản kiểm thử End-to-End (E2E) chuẩn xác nhất cho hệ thống OnlineShoppingStore. Cần chạy theo đúng thứ tự để đảm bảo tính toàn vẹn dữ liệu (ID) và quyền truy cập (Token).



\---



\## 🛠 LƯU Ý TRƯỚC KHI TEST

1\. \*\*Môi trường (Environment):\*\* Đảm bảo Postman Environment đã có sẵn biến `token`. Script tại các API Login sẽ tự động ghi đè giá trị mới vào biến này.

2\. \*\*Kế thừa quyền (Authorization):\*\* Collection gốc phải được thiết lập Type là `Bearer Token` với giá trị `{{token}}`. Các thư mục và request con phải đặt là `Inherit auth from parent`.

3\. \*\*Mã lỗi phổ biến:\*\*

&#x20;  - `401 Unauthorized`: Lỗi chưa có/sai Token (Chạy lại request Login).

&#x20;  - `403 Forbidden`: Lỗi sai quyền Role (Ví dụ: Dùng Customer để đi thêm sản phẩm).

&#x20;  - `500 Internal Server Error`: Thường do truyền trùng lặp khóa chính (Duplicate Key) hoặc ID không tồn tại.



\---



\## 🚀 KỊCH BẢN TEST END-TO-END



\### 🟢 GIAI ĐOẠN 1: Quản lý Sản phẩm (Quyền Manager)

\*Mục đích: Khởi tạo dữ liệu hàng hóa cho hệ thống.\*



1\. \*\*`Manager Login`\*\* 

&#x20;  - \*\*Action:\*\* `POST /login` (với tài khoản manager).

&#x20;  - \*\*Kỳ vọng:\*\* `200 OK`. (Postman tự động lưu Token quyền Manager).

2\. \*\*`Add New Product`\*\*

&#x20;  - \*\*Action:\*\* `POST /product/new`.

&#x20;  - \*\*Lưu ý:\*\* Trong Body, đổi `productId` thành một mã hoàn toàn mới (VD: `NEW\_001`) để tránh lỗi 500.

&#x20;  - \*\*Kỳ vọng:\*\* `200 OK`.

3\. \*\*`Edit Product` / `Delete Product`\*\* 

&#x20;  - \*\*Action:\*\* Cập nhật thông tin hoặc xóa một sản phẩm nháp (Tùy chọn).



\---



\### 🔵 GIAI ĐOẠN 2: Thao tác Giỏ hàng (Quyền Customer)

\*Mục đích: Khách hàng thêm/sửa/xóa sản phẩm trong giỏ.\*



1\. \*\*`Customer Login`\*\*

&#x20;  - \*\*Action:\*\* `POST /login` (với tài khoản customer).

&#x20;  - \*\*Kỳ vọng:\*\* `200 OK`. (Postman đổi Token sang quyền Khách hàng).

2\. \*\*`View current cart` \& `Merge cart from Local/Cookie`\*\*

&#x20;  - \*\*Action:\*\* Gửi request để xem trạng thái giỏ hàng hiện tại.

3\. \*\*`Add a product to the cart`\*\*

&#x20;  - \*\*Action:\*\* Thêm mã `NEW\_001` (hoặc `B0001`) vào giỏ.

&#x20;  - \*\*Kỳ vọng:\*\* `200 OK`.

4\. \*\*`Update quantity`\*\*

&#x20;  - \*\*Action:\*\* `PUT /cart/{productId}`.

&#x20;  - \*\*Lưu ý:\*\* Thẻ Body chọn kiểu `raw` -> `JSON`, và CHỈ TRUYỀN 1 SỐ NGUYÊN (VD: `3`).

&#x20;  - \*\*Kỳ vọng:\*\* `200 OK`.

5\. \*\*`Add a product to the cart` (Lần 2)\*\*

&#x20;  - \*\*Action:\*\* Thêm tiếp mã `F0001` vào giỏ.

6\. \*\*`Remove product from the cart`\*\*

&#x20;  - \*\*Action:\*\* Bỏ mã `F0001` ra khỏi giỏ, giữ lại `NEW\_001` để thanh toán.



\---



\### 🟡 GIAI ĐOẠN 3: Đặt Hàng \& Hủy Đơn (Quyền Customer)

\*Mục đích: Khách hàng tiến hành checkout và test hủy đơn.\*



1\. \*\*`Place order` (Tạo Đơn 1)\*\*

&#x20;  - \*\*Action:\*\* Tiến hành thanh toán giỏ hàng.

&#x20;  - \*\*Kỳ vọng:\*\* `200 OK`.

&#x20;  - 📌 \*\*QUAN TRỌNG:\*\* Nhìn vào JSON trả về, ghi lại mã `orderId` (VD: `orderId: 20`).

2\. \*\*`Add a product...` -> `Place order` (Tạo Đơn 2)\*\*

&#x20;  - \*\*Action:\*\* Mua thêm 1 đơn hàng mới. 

&#x20;  - 📌 \*\*QUAN TRỌNG:\*\* Ghi lại mã `orderId` thứ hai (VD: `orderId: 21`).

3\. \*\*`List of orders`\*\*

&#x20;  - \*\*Action:\*\* `GET /order`.

&#x20;  - \*\*Lưu ý:\*\* Truyền params `page = 1` và `size = 10`.

&#x20;  - \*\*Kỳ vọng:\*\* Thấy danh sách chứa 2 đơn hàng vừa tạo.

4\. \*\*`View order details`\*\*

&#x20;  - \*\*Action:\*\* Sửa URL thành `/order/{id\_đơn\_1}` (VD: `/order/20`).

&#x20;  - \*\*Kỳ vọng:\*\* Hiển thị chi tiết đơn.

5\. \*\*`Cancel order`\*\*

&#x20;  - \*\*Action:\*\* Sửa URL thành `/order/cancel/{id\_đơn\_1}` (VD: `/order/cancel/20`).

&#x20;  - \*\*Kỳ vọng:\*\* `200 OK`. (Trạng thái đơn 20 chuyển sang Canceled).



\---



\### 🔴 GIAI ĐOẠN 4: Duyệt Đơn \& Hoàn Thành (Quyền Employee / Manager)

\*Mục đích: Nhân viên xử lý những đơn hàng chưa bị hủy.\*



1\. \*\*`Employee Login`\*\* (hoặc `Manager Login`)

&#x20;  - \*\*Action:\*\* `POST /login` (Lấy Token quyền Nhân viên/Quản lý).

2\. \*\*`Complete order`\*\*

&#x20;  - \*\*Action:\*\* Sửa URL thành `/order/finish/{id\_đơn\_2}` (VD: `/order/finish/21`). 

&#x20;  - \*\*Lưu ý:\*\* KHÔNG dùng ID của đơn đã hủy (đơn 20) để tránh lỗi 500 xung đột trạng thái.

&#x20;  - \*\*Kỳ vọng:\*\* `200 OK`. (Trạng thái đơn 21 chuyển sang Finished).



\---

\*Xác nhận toàn bộ quy trình trả về 200 OK! Kịch bản hoàn tất.\*

