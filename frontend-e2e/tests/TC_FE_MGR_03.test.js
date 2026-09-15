/**
 * TC_FE_MGR_03 - Known issue SCRUM-81
 * Backend có thể trả 400 khi price âm; FE không hiển thị message lỗi.
 * Test kỳ vọng thấy "Price must be greater than 0" → FAIL cho đến khi FE fix.
 */

Feature('TC_FE_MGR_03 - Edit Product with Negative Price (Known Bug)');

Scenario('Manager edits product with negative price should be rejected', ({ I }) => {
  // Step 1: Login as manager (with Remember me for persistence)
  I.loginAsManager();

  // Step 2: Navigate to seller product list
  I.amOnPage('/seller/product');
  I.wait(2);

  // Verify we're on the product management page
  I.see('Products');
  I.seeElement('table');

  // Step 3: Click Edit on first product (B0001 - Core Java)
  I.click(locate('a').withText('Edit').first());
  I.wait(2);

  // Step 4: Verify we're on edit page
  I.see('Edit Product');

  // Step 5: Clear price field and enter negative value
  I.fillField('input[name="productPrice"]', '-50');

  // Step 6: Submit the form
  I.click('Submit');
  I.wait(2);

  /**
   * EXPECTED RESULT (what SHOULD happen):
   * - Form should reject negative price
   * - Error message should be displayed
   * - Product should NOT be updated
   *
   * ACTUAL RESULT (the bug):
   * - Backend accepts negative price (no @Min(0) constraint)
   * - Product gets updated with negative price
   * - No error is shown to user
   *
   * This test will FAIL because it expects proper validation
   * that doesn't exist in the current codebase.
   */
  I.see('Price must be greater than 0');
});
