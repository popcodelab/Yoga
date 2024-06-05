import * as cypress from "cypress";
describe('Logout spec', () => {

  const USER = {
    id: 1,
    email: 'john.doe@mail.com',
    firstName: 'John',
    lastName: 'Doe',
    password: 'test!1234',
    admin: false,
    createdAt: '2024-06-05',
    updatedAt: '2024-06-05'
  };

  before(() => {
    // Given
    cy.visit('/login')
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 201,
      body: USER,
    });

    cy.intercept('GET', '/api/session', []);
    // When
    cy.get('input[formControlName=email]').type(USER.email);
    cy.get('input[formControlName=password]').type(USER.password);
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
