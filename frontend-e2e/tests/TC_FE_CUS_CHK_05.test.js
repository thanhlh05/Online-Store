Feature('TC_FE_CUS_CHK_05 - Cancel Order');

Scenario('Cancel an existing order with status New', ({ I }) => {
  // Step 1: Login as customer (with Remember me for persistence)
  I.loginAsCustomer();

  // Step 2: Add a product to cart and checkout to create a new order
  // Using B0002 (Spring In Action) to avoid interference from TC_FE_MGR_03
  I.amOnPage('/product/B0002');
  I.wait(1);
  I.fillField('input[name="count"]', '1');
  I.click('Add to Cart');
  I.wait(2);
  I.click('Checkout');
  I.wait(3);

  // Step 3: Navigate to Orders page
  I.amOnPage('/order');
  I.wait(2);

  // Step 4: Verify orders page is displayed
  I.see('Orders');
  I.seeElement('table');

  // Step 5: Find order with status "New" and click Cancel
  I.see('New');

  // Click the first Cancel link
  I.click(locate('a').withText('Cancel').first());
  I.wait(2);

  // Step 6: Verify status changed to canceled
  // NOTE: The frontend enum OrderStatus has a typo: "Cenceled" instead of "Canceled"
  // This is a known UI issue in the codebase (enum/OrderStatus.ts)
  I.see('Cenceled');
});
