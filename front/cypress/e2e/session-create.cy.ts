const teachersInfo = [
  {
    id: 1,
    lastName: 'DELAHAYE',
    firstName: 'Margot',
    createdAt: '2025-10-13T00:14:03',
    updatedAt: '2025-10-13T00:14:03',
  },
  {
    id: 2,
    lastName: 'THIERCELIN',
    firstName: 'Hélène',
    createdAt: '2025-10-13T00:14:03',
    updatedAt: '2025-10-13T00:14:03',
  },
];

function logToCreatePage() {
  cy.login([]);

  cy.get('button[routerlink="create"]').contains('.ml1', 'Create').click();

  cy.url().should('include', `/create`);
}

describe('Session create spec', () => {
  it('On the create page should return to the session page when the back arrow is clicked', () => {
    logToCreatePage();
    cy.contains('mat-card-title button mat-icon', 'arrow_back').click();
    cy.url().should('include', '/sessions');
  });

  it('Should not be able to create a session if the form has empty field', () => {
    logToCreatePage();

    cy.get('input[formcontrolname="name"]').clear();

    cy.get('input[formcontrolname="date"]').clear();

    cy.get('textarea[formcontrolname="description"]').clear();

    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('Should create a new session', () => {
    cy.intercept('GET', '/api/teacher', teachersInfo);
    cy.intercept('POST', '/api/session', {
      statusCode: 200,
    }).as('createSession');

    logToCreatePage();

    cy.get('input[formcontrolname="name"]').type('Nouvelle session');

    cy.get('input[formcontrolname="date"]').type('2025-10-10');

    cy.get('mat-select[formcontrolname="teacher_id"]').click();
    cy.get('mat-option').contains('Margot DELAHAYE').click();

    cy.get('textarea[formcontrolname="description"]').type(
      'Description pour la nouvelle session'
    );
    cy.get('button[type="submit"]').click();

    cy.wait('@createSession').its('request.body').should('include', {
      name: 'Nouvelle session',
      date: '2025-10-10',
      teacher_id: 1,
      description: 'Description pour la nouvelle session',
    });

    cy.get('simple-snack-bar .mat-simple-snack-bar-content')
      .should('be.visible')
      .and('contain.text', 'Session created !');
  });
});
