import * as cypress from "cypress";
describe('Logout spec', () => {

  const user = {
    id: 1,
    email: 'email@test.com',
    firstName: 'Emma',
    lastName: 'Lee',
    password: 'pass!1234',
    admin: false,
    createdAt: '2024-06-05',
    updatedAt: '2024-06-05'
  };

  before(() => {
    // Given
    cy.visit('/login')
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 201,
      body: user,
    });

    cy.intercept('GET', '/api/session', []);
    // When
    cy.get('input[formControlName=email]').type(user.email);
    cy.get('input[formControlName=password]').type(user.password);
    cy.get('button[type=submit]').click();

  })

  it('should logout successfully', () => {
    //Then
    cy.get('.link').contains('Logout').click()
    cy.url().should('eq', Cypress.config().baseUrl)
    cy.get('.link').contains('Logout').should('not.exist')
    cy.url().should('include', '/');
    cy.get('span[routerLink="login"]').contains('Login');
    cy.get('span[routerLink="register"]').contains('Register');
  })
})
