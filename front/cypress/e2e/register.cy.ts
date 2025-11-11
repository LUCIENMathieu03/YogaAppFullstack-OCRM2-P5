describe('register spec', () => {
  it('Register a new user', () => {
    cy.intercept('POST', '/api/auth/register', {
      body: {
        lastName: 'toto',
        firstName: 'toto',
        email: 'toto3testcypress@toto.com',
        password: 'test!1234',
      },
    }).as('registerRequest');

    cy.visit('/register');

    cy.get('button[type="submit"]').should('be.disabled');
    //on verifie que tout les champs soient bien apparent et on les remplis
    cy.get('input[formcontrolname="firstName"]').type('toto');
    cy.get('input[formcontrolname="lastName"]').type('toto');
    cy.get('input[formcontrolname="email"]').type('toto3testcypress@toto.com');
    cy.get('input[formcontrolname="password"]').type('test!1234');

    cy.get('button[type="submit"]').should('not.be.disabled').click();

    //on verifie que les bonne donné aient bien été envoyé
    cy.wait('@registerRequest').its('request.body').should('include', {
      lastName: 'toto',
      firstName: 'toto',
      email: 'toto3testcypress@toto.com',
      password: 'test!1234',
    });

    cy.url().should('include', '/');
  });

  it('Register a new user - email already taken error', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 409,
      body: {
        message: 'Error: Email is already taken!',
      },
    }).as('registerRequestError');

    cy.visit('/register');

    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formcontrolname="firstName"]').type('toto');
    cy.get('input[formcontrolname="lastName"]').type('toto');
    cy.get('input[formcontrolname="email"]').type('toto3testcypress@toto.com');
    cy.get('input[formcontrolname="password"]').type('test!1234');

    cy.get('button[type="submit"]').should('not.be.disabled').click();

    cy.contains('span.error', 'An error occurred').should('be.visible');

    cy.url().should('include', '/register');
  });
});
