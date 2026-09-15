Feature('TC_FE_CUS_REG_04 - Register empty required fields');

Scenario('Empty required fields show errors and disable submit', ({ I }) => {
  I.amOnPage('/register');

  // Touch each field (click then blur via Tab) to trigger required errors
  I.click('#email');
  I.pressKey('Tab');
  I.click('#name');
  I.pressKey('Tab');
  I.click('#password');
  I.pressKey('Tab');
  I.click('#phone');
  I.pressKey('Tab');
  I.click('#address');
  I.pressKey('Tab');

  I.see('Email is required.');
  I.see('Name is required.');
  I.see('Password is required.');
  I.see('Phone is required.');
  I.see('Address is required.');

  I.seeElement('button[type="submit"]:disabled');
  I.seeInCurrentUrl('/register');
});