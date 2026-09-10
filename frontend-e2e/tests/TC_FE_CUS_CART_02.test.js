Feature('TC_FE_CUS_CART_02 - Add quantity above stock (BVA)');

Scenario('Quantity above stock is clamped to stock value', async ({ I }) => {
  I.loginAsCustomer();

  // Stock of B0001 may have changed by earlier runs; read it dynamically
  I.amOnPage('/product/B0001');
  I.wait(1);
  I.see('Stock:');

  const stock = await I.executeScript(() => {
    const m = document.body.innerText.match(/Stock:\s*(\d+)/i);
    return m ? m[1] : null;
  });
  if (!stock) {
    throw new Error('Stock value not found on product page');
  }

  // Enter stock+1 then blur to trigger validateCount()
  I.fillField('input[name="count"]', String(Number(stock) + 1));
  I.pressKey('Tab');
  I.wait(1);

  // Clamped down to stock value, no error shown
  I.seeInField('input[name="count"]', stock);

  // Can still add to cart with clamped quantity
  I.click('Add to Cart');
  I.wait(2);

  I.see('My Cart');
  I.seeInField('#B0001', stock);

  // Cleanup: remove B0001 from cart (name may have changed across runs)
  I.usePlaywrightTo('remove B0001 from cart', async ({ page }) => {
    const row = page.locator('tbody tr').filter({ has: page.locator('a[href*="B0001"]') }).first();
    await row.locator('a', { hasText: 'Remove' }).click();
    await page.waitForTimeout(1200);
  });
});