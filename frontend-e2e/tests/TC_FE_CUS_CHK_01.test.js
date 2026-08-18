Feature('TC_FE_CUS_CHK_01 - Successful Checkout');

Scenario('Checkout with items in cart', ({ I }) => {
  // Step 1: Login as customer (with Remember me for persistence)
  I.loginAsCustomer();

  // Step 2: Navigate to product detail page and add item
  // Using B0002 (Spring In Action) to avoid interference from TC_FE_MGR_03
  I.amOnPage('/product/B0002');
  I.wait(1);
  I.fillField('input[name="count"]', '1');
  I.click('Add to Cart');
  I.wait(2);

  // Step 3: Verify we're on cart page with item
  I.see('My Cart');
  I.see('Spring In Action');

  // Step 4: Click Checkout
  I.click('Checkout');
  I.wait(3);

  // Step 5: Verify redirect to home page (checkout success)
  I.see('Spring In Action');

  // Step 6: Verify cart is now empty
  I.amOnPage('/cart');
  I.wait(1);
  I.see('Cart is empty. Go to get something! :)');

  // Step 7: Verify order was created by checking Orders page
  I.amOnPage('/order');
  I.wait(2);
  I.see('Orders');
  I.seeElement('table');
});
