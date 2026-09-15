Feature('TC_FE_CUS_LOG_02 - Login wrong password');

Scenario('Login with wrong password shows error', ({ I }) => {
  I.amOnPage('/login');
  I.fillField('input[name="email"]', 'customer1@email.com');
  I.fillField('input[name="password"]', 'wrongpass');
  I.click('Sign In');
  I.wait(2);
  I.see('Invalid username and password');
  I.seeInCurrentUrl('/login');
});