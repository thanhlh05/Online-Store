Feature('TC_FE_CUS_CHK_01 - Successful Checkout');

Scenario('Checkout with items in cart', ({ I }) => {
  I.loginAsCustomer();

  I.amOnPage('/product/B0002');
  I.wait(1);
  I.fillField('input[name="count"]', '1');
  I.click('Add to Cart');
  I.wait(2);

  I.see('My Cart');
  I.see('Spring In Action');

  I.click('Checkout');
  I.wait(3);

  // Đúng expected Excel: về trang chủ, KHÔNG bắt buộc thấy tên SP ngay sau checkout
  I.see('Get Whatever You Want!');

  I.amOnPage('/cart');
  I.wait(1);
  I.see('Cart is empty. Go to get something! :)');

  I.amOnPage('/order');
  I.wait(2);
  I.see('Orders');
  I.seeElement('table');
});