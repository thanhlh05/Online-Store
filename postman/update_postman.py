import json
import copy

INPUT_FILE = "OnlineShoppingStore.postman_collection.json"
OUTPUT_FILE = "OnlineShoppingStore.postman_collection.json"

def make_request(name, method, path_parts, token_var=None, body=None, expected_status=200, extra_test=""):
    """Tạo 1 request Postman chuẩn."""
    headers = []
    auth = {"type": "noauth"}

    if token_var:
        auth = {
            "type": "bearer",
            "bearer": [{"key": "token", "value": f"{{{{{token_var}}}}}", "type": "string"}]
        }

    if body is not None:
        headers.append({"key": "Content-Type", "value": "application/json"})

    test_script = [
        f'pm.test("Status {expected_status}", function () {{',
        f'    pm.response.to.have.status({expected_status});',
        "});"
    ]
    if extra_test:
        test_script.append(extra_test)

    req = {
        "name": name,
        "event": [{
            "listen": "test",
            "script": {
                "exec": test_script,
                "type": "text/javascript"
            }
        }],
        "request": {
            "auth": auth,
            "method": method,
            "header": headers,
            "url": {
                "raw": "{{base_url}}/" + "/".join(path_parts),
                "host": ["{{base_url}}"],
                "path": path_parts
            }
        },
        "response": []
    }

    if body is not None:
        req["request"]["body"] = {
            "mode": "raw",
            "raw": json.dumps(body, indent=2),
            "options": {"raw": {"language": "json"}}
        }

    return req


