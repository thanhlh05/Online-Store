/**
 * TC_FE_CUS_CART_04 - Update quantity in cart with +/- buttons (EP).
 * Dùng sản phẩm riêng F0001 (Chicken). Server cart cho customer1 tồn tại
 * lâu dài trong DB giữa các lần chạy, nên assert theo delta (+1 / -1)
 * thay vì giá trị tuyệt đối. Thao tác + / - thực hiện qua usePlaywrightTo
 * với locator giới hạn đúng dòng sản phẩm để tránh nhầm sản phẩm khác.
 */

Feature('TC_FE_CUS_CART_04 - Update quantity with +/- buttons');

Scenario('Plus button increments quantity, minus button decrements', ({ I }) => {
  I.loginAsCustomer();

  // Add F0001 (Chicken) with quantity 1
  I.amOnPage('/product/F0001');
  I.wait(1);
  I.fillField('input[name="count"]', '1');
  I.click('Add to Cart');
  I.wait(2);

  I.see('My Cart');
  I.see('Chicken');

  I.usePlaywrightTo('verify + increments and - decrements quantity', async ({ page }) => {
    const row = page.locator('tbody tr', { hasText: 'Chicken' }).first();
    const qty = async () => parseInt(await row.locator('input[type=number]').inputValue(), 10);

    const start = await qty();

    await row.locator('.fa-plus').click();
    await page.waitForTimeout(2500);
    if ((await qty()) !== start + 1) {
      throw new Error(`Expected quantity after + to be ${start + 1}, got ${await qty()}`);
    }

    await row.locator('.fa-minus').click();
    await page.waitForTimeout(2500);
    if ((await qty()) !== start) {
      throw new Error(`Expected quantity after - to be ${start}, got ${await qty()}`);
    }

    // Cleanup: remove Chicken from cart
    await row.locator('a', { hasText: 'Remove' }).click();
    await page.waitForTimeout(1500);
  });

  I.dontSee('Chicken');
});