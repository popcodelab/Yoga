import {HttpClientModule} from '@angular/common/http';
import {TestBed} from '@angular/core/testing';
import {expect} from '@jest/globals';

import {SessionApiService} from './session-api.service';
import {HttpClientTestingModule, HttpTestingController, TestRequest} from "@angular/common/http/testing";
import {SessionService} from "../../../services/session.service";
import {Session} from "../interfaces/session.interface";

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const API_URL = 'api/session';

  const fakeSessions: Session[] = [
    {id: 1 , name: 'Session 1', description: 'Description 1', date: new Date('2024-06-04'), teacher_id: 1, users: []},
    {id: 2 , name: 'Session 2', description: 'Description 2', date: new Date('2024-06-05'), teacher_id: 2, users: [1, 2]}
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  // ensures that all HTTP requests defined in your tests have been made and that there are no outstanding requests
  // that need to be flushed
  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all sessions', () => {
    service.all().subscribe(sessions => {
      expect(sessions.length).toBe(2);
      expect(sessions).toEqual(fakeSessions);
    });

    const testRequest: TestRequest = httpMock.expectOne(API_URL);
    expect(testRequest.request.method).toBe('GET');
    testRequest.flush(fakeSessions);
  });

  it('should retrieve session details', () => {
    const dummySession: Session = fakeSessions[0];

    service.detail('1').subscribe(session => {
      expect(session).toEqual(dummySession);
    });

    const testRequest: TestRequest = httpMock.expectOne(`${API_URL}/1`);
    expect(testRequest.request.method).toBe('GET');
    testRequest.flush(dummySession);
  });

  it('should create a new session', () => {
    const newSession: Session = {id: 3 , name: 'Updated Session 3', description: 'Description session to be updated', date: new Date('2024-06-04'), teacher_id: 1, users: []};

    service.create(newSession).subscribe(session => {
      expect(session).toEqual(newSession);
    });

    const testRequest: TestRequest = httpMock.expectOne(`${API_URL}`);
    expect(testRequest.request.method).toBe('POST');
    expect(testRequest.request.body).toEqual(newSession);
    testRequest.flush(newSession);
  });

  it('should update a session', () => {
    const updatedSession: Session = {id: 3 , name: 'Session 3', description: 'Description session to create', date: new Date('2024-06-04'), teacher_id: 1, users: []};

    service.update('1', updatedSession).subscribe(session => {
      expect(session).toEqual(updatedSession);
    });

    const testRequest: TestRequest = httpMock.expectOne(`${API_URL}/1`);
    expect(testRequest.request.method).toBe('PUT');
    expect(testRequest.request.body).toEqual(updatedSession);
    testRequest.flush(updatedSession);
  });

  it('should delete a session', () => {
    service.delete('1').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const testRequest: TestRequest = httpMock.expectOne(`${API_URL}/1`);
    expect(testRequest.request.method).toBe('DELETE');
    testRequest.flush(null);
  });

  test('if user participates in a session', () => {
    service.participate('1', 'user1').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const testRequest: TestRequest = httpMock.expectOne(`${API_URL}/1/participate/user1`);
    expect(testRequest.request.method).toBe('POST');
    testRequest.flush(null);
  });

  test('if a user unparticipate from a session', () => {
    service.unParticipate('1', 'user1').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const testRequest: TestRequest = httpMock.expectOne(`${API_URL}/1/participate/user1`);
    expect(testRequest.request.method).toBe('DELETE');
    testRequest.flush(null);
  });

});
