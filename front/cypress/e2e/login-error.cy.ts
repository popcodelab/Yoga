// Remove the import statement for 'cy' from the 'cypress' module
describe('Login error test', () => {
  it('should not log the user in and an error message should be displayed', () => {
    cy.visit('/login')

    // This command intercepts all POST HTTP requests going to the '/api/auth/login' URL and overrides the response
    // with a 404 status code and a body containing 'Not Found'.
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 404,
      body: 'Not Found',
    }).as('apiRequest');

    cy.get('mat-card-title').should('have.text', 'Login');

    // simulate user inputs in the email and password fields followed by pressing the Enter key twice.
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test1234"}{enter}{enter}`)

    cy.get('.error' ).should('be.visible').should('have.text', 'An error occurred');

    cy.get('mat-card-title').should('be.visible')

  })

});
