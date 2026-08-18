Feature('TC_FE_CUS_LOG_01 - Successful Login');

Scenario('Login with valid customer credentials', ({ I }) => {
  I.amOnPage('/login');

  // Verify login form is displayed
  I.see('Sign In');
  I.seeElement('input[name="email"]');
  I.seeElement('input[name="password"]');

  // Fill in valid credentials
  I.fillField('input[name="email"]', 'customer1@email.com');
  I.fillField('input[name="password"]', '123');

  // Check "Remember me" so token persists in localStorage across page navigations
  I.checkOption('#remember_me');

  // Click Sign In
  I.click('Sign In');
  I.wait(2);

  // Verify successful login - should redirect to home page
  I.see('Get Whatever You Want!');

  // Verify navigation bar shows user-specific elements
  I.see('Cart');
  I.see('Orders');
  I.see('Sign Out');

  // Verify JWT token is stored in localStorage
  I.executeScript(() => {
    const token = localStorage.getItem('currentUser');
    if (!token) throw new Error('JWT token not found in localStorage');
    const user = JSON.parse(token);
    if (!user.token) throw new Error('Token property not found');
    if (user.role !== 'ROLE_CUSTOMER') throw new Error('Wrong role: ' + user.role);
  });
});
