describe('Sessions page spec', () => {
  it('should not be accesible when not connected', () => {
    cy.visit('/sessions');
    cy.url().should('include', '/login');
  });

  it('should display base home page elements', () => {
    cy.login([]);

    //header
    cy.contains('Yoga app').should('exist');
    cy.contains('Sessions').should('exist');
    cy.contains('Logout').should('exist');

    //body
    cy.contains('Rentals available').should('exist');
    cy.contains('button', 'Create').should('exist');
  });

  it('Should display available session', () => {
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
    ]);

    cy.get('mat-card.item').should('have.length.at.least', 1);

    // Vérifie que la card a le bon titre et la bonne date
    cy.get('mat-card.item').within(() => {
      cy.get('mat-card-title').should('contain.text', 'Première session'); // a supprimer car verification inutile
      cy.get('mat-card-subtitle').should('contain.text', '30');
      cy.get('mat-card-content p').should(
        'contain.text',
        'Premiere création de session'
      );
      cy.contains('button', 'Detail').should('exist');
      cy.contains('button', 'Edit').should('exist');
    });
  });
});
