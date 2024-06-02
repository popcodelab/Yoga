import {ComponentFixture, TestBed} from "@angular/core/testing";
import {ListComponent} from "./list.component";
import {SessionService} from "../../../../services/session.service";
import {HttpClientTestingModule, HttpTestingController, TestRequest} from "@angular/common/http/testing";
import {SessionInformation} from "../../../../interfaces/sessionInformation.interface";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {Session} from "../../interfaces/session.interface";

import { expect } from '@jest/globals';
import {By} from "@angular/platform-browser";
import {DebugElement} from "@angular/core";

describe('ListComponent integration tests',() =>{
  let fixture: ComponentFixture<ListComponent>;
  let sessionService: SessionService;
  let httpMock: HttpTestingController;

  const API_URL = "api/session";

  const sessionInformation : SessionInformation =  {
    token :"WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ" ,
    type : "course",
    id: 1,
    username : "john.doe@mail.com",
    firstName : "John",
    lastName : "Doe",
    admin : true
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    sessionService = TestBed.inject(SessionService);
    sessionService.sessionInformation = sessionInformation
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges()

  });

  it('should retrieve and display all the sessions.', () => {
    const mockSessions: Session[] = [
      { name: 'Hatha Yoga', date: new Date(), description: 'Description Hatha yoga', teacher_id: 1, users: [4] },
      { name: 'Yin Yoga', date: new Date(), description: 'Description Yin yoga', teacher_id: 2, users: [10] }
    ];


    // Checks that a single http GET request has been done to the API endpoint
    const testRequest: TestRequest = httpMock.expectOne(API_URL);
    // The http verb should be GET
    expect(testRequest.request.method).toEqual('GET');
    // Flushes the mock response (mockSessions) to simulate a successful HTTP request.
    testRequest.flush(mockSessions);

    // Triggers Angular's change detection to update the view with the mockSessions data.
    fixture.detectChanges();

    // Queries the DOM for elements matching the specified CSS selector item mat-card-title.
    const sessionTitleElements: DebugElement[] = fixture.debugElement.queryAll(By.css('.item mat-card-title'));
    // Should have 2 sessions
    expect(sessionTitleElements.length).toEqual(2);
    // The session names should the ones defined in the mockSessions array
    expect(sessionTitleElements[0].nativeElement.textContent).toContain('Hatha Yoga')
    expect(sessionTitleElements[1].nativeElement.textContent).toContain('Yin Yoga')

  });

  test('if the edit button is not displayed when user is not an admin', () => {
    sessionService.sessionInformation = {
      token :"WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ" ,
      type : "course",
      id: 1,
      username : "john.doe@mail.com",
      firstName : "John",
      lastName : "Doe",
      admin : false
    };

    // Triggers Angular's change detection to update the view with the mockSessions data.
    fixture.detectChanges()

    // Queries the DOM for elements matching the specified CSS selector to get the edit button
    const editButton: DebugElement = fixture.debugElement.query(By.css('button[routerLink*="update"]'));
    // The button should not be present (editButton should be null)
    expect(editButton).toBeFalsy();
  })

});
