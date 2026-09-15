Feature('TC_FE_CUS_LOG_03 - Login empty fields');

Scenario('Empty login fields show errors and disable Sign In', ({ I }) => {
  I.amOnPage('/login');

  // Touch both fields to trigger required errors
  I.click('#email');
  I.pressKey('Tab');
  I.click('#password');
  I.pressKey('Tab');

  // Known issue SCRUM-45: login error divs use [hidden]="field.valid || field.pristine",
  // so empty fields (never dirty) never show "Email is required". Test FAILS until fixed.
  I.see('Email is required');
  I.seeElement('button[type="submit"]:disabled');
  I.seeInCurrentUrl('/login');
});