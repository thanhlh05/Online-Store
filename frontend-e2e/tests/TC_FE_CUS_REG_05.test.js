Feature('TC_FE_CUS_REG_05 - Register password below min length (BVA)');

Scenario('Password shorter than minlength=3 shows error and disables submit', ({ I }) => {
  I.amOnPage('/register');

  I.fillField('input[name="email"]', 'bva@email.com');
  I.fillField('input[name="name"]', 'BVA User');
  I.fillField('input[name="password"]', 'ab');
  I.fillField('input[name="phone"]', '0901234567');
  I.fillField('input[name="address"]', '789 BVA Street');

  // Trigger validation error on password field
  I.click('input[name="password"]');
  I.pressKey('Tab');

  I.see('Password must be at least 3 characters long.');
  I.seeElement('button[type="submit"]:disabled');
  I.seeInCurrentUrl('/register');
});