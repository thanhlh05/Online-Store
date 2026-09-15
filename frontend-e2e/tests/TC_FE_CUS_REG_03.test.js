/**
 * TC_FE_CUS_REG_03 - Register with invalid email format
 * 
 * Kịch bản kiểm tra Validation định dạng Email trên Frontend.
 * Khi Email không đúng định dạng, Frontend Angular sẽ chủ động disable nút Sign Up.
 */

Feature('TC_FE_CUS_REG_03 - Register invalid email');

Scenario('Invalid email disables submit button and stays on register page', ({ I }) => {
  I.amOnPage('/register');
  
  I.fillField('input[name="email"]', 'not-an-email');
  I.fillField('input[name="name"]', 'Test User');
  I.fillField('input[name="password"]', 'pass123');
  I.fillField('input[name="phone"]', '0901234567');
  I.fillField('input[name="address"]', '123 Test Street');

  // Xác nhận nút Sign Up bị disabled do Form Validation phía Client
  I.seeElement('button[type="submit"]:disabled');
  
  // Xác nhận người dùng vẫn ở lại trang /register
  I.seeInCurrentUrl('/register');
});