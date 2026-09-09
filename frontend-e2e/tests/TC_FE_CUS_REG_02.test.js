Feature('TC_FE_CUS_REG_02 - Register duplicate email');

Scenario('Register with existing email fails', ({ I }) => {
  I.amOnPage('/register');
  I.fillField('input[name="email"]', 'customer1@email.com');
  I.fillField('input[name="name"]', 'Duplicate User');
  I.fillField('input[name="password"]', 'pass123');
  I.fillField('input[name="phone"]', '0901234567');
  I.fillField('input[name="address"]', '456 Dup Street');
  I.click('Sign Up');
  I.wait(2);
  I.dontSee('Get Whatever You Want!');
  I.seeInCurrentUrl('/register');
});