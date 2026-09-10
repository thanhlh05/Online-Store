Feature('TC_FE_MGR_04 - Manager edits product description');

Scenario('Manager updates description and it persists', ({ I }) => {
  I.loginAsManager();

  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.see('Edit Product');

  I.fillField('#productDescription', 'Books for learning Java language.');
  I.click('Submit');
  I.wait(2);

  I.amOnPage('/seller/product/B0001/edit');
  I.wait(2);
  I.seeInField('#productDescription', 'Books for learning Java language.');
});