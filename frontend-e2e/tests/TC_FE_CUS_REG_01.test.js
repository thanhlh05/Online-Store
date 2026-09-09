Feature('TC_FE_CUS_REG_01 - Register valid data');

Scenario('Register with valid data redirects to login', ({ I }) => {
  const email = `newuser_${Date.now()}@email.com`;
  I.amOnPage('/register');
  I.fillField('input[name="email"]', email);
  I.fillField('input[name="name"]', 'Test User');
  I.fillField('input[name="password"]', 'pass123');
  I.fillField('input[name="phone"]', '0901234567');
  I.fillField('input[name="address"]', '123 Test Street');
  I.click('Sign Up');
  I.wait(2);
  I.seeInCurrentUrl('/login');
});