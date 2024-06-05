import * as cypress from "cypress";
describe('Not Found Page', () => {
  it('should display the not found page for any unknown route', () => {
    // Given
    cy.visit('/wrong-url');
    // Then
    cy.url().should('include', '/404');
    cy.contains('h1', 'Page not found !');
  });
});
