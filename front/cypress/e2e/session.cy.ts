import * as cypress from "cypress";

describe('Session spec', () => {
  it('should log in the user', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'jean.dupond@mail.com',
        firstName: 'Jean',
        lastName: 'Dupond',
        admin: true
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []);

    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('button[type=submit]').click();

  });

  it('should create a new yoga session', () => {
    // Given
    cy.url().should('include', '/sessions');

    cy.intercept('GET', '/api/teacher', {
      body: [
        {
          id: 1,
          firstName: 'John',
          lastName: 'DOE'
        }
      ]
    });

    cy.intercept('POST', '/api/session', {
      body: {
        id: 2,
        name: 'Yin Yoga Session',
        date: '2024-10-01',
        description: 'Description Yin yoga',
        teacher_id: 1
      },
      statusCode: 201
    }).as('createdSession');

    cy.intercept('GET', '/api/session', {
      body: [
        {
          id: 1,
          name: 'Hatha Yoga Session',
          date: '2024-09-01',
          teacher_id: 1,
          description: 'Hatha yoga is an ancient Indian practice aimed at improving both body and mind through postures.',
          users: []
        },
        {
          id: 2,
          name: 'Yin Yoga Session',
          date: '2024-10-01',
          description: 'Description Yin yoga',
          teacher_id: 1,
          users: []
        }
      ]
    });

    cy.get('button[routerLink=create]').click();
    cy.get('input[formControlName=name]').type("Yin Yoga Session");
    cy.get('input[formControlName=date]').type("2024-10-01");
    cy.get('mat-select[formControlName=teacher_id]').click().get('mat-option').contains('John DOE').click();
    cy.get('textarea[formControlName=description]').type("Description Yin yoga");

    cy.get('button[type=submit]').click();

    cy.wait('@createdSession');
    cy.url().should('include', '/sessions');
    cy.get('.mat-snack-bar-container').should('contain', 'Session created !');

  });

  it('Update an existing yoga session', () => {
    cy.url().should('include', '/sessions');

    cy.intercept('GET', '/api/session/1', {
      body: {
        id: 1,
        name: 'Hatha Yoga Session',
        date: '2024-09-01',
        teacher_id: 1,
        description: 'Hatha yoga is an ancient Indian practice aimed at improving both body and mind through postures.',
        users: []
      }
    });

    cy.intercept('GET', '/api/teacher', {
      body: [
        {
          id: 1,
          firstName: 'John',
          lastName: 'DOE'
        }
      ]
    });

    cy.contains('Edit').click();

    cy.intercept('PUT', '/api/session/1', {
      body: {
        id: 1,
        name: 'Hatha Yoga Session - UPDATED',
        date: '2024-09-01',
        teacher_id: 1,
        description: 'Hatha yoga is an ancient Indian practice.',
        users: []
      },
    }).as('updatedSession');

    cy.url().should('include', '/sessions/update');

    cy.get('input[formControlName=name]').clear().type("Hatha Yoga Session - UPADTED");
    cy.get('input[formControlName=date]').clear().type("2024-09-15");
    cy.get('textarea[formControlName=description]').clear().type("Hatha yoga is an ancient + " +
      "Indian practice. - UPDATED");

    cy.intercept('GET', '/api/session', {
      body: [
        {
          id: 1,
          name: 'UpdatedSession',
          date: '2024-02-06T00:00:00.000+00:00',
          description: 'Updated session description',
          teacher_id: 1,
          users: []
        }
      ]
    });

    cy.get('button[type=submit]').click();
    cy.contains('Sessions').click();

    cy.get('.mat-snack-bar-container').should('contain', 'Session updated !');
  });

  it('Delete a yoga session', () => {
    cy.url().should('include', '/sessions');


    cy.intercept('GET', '/api/session/1', {
      body: {
        id: 1,
        name: 'Hatha Yoga Session - UPDATED',
        date: '2024-09-01',
        teacher_id: 1,
        description: 'Hatha yoga is an ancient Indian practice.',
        users: []
      }
    });
    //cy.contains('Hatha Yoga Session - UPDATED').should('exist');

    cy.contains('Detail').click();

    cy.intercept('DELETE', '/api/session/1', {
      status: 200
    });

    cy.intercept('GET', '/api/session', {
      body: []
    });

    cy.contains('Hatha Yoga Session - Updated').should('exist');
    cy.contains('Delete').click();

    cy.contains('Hatha Yoga Session - Updated').should('not.exist');

    cy.url().should('include', '/sessions');
    cy.get('.mat-snack-bar-container').should('contain', 'Session deleted !');
  });
});
