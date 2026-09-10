Feature('TC_FE_MGR_07 - Manager views order detail');

Scenario('Manager opens order detail via Show button', ({ I }) => {
  I.loginAsManager();

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