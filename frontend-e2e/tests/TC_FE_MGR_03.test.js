/**
 * TC_FE_MGR_03 - Edit Product with Negative Price
 *
 * INTENTIONAL FAILURE TEST:
 * This test targets a REAL BUG in the application.
 *
 * BUG DESCRIPTION:
 * The backend ProductController edit endpoint (PUT /seller/product/{id}/edit)
 * uses @Valid annotation on ProductInfo, but ProductInfo entity only has
 * @NotNull on productPrice - there is NO @Min(0) or similar constraint.
 *
 * This means the backend ACCEPTS negative prices without any validation error.
 * The test expects the form to reject negative prices, but the system will
 * actually accept them - causing this test to FAIL.
 *
 * This is an EXPECTED FAILURE that demonstrates automation can catch real bugs.
 * The test should be marked as "known issue" in test reports.
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