def build_state_transition_folder():
    """
    Seed order_main (từ import.sql):
      2147483643  status=0 NEW      buyer=customer2
      2147483645  status=0 NEW      buyer=customer2
      2147483641  status=2 CANCELED buyer=customer2
      2147483647  status=2 CANCELED buyer=customer2
      2147483649  status=0 NEW      buyer=customer2
      2147483642  status=2 CANCELED buyer=customer1
      2147483640  status=2 CANCELED buyer=customer1
      2147483648  status=1 FINISHED buyer=customer1

    Product seed: B0001, B0002, F0001, F0002, C0001, C0002, D0001, D0002
    ItemForm field: productId + quantity (KHÔNG phải count)
    """

    order_items = [
        # TC-ST-01: Customer2 hủy đơn NEW của chính mình → 200
        make_request(
            "TC-ST-01: Customer hủy đơn NEW (Hợp lệ)",
            "PATCH",
            ["order", "cancel", "2147483643"],
            token_var="customer2_token",
            expected_status=200
        ),
        # TC-ST-02: Employee finish đơn NEW → 200
        make_request(
            "TC-ST-02: Employee finish đơn NEW (Hợp lệ)",
            "PATCH",
            ["order", "finish", "2147483645"],
            token_var="employee_token",
            expected_status=200
        ),
        # TC-ST-03: Finish đơn CANCELED → hiện 500 (SCRUM-59), sau fix = 400
        make_request(
            "TC-ST-03: Finish đơn CANCELED (SCRUM-59 - kỳ vọng 400)",
            "PATCH",
            ["order", "finish", "2147483642"],
            token_var="employee_token",
            expected_status=400
        ),
        # TC-ST-04: Cancel đơn FINISHED → hiện 500 (SCRUM-59), sau fix = 400
        make_request(
            "TC-ST-04: Cancel đơn FINISHED (SCRUM-59 - kỳ vọng 400)",
            "PATCH",
            ["order", "cancel", "2147483648"],
            token_var="employee_token",
            expected_status=400
        ),
        # TC-ST-05: Hủy lần 2 cùng đơn (đã cancel ở TC-ST-01) → hiện 500, sau fix 400
        make_request(
            "TC-ST-05: Hủy lần 2 cùng đơn (Idempotency - SCRUM-59)",
            "PATCH",
            ["order", "cancel", "2147483643"],
            token_var="customer2_token",
            expected_status=400
        ),
        # TC-ST-06: Customer cố finish → 401
        make_request(
            "TC-ST-06: Customer cố finish đơn (bị cấm)",
            "PATCH",
            ["order", "finish", "2147483649"],
            token_var="customer_token",
            expected_status=401
        ),
        # TC-ST-07: Employee hủy đơn của customer2 → 200
        make_request(
            "TC-ST-07: Employee hủy đơn bất kỳ (Hợp lệ)",
            "PATCH",
            ["order", "cancel", "2147483649"],
            token_var="employee_token",
            expected_status=200
        ),
        # TC-ST-08: Customer1 hủy đơn của customer2 → 401
        # Lưu ý: nếu chạy sau TC-ST-07 thì đơn đã CANCELED, vẫn trả 401 vì ownership
        make_request(
            "TC-ST-08: Customer hủy đơn người khác (bị cấm)",
            "PATCH",
            ["order", "cancel", "2147483647"],
            token_var="customer_token",
            expected_status=401
        ),
    ]

    cart_items = [
        # TC-ST-09: Guest add (có thể 401 nếu API bắt buộc login - tùy code)
        make_request(
            "TC-ST-09: Guest thêm vào giỏ",
            "POST",
            ["cart", "add"],
            token_var=None,
            body={"productId": "F0001", "quantity": 2},
            expected_status=200
        ),
        # TC-ST-10: Customer add
        make_request(
            "TC-ST-10: Customer thêm vào giỏ",
            "POST",
            ["cart", "add"],
            token_var="customer_token",
            body={"productId": "F0001", "quantity": 3},
            expected_status=200
        ),
        # TC-ST-11: Login (merge) - dùng đúng endpoint /login + credential seed
        make_request(
            "TC-ST-11: Login (merge cart)",
            "POST",
            ["login"],
            token_var=None,
            body={"username": "customer1@email.com", "password": "123"},
            expected_status=200
        ),
        # TC-ST-12: Checkout có item
        make_request(
            "TC-ST-12: Checkout có item",
            "POST",
            ["cart", "checkout"],
            token_var="customer_token",
            body={},
            expected_status=200
        ),
        # TC-ST-13: Checkout giỏ trống
        make_request(
            "TC-ST-13: Checkout giỏ trống (Blocked)",
            "POST",
            ["cart", "checkout"],
            token_var="customer_token",
            body={},
            expected_status=400
        ),
        # TC-ST-14 / 15: remove - endpoint thực tế cần kiểm tra code
        # CartController có PUT /cart/{itemId} và có thể DELETE
        # Tạm giữ POST /cart/remove nếu project có custom, không thì bỏ
    ]

    return {
        "name": "State Transition (Order + Cart)",
        "description": (
            "Map đúng seed data + API thật.\n"
            "SCRUM-59: TC-ST-03/04/05 hiện trả 500, sau khi thêm @ControllerAdvice sẽ trả 400.\n"
            "Chạy Login (Customer/Employee/Customer2) trước khi chạy folder này.\n"
            "Thứ tự quan trọng: TC-ST-01 phải chạy trước TC-ST-05."
        ),
        "item": [
            {
                "name": "1. Order Transitions",
                "item": order_items
            },
            {
                "name": "2. Cart Operations",
                "item": cart_items
            }
        ]
    }


def main():
    with open(INPUT_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)

    # Xóa folder State Transition cũ (mọi tên chứa "State Transition")
    data["item"] = [
        folder for folder in data.get("item", [])
        if "State Transition" not in folder.get("name", "")
    ]

    # Thêm folder mới
    data["item"].append(build_state_transition_folder())

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print("OK - Đã cập nhật folder State Transition")
    print("Chạy trước: Customer Login, Employee Login, Customer2 Login")
    print("Sau đó chạy TC-ST-03 và TC-ST-04 để confirm SCRUM-59 (hiện phải ra 500)")


if __name__ == "__main__":
    main()