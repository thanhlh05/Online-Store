Feature('TC_FE_CUS_CHK_04 - Customer orders list');

Scenario('Customer can view orders table', ({ I }) => {
  I.loginAsCustomer();
  I.amOnPage('/order');
  I.wait(2);
  I.see('Orders');
  I.seeElement('table');
});