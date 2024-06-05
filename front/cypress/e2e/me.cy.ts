import * as cypress from "cypress";
describe('Me spec', () => {
  beforeEach(() => {
    // Given
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        email: 'user@mail.com',
        admin: false
      }
    }).as('login');

    // When
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('button[type=submit]').click();

    // Then
    cy.url().should('include', '/sessions');
  });

  it('should display user details', () => {
    // Given
    cy.intercept('GET', '/api/user/1', {
      body: {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@mail.com',
        admin: false,
        createdAt: '2024-06-05',
        updatedAt: '2024-06-05'
      }
    }).as('getUserInfo');

    // When
    cy.get('span.link').contains('Account').click();
    cy.wait('@getUserInfo');

    // Then
    cy.get('p').contains('Name: John DOE');
    cy.get('p').contains('Email: john.doe@mail.com');
    cy.get('p').contains('Create at: June 5, 2024');
    cy.get('p').contains('Last update: June 5, 2024');
  });

  it('should delete user account successfully', () => {
    // Given
    cy.intercept('GET', '/api/user/1', {
      body: {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@mail.com',
        admin: false,
        createdAt: '2024-06-05',
        updatedAt: '2024-06-05'
      }
    }).as('getUserInfo');

    cy.intercept('DELETE', '/api/user/1', {
      statusCode: 200,
      body: {}
    }).as('deleteUser');
    // When
    cy.get('span.link').contains('Account').click();
    cy.wait('@getUserInfo');

    cy.get('button').contains('Detail').click();
    cy.wait('@deleteUser');

    // Then
    cy.url().should('include', '/');
    cy.get('.mat-snack-bar-container').should('contain', 'Your account has been deleted !');
  });
});
