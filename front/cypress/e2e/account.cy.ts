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

let conectedUser = {
  id: 1,
  email: 'toto3testcypress@toto.com',
  lastName: 'toto',
  firstName: 'toto',
  admin: true,
  createdAt: '2025-10-14T18:13:15',
  updatedAt: '2025-10-14T18:13:15',
};

function logToAccountPage(sessionDetail: {
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

  cy.contains('Account').click();

  cy.url().should('include', `/me`);
}

describe('Account spec', () => {
  it('On the account page it should return to the session page when the back arrow is clicked', () => {
    logToAccountPage(sessionDetail);
    cy.contains('mat-card-title ', 'User information').should('be.visible');
    cy.contains('mat-card-title button mat-icon', 'arrow_back').click();
    cy.url().should('include', '/sessions');
  });

  it('Should show the user information', () => {
    cy.intercept('GET', `api/user/1`, conectedUser); // on utilise l'id 1 car lors du login (commands.ts) passe cette info dans body du retour de la requette post
    logToAccountPage(sessionDetail);

    cy.contains('p', 'Name: toto TOTO').should('be.visible');

    cy.contains('p', 'Email: toto3testcypress@toto.com').should('be.visible');

    cy.contains('p', 'You are admin').should('be.visible');

    cy.contains('p', 'Create at:').should('contain.text', 'October 14, 2025');

    cy.contains('p', 'Last update:').should('contain.text', 'October 14, 2025');
  });
});
