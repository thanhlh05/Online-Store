Feature('TC_FE_CUS_CART_03 - Add quantity 1');

Scenario('Add product with quantity 1', ({ I }) => {
  I.loginAsCustomer();
  I.amOnPage('/product/B0002');
  I.wait(1);
  I.fillField('input[name="count"]', '1');
  I.click('Add to Cart');
  I.wait(2);
  I.see('My Cart');
  I.see('Spring In Action');
  I.see('Total');
});