Feature('TC_FE_CUS_CHK_02 - Empty cart');

Scenario('Empty cart shows message and no Checkout', ({ I }) => {
  I.loginAsCustomer();
  I.amOnPage('/cart');
  I.wait(1);
  I.see('Cart is empty');
  I.dontSee('Checkout');
});