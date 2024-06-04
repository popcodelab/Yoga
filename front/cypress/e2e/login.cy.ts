describe('Login e2e test', () => {

  beforeEach(() => {
    cy.visit('/login')
  });

  it('Should log the user in successfully', () => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')
  });

  it('should not log the user in', () => {

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 404,
      body: 'Not Found',
    }).as('apiRequest');

    cy.get('mat-card-title').should('have.text', 'Login');

    cy.get('input[formControlName=email]').type("yoga")
    cy.get('input[formControlName=password]').type(`${"test"}{enter}{enter}`)

    cy.get('.error' ).should('be.visible').should('have.text', 'An error occurred');

    cy.get('mat-card-title').should('be.visible')

  });

});
