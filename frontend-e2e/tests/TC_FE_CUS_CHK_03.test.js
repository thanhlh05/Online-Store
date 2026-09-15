/**
 * TC_FE_CUS_CHK_03 - Checkout redirect unauthenticated user to login (REQ-CART-03).
 * Guest không thể thêm hàng qua UI (xem CART_06), nên test seed cookie cart
 * trực tiếp để có nút Checkout, rồi xác nhận redirect /login?returnUrl=%2Fcart.
 */

Feature('TC_FE_CUS_CHK_03 - Unauthenticated checkout redirects to login');

Scenario('Checkout as guest redirects to login with returnUrl', ({ I }) => {
  // Load app page first (cookie can only be set on app origin, not about:blank)
  I.amOnPage('/');

  // Seed guest local cart cookie so Checkout button renders
  I.executeScript(() => {
    const cart = {
      B0002: {
        productId: 'B0002',
        productName: 'Spring In Action',
        productPrice: 20,
        productStock: 195,
        productIcon: 'https://example.com/spring.jpg',
        productDescription: 'Learn Spring',
        categoryType: 0,
        productStatus: 0,
        count: 1
      }
    };
    document.cookie = 'cart=' + encodeURIComponent(JSON.stringify(cart)) + '; path=/';
  });

  I.amOnPage('/cart');
  I.wait(2);
  I.see('Spring In Action');
  I.see('Checkout');

  // Not logged in -> redirect to /login?returnUrl=%2Fcart
  I.click('Checkout');
  I.wait(2);
  I.seeInCurrentUrl('/login');
  I.seeInCurrentUrl('returnUrl=%2Fcart');

  // Login -> redirected back to /cart
  I.fillField('input[name="email"]', 'customer1@email.com');
  I.fillField('input[name="password"]', '123');
  I.checkOption('#remember_me');
  I.click('Sign In');
  I.wait(2);

  I.seeInCurrentUrl('/cart');
  I.see('Spring In Action');
});