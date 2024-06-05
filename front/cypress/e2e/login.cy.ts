import * as cypress from "cypress";

describe('Login spec', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('Login successful', () => {
  // Given
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
      []);

    // When
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('button[type=submit]').click();

    // Then
    cy.url().should('include', '/sessions')
    cy.get('span').should('exist', 'Logout');
  });

  it('Should failed if using wrong credentials', () => {
    // Given
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {
        message: 'Bad credentials'
      },
    }).as('loginFailure');

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []);

    // When
    cy.get('input[formControlName=email]').type("yogi@studio.com");
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('button[type=submit]').click();

    cy.wait('@loginFailure');

    // Then
    cy.get('.error').should('be.visible').and('contain', 'An error occurred');

  });

  it('should disabled submit button if the email field is empty', () => {
    // Given
    cy.get('input[formControlName=email]').clear;
    cy.get('input[formControlName=password]').type("test!1234");

    // Then
    cy.get('input[formControlName=email]').should('have.class', 'ng-invalid');
    cy.get('button[type=submit]').should('be.disabled');
  });

  it('should disabled submit button if the password field is empty', () => {
    // Given
    cy.get('input[formControlName=password]').clear;
    cy.get('input[formControlName=email]').type("yoga@studio.com");

    // Then
    cy.get('input[formControlName=password]').should('have.class', 'ng-invalid');
    cy.get('button[type=submit]').should('be.disabled');
  });



});
