Feature('TC_FE_EMP_02 - Employee edits product price (valid)');

Scenario('Employee updates price to valid value and it persists', ({ I }) => {
  I.loginAsEmployee();

  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.see('Edit Product');

  I.fillField('#productPrice', '90');
  I.click('Submit');
  I.wait(2);

  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.seeInField('#productPrice', '90');
});