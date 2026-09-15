Feature('TC_FE_EMP_06 - Employee orders list');

Scenario('Employee can open orders', ({ I }) => {
  I.loginAsEmployee();
  I.amOnPage('/order');
  I.wait(2);
  I.seeElement('table');
});