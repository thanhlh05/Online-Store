/**
 * TC_FE_CUS_CART_06 - Guest cart không được hỗ trợ (REQ-CART-02).
 * Excel: "Guest không được thêm vào cart. Chỉ Customer đã login mới thêm được.
 * Local cart không còn hỗ trợ." (kết quả thực tế được ghi nhận PASS)
 */

Feature('TC_FE_CUS_CART_06 - Guest cart not supported, only logged-in customer adds');

Scenario('Guest cannot add to cart; logged-in customer can', ({ I }) => {
  // Step 1: Guest adds product -> cart stays empty
  I.amOnPage('/product/B0002');
  I.wait(1);
  I.fillField('input[name="count"]', '1');
  I.click('Add to Cart');
  I.wait(2);

  I.seeInCurrentUrl('/cart');
  I.see('Cart is empty. Go to get something! :)');

  // Step 2: Login as customer
  I.click('Sign In');
  I.fillField('input[name="email"]', 'customer1@email.com');
  I.fillField('input[name="password"]', '123');
  I.checkOption('#remember_me');
  I.click('Sign In');
  I.wait(2);

  // Step 3: Logged-in customer adds -> cart shows product
  I.amOnPage('/product/B0002');
  I.wait(1);
  I.fillField('input[name="count"]', '1');
  I.click('Add to Cart');
  I.wait(2);

  I.seeInCurrentUrl('/cart');
  I.see('Spring In Action');
});