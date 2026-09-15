Feature('TC_FE_MGR_05 - Manager edits product stock');

Scenario('Manager updates stock to valid value and it persists', ({ I }) => {
  I.loginAsManager();

  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.see('Edit Product');

  I.fillField('#productStock', '50');
  I.click('Submit');
  I.wait(2);

  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.seeInField('#productStock', '50');
});