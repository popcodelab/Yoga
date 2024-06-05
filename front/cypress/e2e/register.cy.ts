import * as cypress from "cypress";
describe('Register spec', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('Should register a new user successfully', () => {
    // Given
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: {
        "message": "User registered successfully!"
      }
    }).as('register');
    cy.intercept(
      {
        method: 'GET',
        url: '/api/auth/login',
      },
      []).as('login')

    //When
    cy.get('input[formControlName=firstName]').type("John");
    cy.get('input[formControlName=lastName]').type("Doe");
    cy.get('input[formControlName=email]').type("john.doe@mail.com");
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('button[type=submit]').click();

    // Then
    cy.url().should('include', '/login');
  });

  it('Should display an error message if register with existing email', () => {
    // Given
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: {
        message: 'Error: Email is already taken!'
      },
    }).as('register');

    //When
    cy.get('input[formControlName=firstName]').type("John");
    cy.get('input[formControlName=lastName]').type("Doe");
    cy.get('input[formControlName=email]').type("john.doe@mail.com");
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('button[type=submit]').click();

    cy.wait('@register');

    // Then
    cy.get('.error').should('be.visible').and('contain', 'An error occurred');
  });

  it('should disabled submit button if the email form field is empty', () => {
    // When
    cy.get('input[formControlName=firstName]').type("John");
    cy.get('input[formControlName=lastName]').type("Doe");
    cy.get('input[formControlName=email]').clear();
    cy.get('input[formControlName=password]').type("test!1234");

    // Then
    cy.get('input[formControlName=email]').should('have.class', 'ng-invalid')
    cy.get('button[type=submit]').should('be.disabled')
  })

  it('should disabled submit button if the email format is invalid', () => {
    // When
    cy.get('input[formControlName=firstName]').type("John");
    cy.get('input[formControlName=lastName]').type("Doe");
    cy.get('input[formControlName=email]').type("john.doe_mail.com");
    cy.get('input[formControlName=password]').type("test!1234");

    // Then
    cy.get('input[formControlName=email]').should('have.class', 'ng-invalid')
    cy.get('button[type=submit]').should('be.disabled')
  })

  it('should disabled submit button if the firstname form field is empty', () => {
    // When
    cy.get('input[formControlName=firstName]').clear();
    cy.get('input[formControlName=lastName]').type("Doe");
    cy.get('input[formControlName=email]').type("john.doe@mail.com");
    cy.get('input[formControlName=password]').type("test!1234");

    // Then
    cy.get('input[formControlName=firstName]').should('have.class', 'ng-invalid')
    cy.get('button[type=submit]').should('be.disabled')
  })

  it('should disabled submit button if the lastname form field is empty', () => {
    // When
    cy.get('input[formControlName=firstName]').type("John");
    cy.get('input[formControlName=lastName]').clear();
    cy.get('input[formControlName=email]').type("john.doe@mail.com");
    cy.get('input[formControlName=password]').type("test!1234");

    // Then
    cy.get('input[formControlName=lastName]').should('have.class', 'ng-invalid')
    cy.get('button[type=submit]').should('be.disabled')
  })

  it('should disabled submit button if the password form field is empty', () => {
    // When
    cy.get('input[formControlName=firstName]').type("John");
    cy.get('input[formControlName=lastName]').type("Doe");
    cy.get('input[formControlName=email]').type("john.doe@mail.com");
    cy.get('input[formControlName=password]').clear();

    // Then
    cy.get('input[formControlName=password]').should('have.class', 'ng-invalid')
    cy.get('button[type=submit]').should('be.disabled')
  })

});
