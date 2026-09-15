Feature('TC_FE_CUS_LOG_04 - Logout clears session and redirects');

Scenario('Sign Out clears JWT, shows logged out message, cart stays accessible', ({ I }) => {
  I.loginAsCustomer();

  // Sign Out from navigation bar
  I.click('Sign Out');
  I.wait(2);

  // Redirected to /login with logout=true param and info message
  I.seeInCurrentUrl('/login');
  I.seeInCurrentUrl('logout=true');
  I.see('You have been logged out.');

  // JWT removed from localStorage
  I.executeScript(() => {
    if (localStorage.getItem('currentUser')) {
      throw new Error('JWT still present in localStorage after logout');
    }
    if (localStorage.getItem('currentUser') !== null && localStorage.getItem('currentUser') !== undefined) {
      return 'present';
    }
    return 'cleared';
  });

  // /cart route is not protected - page loads and shows empty cart message
  I.amOnPage('/cart');
  I.wait(1);
  I.see('Cart is empty. Go to get something! :)');
});