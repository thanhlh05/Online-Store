Feature('TC_FE_NEG_02 - No Delete product UI');

Scenario('Manager has no Delete on product list', ({ I }) => {
  I.loginAsManager();
  I.amOnPage('/seller/product');
  I.wait(2);
  I.see('Edit');
  I.dontSee('Delete');
});

Scenario('Employee has no Delete on product list', ({ I }) => {
  I.loginAsEmployee();
  I.amOnPage('/seller/product');
  I.wait(2);
  I.dontSee('Delete');
});