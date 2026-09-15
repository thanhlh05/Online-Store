Feature('TC_FE_NEG_01 - No Add Product UI');

Scenario('Manager has no Add Product button', ({ I }) => {
  I.loginAsManager();
  I.amOnPage('/seller/product');
  I.wait(2);
  I.see('Products');
  I.dontSee('Add Product');
  I.dontSee('New Product');
  I.dontSee('Create Product');
});