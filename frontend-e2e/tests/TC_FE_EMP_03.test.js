/**
 * TC_FE_EMP_03 - Known issue SCRUM-43 (same root cause as SCRUM-44/81).
 * Backend/FE không có validation min=0 cho price; form chấp nhận giá âm.
 * Test kỳ vọng thấy "Price must be greater than 0" -> FAIL cho đến khi fix.
 * Dùng B0004 để không phá dữ liệu B0001 mà EMP_01/02/04/05 đang dùng.
 */

Feature('TC_FE_EMP_03 - Employee edits product with negative price (Known Bug)');

Scenario('Employee submits negative price should be rejected', ({ I }) => {
  I.loginAsEmployee();

  I.amOnPage('/seller/product/B0004/edit');
  I.wait(2);
  I.see('Edit Product');

  I.fillField('#productPrice', '-30');
  I.click('Submit');
  I.wait(2);

  /**
   * EXPECTED: form rejects negative price + shows error, product unchanged.
   * ACTUAL (bug): backend accepts negative price, product saved, no error.
   * This test FAILS until validation is fixed.
   */
  I.see('Price must be greater than 0');
});