Feature('TC_FE_CUS_CART_01 - Add Product to Cart');

Scenario('Add product to cart from detail page', ({ I }) => {
  I.loginAsCustomer();

  // B0002 tránh ảnh hưởng nếu MGR_03 từng sửa B0001
  I.amOnPage('/product/B0002');
  I.wait(1);

  I.see('Spring In Action');
  I.see('Learn Spring');
  I.see('$20.00');

  I.fillField('input[name="count"]', '2');
  I.click('Add to Cart');
  I.wait(2);

  I.see('My Cart');
  I.see('Spring In Action');
  I.see('$20.00');
  // Không hard-code $40.00 (dễ fail do data/locale — SCRUM-79)
  I.see('Total');
});