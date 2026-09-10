Feature('TC_FE_MGR_09 - Finished order shows only Show action');

Scenario('Finished order cannot be re-finished or cancelled', async ({ I }) => {
  I.createOrderAsCustomer();
  I.click('Sign Out');
  I.wait(2);
  I.loginAsManager();

  I.amOnPage('/order');
  I.wait(2);
  I.see('Orders');

  I.usePlaywrightTo('finish own order and verify only Show action remains', async ({ page }) => {
    const handle = await page.waitForFunction(() => {
      const rows = [...document.querySelectorAll('tbody tr')];
      const row = rows.find(r => r.querySelectorAll('td')[6]?.textContent.trim() === 'New');
      return row ? row.querySelector('th').textContent.trim() : false;
    }, undefined, { timeout: 15000 });
    const orderId = String(await handle.jsonValue());
    if (!orderId) throw new Error('No order with status New found');

    await page.evaluate(id => {
      const rows = [...document.querySelectorAll('tbody tr')];
      const row = rows.find(r => r.querySelector('th')?.textContent.trim() === String(id));
      if (!row) throw new Error(`Row ${id} not found`);
      const a = [...row.querySelectorAll('a')].find(x => x.textContent.trim() === 'Finish');
      if (!a) throw new Error(`Finish link not found in row ${id}`);
      a.click();
    }, orderId);
    await page.waitForFunction(id => {
      const rows = [...document.querySelectorAll('tbody tr')];
      const row = rows.find(r => r.querySelector('th')?.textContent.trim() === String(id));
      return !!(row && row.querySelectorAll('td')[6]?.textContent.trim() === 'Finished');
    }, orderId, { timeout: 15000 });

    const actions = await page.evaluate(id => {
      const rows = [...document.querySelectorAll('tbody tr')];
      const row = rows.find(r => r.querySelector('th')?.textContent.trim() === String(id));
      return row ? [...row.querySelectorAll('td:last-child a')].map(a => a.textContent.trim()) : null;
    }, orderId);
    if (JSON.stringify(actions) !== JSON.stringify(['Show'])) {
      throw new Error(`Unexpected action links for finished order: ${actions}`);
    }
  });
});