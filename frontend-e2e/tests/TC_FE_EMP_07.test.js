Feature('TC_FE_EMP_07 - Employee views order detail');

Scenario('Employee opens order detail via Show button', ({ I }) => {
  I.loginAsEmployee();

  I.amOnPage('/order');
  I.wait(2);
  I.see('Orders');
  I.seeElement('table');

  I.click(locate('a').withText('Show').first());
  I.wait(2);

  I.see('Order Detail');
  I.seeElement('table');
  I.see('Total');
});