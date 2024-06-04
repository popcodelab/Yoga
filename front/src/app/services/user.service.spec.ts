import {HttpClient, HttpClientModule} from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { jest } from '@jest/globals';
import { expect } from '@jest/globals';

import { UserService } from './user.service';
import {TeacherService} from "./teacher.service";
import {of} from "rxjs";

describe('UserService', () => {
  let service: UserService;
  let httpClientMock = {
    get: jest.fn(),
    delete: jest.fn()
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ],
      providers : [
        {provide : HttpClient, useValue : httpClientMock}
      ]
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user by id', () => {
    httpClientMock.get.mockReturnValue(of(null))
    service.getById('1').subscribe(user => {
      expect(user).toEqual(null)
    });
    expect(httpClientMock.get).toHaveBeenCalledWith( "api/user/1")
  });

  it('should delete user by id', () => {
    httpClientMock.delete.mockReturnValue(of("deleted"))
    service.delete("1").subscribe(response => {
      expect(response).toEqual("deleted")
    });
    expect(httpClientMock.delete).toHaveBeenCalledWith( "api/user/1")
  });

});
