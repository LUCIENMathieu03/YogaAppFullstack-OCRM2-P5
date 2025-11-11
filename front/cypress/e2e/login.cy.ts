describe('Login spec', () => {
  it('Login successfull', () => {
    cy.login();

    cy.url().should('include', '/sessions');
  });

  it('Logout succesfully', () => {
    cy.login();

    cy.url().should('include', '/sessions');

    cy.contains('Logout').click();

    cy.url().should('include', '/');
  });

  it('Login not successfull', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid credentials' },
    }).as('loginFailed');

    cy.visit('/login');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      'wrongpassword{enter}{enter}'
    );
    cy.wait('@loginFailed');

    cy.url().should('include', '/login');

    cy.contains('p.error', 'An error occurred').should('be.visible');
  });
});
