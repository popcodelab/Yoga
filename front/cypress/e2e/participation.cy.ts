import * as cypress from "cypress";

describe('Participation e2e tests', () => {

  const SESSION = {
    id: 1,
    name: 'Hatha Yoga Session',
    date: '2024-09-01',
    teacher_id: 1,
    description: 'Hatha yoga is an ancient Indian practice aimed at improving both body and mind through postures.',
    users: [1,2,3,4],
    createdAt: '2024-06-05',
    updatedAt: '2024-06-05',
  }

  const SESSION_MINUS_ONE_ATTENDEE = {
    id: 1,
    name: 'Hatha Yoga Session',
    date: '2024-09-01',
    teacher_id: 1,
    description: 'Hatha yoga is an ancient Indian practice aimed at improving both body and mind through postures.',
    users: [2,3,4],
    createdAt: '2024-06-05',
    updatedAt: '2024-06-05',
  }

  const TEACHER = {
    id: 1,
    firstName: 'John',
    lastName: 'DOE'
  }

  it('Should log a standard user in', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'jean.dupond@mail.com',
        admin: false
      },
    })

    cy.intercept('GET', '/api/session', [SESSION]);

    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234");
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/sessions')
  })

  it('Should not involve in the Hatha Yoga Session', () => {
    cy.intercept('GET', '/api/session/1', SESSION)
    cy.intercept('GET', '/api/teacher/1', TEACHER)

    cy.intercept('DELETE', '/api/session/1/participate/1', {})

    cy.contains('Detail').first().click()

    cy.url().should('include', '/sessions/detail/1')
    cy.should('not.contain', 'Delete')

    cy.intercept('GET', '/api/session/1', SESSION_MINUS_ONE_ATTENDEE);

    cy.contains('Do not participate').click()
    cy.contains('3 attendees');

    cy.contains('Participate')
  })

  it('Should participate for the Hatha Yoga Session', () => {

    cy.intercept('POST', '/api/session/1/participate/1', {})

    cy.intercept('GET', '/api/session/1', SESSION);
    cy.intercept('GET', '/api/teacher/1', TEACHER);

    cy.contains('Participate').click();
    cy.contains('4 attendees');
    cy.contains('Do not participate');
  })

});
