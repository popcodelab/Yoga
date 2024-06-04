import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import {SessionInformation} from "../interfaces/sessionInformation.interface";

describe('SessionService', () => {
  let service: SessionService;
  const sessionInformationMock : SessionInformation  = {
    token: 'myDummyToken',
    type: 'course',
    id: 1,
    username: 'john.doe@mail.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: false
  };;


  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);

  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have default isLogged as false', () => {
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(false);
    });
  });

  describe('logIn', () => {
    beforeEach(() => {
      service.logIn(sessionInformationMock)
    })
    it('should set session information',() => {
      expect(service.sessionInformation).toEqual(sessionInformationMock)
    })
    it('should set isLogged to true', () => {
      expect(service.isLogged).toBe(true)
    })
  })

  describe('logOut', () => {
    beforeEach(() => {
      service.logOut()
    })
    it('should set sessionInformation to undefined', () => {
      expect(service.sessionInformation).toBe(undefined)
    })
    it('should set isLogged to false', () => {
      expect(service.isLogged).toBe(false)
    })
  })

});
