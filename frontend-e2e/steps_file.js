const { I } = inject();

module.exports = function() {
  return actor({
    loginAsCustomer() {
      I.amOnPage('/login');
      I.fillField('input[name="email"]', 'customer1@email.com');
      I.fillField('input[name="password"]', '123');
      I.checkOption('#remember_me');
      I.click('Sign In');
      I.wait(2);
    },

    loginAsManager() {
      I.amOnPage('/login');
      I.fillField('input[name="email"]', 'manager1@email.com');
      I.fillField('input[name="password"]', '123');
      I.checkOption('#remember_me');
      I.click('Sign In');
      I.wait(2);
    },

    loginAsEmployee() {
      I.amOnPage('/login');
      I.fillField('input[name="email"]', 'employee1@email.com');
      I.fillField('input[name="password"]', '123');
      I.checkOption('#remember_me');
      I.click('Sign In');
      I.wait(2);
    }
  });
};
