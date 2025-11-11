function logToSessionDetail() {
  cy.login([
    {
      id: 1,
      name: 'Première session',
      date: '2025-08-30T00:00:00.000+00:00',
      teacher_id: 2,
      description: 'Premiere création de session',
      users: [],
      createdAt: '2025-08-27T13:39:23',
      updatedAt: '2025-09-19T17:09:45',
    },
    {
      id: 2,
      name: 'Première session',
      date: '2025-08-30T00:00:00.000+00:00',
      teacher_id: 2,
      description: 'deuxieme création de session',
      users: [],
      createdAt: '2025-08-27T13:39:23',
      updatedAt: '2025-09-19T17:09:45',
    },
  ]);

  cy.url().should('include', '/sessions');
  cy.get('mat-card .items').should('be.visible');

  cy.get('button.mat-raised-button')
    .filter((index, el) => el.textContent.includes('Detail')) // recupération des bouton qui on le texte "Detail"
    .eq(sessionDetail.id - 1) // on clique sur celui se la session qu'on veux (vu qu'on mock l'api en renvoyant sessionDetail on prend donc son id)
    .click();

  cy.url().should('include', '/detail/1');
}

let sessionDetail = {
  id: 1,
  name: 'Première session',
  date: '2025-08-30T00:00:00.000+00:00',
  teacher_id: 2,
  description: 'Premiere création de session',
  users: [],
  createdAt: '2025-08-27T13:39:23',
  updatedAt: '2025-09-19T17:09:45',
};

describe('Session detail spec', () => {
  it('should not be accessible from an url logged or not', () => {
    cy.login([sessionDetail]);

    cy.visit('/sessions/detail/1');
    cy.url().should('include', '/login');
    cy.visit('/sessions/detail/1');
    cy.url().should('include', '/login');
  });

  it('Should return to the sessions page on click on return button', () => {
    cy.sessionDetailRequestIntercept(sessionDetail);
    logToSessionDetail();

    cy.get('button[mat-icon-button]')
      .find('mat-icon')
      .should('have.text', 'arrow_back')
      .and('be.visible')
      .click();

    cy.url().should('include', '/sessions');
  });

  it('Should be accessible with the detail button', () => {
    cy.sessionDetailRequestIntercept(sessionDetail, sessionDetail.id);

    cy.intercept('GET', '/api/teacher/2', {
      id: 2,
      lastName: 'THIERCELIN',
      firstName: 'Hélène',
      createdAt: '2025-10-13T00:14:03',
      updatedAt: '2025-10-13T00:14:03',
    }).as('teacherInfo');

    logToSessionDetail();

    cy.wait('@sessionDetail');

    // Vérifie le titre h1
    cy.get('mat-card-title h1').should('contain.text', 'Première Session');

    // Vérifie le bouton retour
    cy.get('button[mat-icon-button]')
      .find('mat-icon')
      .should('have.text', 'arrow_back')
      .and('be.visible');

    // Vérifie le bouton delete
    cy.get('button[mat-raised-button][color="warn"]')
      .should('contain.text', 'Delete')
      .and('be.visible');

    // Vérifie que le nom du teacher est affiché
    cy.contains('.mat-card-subtitle', 'Hélène THIERCELIN').should('be.visible');

    // Vérifie la description
    cy.get('.description p').should('contain.text', 'Description:');

    // Vérifie la date de la session (près de l'icône calendrier)
    cy.get('mat-card-content')
      .contains('calendar_month')
      .parents('.mat-card-content')
      .should('contain.text', 'August 30, 2025');

    // Vérifie la date de création
    cy.get('.created').should('contain.text', 'August 27, 2025');

    // Vérifie la dernière mise à jour
    cy.get('.updated').should('contain.text', 'September 19, 2025');
  });

  it('Should delete a session when click on delete button ', () => {
    cy.sessionDetailRequestIntercept(sessionDetail);

    cy.intercept('GET', '/api/teacher/2', {
      id: 2,
      lastName: 'THIERCELIN',
      firstName: 'Hélène',
      createdAt: '2025-10-13T00:14:03',
      updatedAt: '2025-10-13T00:14:03',
    }).as('teacherInfo');

    cy.intercept('DELETE', `/api/session/1`, {
      statusCode: 200,
    }).as('deleteSession');

    logToSessionDetail();

    cy.get('button[mat-raised-button][color="warn"]')
      .contains('Delete')
      .click();

    cy.wait('@deleteSession');

    cy.get('snack-bar-container')
      .should('be.visible')
      .find('simple-snack-bar span.mat-simple-snack-bar-content')
      .should('contain.text', 'Session deleted !');

    cy.url().should('include', '/sessions');
  });
});
// verifié que lorsqu'on a un autre user on participate bien
