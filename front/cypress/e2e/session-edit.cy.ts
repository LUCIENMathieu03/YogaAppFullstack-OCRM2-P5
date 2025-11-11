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
const sessionDetail = {
  id: 1,
  name: 'Première session',
  date: '2025-08-30T00:00:00.000+00:00',
  teacher_id: 2,
  description: 'Premiere création de session',
  users: [],
  createdAt: '2025-08-27T13:39:23',
  updatedAt: '2025-09-19T17:09:45',
};

function logToUpdatePage(sessionDetail: {
  id: any;
  name?: string;
  date?: string;
  teacher_id?: number;
  description?: string;
  users?: never[];
  createdAt?: string;
  updatedAt?: string;
}) {
  cy.login([sessionDetail]);

  cy.url().should('include', '/sessions');
  cy.get('mat-card .items').should('be.visible');

  cy.get('button.mat-raised-button')
    .filter((index, el) => el.textContent.includes('Edit')) // recupération des bouton qui on le texte "Detail"
    .eq(sessionDetail.id - 1) // on clique sur celui se la session qu'on veux (vu qu'on mock l'api en renvoyant sessionDetail on prend donc son id)
    .click();

  cy.url().should('include', `/update/${sessionDetail.id}`);
}

describe('Session edit spec', () => {
  it('should not be accessible from an url logged or not', () => {
    cy.login([sessionDetail]);

    cy.visit('/sessions/update/1');
    cy.url().should('include', '/login');
    cy.visit('/sessions/update/1');
    cy.url().should('include', '/login');
  });

  it('On the edit page should return to the session page when the back arrow is clicked', () => {
    logToUpdatePage(sessionDetail);
    cy.contains('mat-card-title button mat-icon', 'arrow_back').click();
    cy.url().should('include', '/sessions');
  });

  it('Should display the edit page with all the field filled', () => {
    cy.sessionDetailRequestIntercept(sessionDetail, 1);

    cy.intercept('GET', '/api/teacher', teachersInfo);

    logToUpdatePage(sessionDetail);

    cy.get('input[formcontrolname="name"]').should(
      'have.value',
      'Première session'
    );

    cy.get('input[formcontrolname="date"]').should('have.value', '2025-08-30');

    cy.get(
      'mat-select[formcontrolname="teacher_id"] .mat-select-value-text'
    ).should('contain.text', 'Hélène THIERCELIN');

    cy.get('textarea[formcontrolname="description"]').should(
      'have.value',
      'Premiere création de session'
    );
  });

  it('Should update the form with new value', () => {
    cy.intercept('PUT', '/api/session/1', {
      statusCode: 200,
    }).as('updateRequest');

    cy.sessionDetailRequestIntercept(sessionDetail, 1);

    cy.intercept('GET', '/api/teacher', teachersInfo);

    logToUpdatePage(sessionDetail);

    cy.get('input[formcontrolname="name"]')
      .clear()
      .type('Première session modifié');

    cy.get('input[formcontrolname="date"]').clear().type('2025-10-10');

    cy.get('mat-select[formcontrolname="teacher_id"]').click();
    cy.get('mat-option').contains('Margot DELAHAYE').click();

    cy.get('textarea[formcontrolname="description"]')
      .clear()
      .type('Description de session modifié');

    cy.get('button[type="submit"]').contains('Save').click();

    cy.wait('@updateRequest').its('request.body').should('include', {
      name: 'Première session modifié',
      date: '2025-10-10',
      teacher_id: 1,
      description: 'Description de session modifié',
    });

    cy.url().should('include', '/sessions');

    cy.get('simple-snack-bar .mat-simple-snack-bar-content')
      .should('be.visible')
      .and('contain.text', 'Session updated !');
  });

  it('Should not be able to update if the form has empty field', () => {
    cy.sessionDetailRequestIntercept(sessionDetail, 1);

    cy.intercept('GET', '/api/teacher', teachersInfo);

    logToUpdatePage(sessionDetail);

    cy.get('input[formcontrolname="name"]').clear();

    cy.get('input[formcontrolname="date"]').clear();

    cy.get('textarea[formcontrolname="description"]').clear();

    cy.get('button[type="submit"]').should('be.disabled');
  });
});
