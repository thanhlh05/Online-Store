Feature('TC_FE_CUS_CART_05 - Remove from cart');

Scenario('Remove product from cart', ({ I }) => {
  I.loginAsCustomer();
  I.amOnPage('/product/B0002');
  I.wait(1);
  I.fillField('input[name="count"]', '1');
  I.click('Add to Cart');
  I.wait(2);
  I.see('Spring In Action');
  I.click('Remove');
  I.wait(1);
  I.see('Cart is empty');
});