import { AuthService } from './auth.service';
import {HttpClientTestingModule, HttpTestingController, TestRequest} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";

import { expect } from '@jest/globals';
import {LoginRequest} from "../interfaces/loginRequest.interface";
import {SessionInformation} from "../../../interfaces/sessionInformation.interface";

describe('AuthService', () => {
  // The HttpTestingController is used for testing applications that use the HttpClient module in Angular.
  // It provides an easy way to inspect and deliver mock responses to HTTP requests in your tests.
  let httpMock: HttpTestingController;
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    authService = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  describe('register',()=>{

    it('should register an user posting the form with the correct URL and data', () => {

      const regsisterRequest = {
        email: 'john.doe@mail.com',
        firstName: 'John',
        lastName: 'Doe',
        password: 'password'
      };

      authService.register(regsisterRequest).subscribe(
        response => {
          // In the context of your register method, the response is void, meaning that the observable doesn't
          // emit any value when the HTTP POST request completes successfully.
          // Therefore, when testing the register method, the expected response is indeed undefined because
          // the method is designed to return an Observable<void>.
          expect(response).toBeUndefined();
        });

      const testRequest: TestRequest = httpMock.expectOne('api/auth/register');

      expect(testRequest.request.method).toBe('POST');
      expect(testRequest.request.body).toEqual(regsisterRequest);

      testRequest.flush({}); // simulate server response - null
    });
  });

  describe('login',()=>{

    it('should login a user', () => {
      const loginRequest : LoginRequest  = {
        email: 'john.doe@mail.com',
        password: 'password'
      };

      const sessionInformation  = {
        token: 'fake-jwt-token',
        username: 'john.doe@mail.com',
      };

      authService.login(loginRequest).subscribe((response) => {
        expect(response).toBeTruthy();
        expect(response).toHaveProperty('username');
        expect(response).toHaveProperty('token');
        expect(response).toEqual(sessionInformation);

      });

      const testRequest: TestRequest = httpMock.expectOne('api/auth/login');

      expect(testRequest.request.method).toBe('POST');
      expect(testRequest.request.body).toEqual(loginRequest);

      testRequest.flush(sessionInformation); // simulate server response with session information
    });
  });

});
