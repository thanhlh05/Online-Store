Feature('TC_FE_CUS_REG_06 - Register password at min length (BVA)');

Scenario('Password exactly minlength=3 is valid and redirects to login', ({ I }) => {
  // Unique email to avoid duplicate registration on re-runs
  const email = `bva_${Date.now()}@email.com`;

  I.amOnPage('/register');

  I.fillField('input[name="email"]', email);
  I.fillField('input[name="name"]', 'BVA User 2');
  I.fillField('input[name="password"]', 'abc');
  I.fillField('input[name="phone"]', '0901234567');
  I.fillField('input[name="address"]', '321 BVA Street');

  I.click('Sign Up');
  I.wait(2);

  I.seeInCurrentUrl('/login');
});