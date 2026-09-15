Feature('TC_FE_MGR_01 - Manager edits product name');

Scenario('Manager renames product and change persists', ({ I }) => {
  I.loginAsManager();

  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.see('Edit Product');

  I.fillField('#productName', 'Core java');
  I.click('Submit');
  I.wait(2);

  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.see('Edit Product');
  I.seeInField('#productName', 'Core java');
});