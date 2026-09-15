Feature('TC_FE_EMP_01 - Employee edits product name');

Scenario('Employee renames product and change persists', ({ I }) => {
  I.loginAsEmployee();

  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.see('Edit Product');

  I.fillField('#productName', 'Core java');
  I.click('Submit');
  I.wait(2);

  // Reopen edit page and verify the name was saved
  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.see('Edit Product');
  I.seeInField('#productName', 'Core java');
});