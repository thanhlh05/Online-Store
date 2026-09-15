Feature('TC_FE_MGR_06 - Manager views orders list');

Scenario('Manager can open orders table with full columns', ({ I }) => {
  I.loginAsManager();

  I.amOnPage('/order');
  I.wait(2);

  I.see('Orders');
  I.seeElement('table');
  I.see('Customer Email');
  I.see('Total');
  I.see('Status');
  I.see('Action');
});