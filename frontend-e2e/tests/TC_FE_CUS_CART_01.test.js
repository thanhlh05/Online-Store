Feature('TC_FE_CUS_CART_01 - Add Product to Cart');

Scenario('Add product to cart from detail page', ({ I }) => {
  // Step 1: Login as customer first
  I.loginAsCustomer();

  // Step 2: Navigate to product detail page
  // Using B0002 (Spring In Action) to avoid interference from TC_FE_MGR_03
  // which modifies B0001's price to negative
  I.amOnPage('/product/B0002');
  I.wait(1);

  // Verify product details are displayed
  I.see('Spring In Action');
  I.see('Learn Spring');
  I.see('$20.00');

  // Step 3: Set quantity to 2
  I.fillField('input[name="count"]', '2');

  // Step 4: Click Add to Cart
  I.click('Add to Cart');
  I.wait(2);

  // Step 5: Verify redirect to cart page
  I.see('My Cart');

  // Step 6: Verify product is in cart with correct quantity
  I.see('Spring In Action');
  I.see('$20.00');

  // Verify subtotal = price x quantity = 20 x 2 = 40
  I.see('$40.00');

  // Verify cart total is updated
  I.see('Total:');
});
