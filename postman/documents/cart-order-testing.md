# Cart and Order role testing

## Tokens used
- `customer_token`: used by Cart requests through collection-level bearer auth.
- `employee_token`: used only by `Order > Complete order` through a request-level bearer override.
- `token`: still updated by `Customer Login` for backward compatibility.

## Test steps
1. Select the `dev` environment and set `base_url`.
2. Run `User > Customer Login`.
   - This saves `customer_token` and also updates `token`.
3. Run Cart requests under `Cart`.
   - They inherit the collection auth and use `{{customer_token}}`.
4. Run `User > Employee Login`.
   - This saves `employee_token`.
5. Run `Order > Complete order`.
   - It uses `{{employee_token}}` via a minimal request-level auth override.

## Why one request override exists
- Collection auth now points to the customer token so Cart testing works by default.
- `Order > Complete order` needs employee/manager credentials, so it has its own bearer token override.
- Other requests still keep `Authorization` inherited from parent.
