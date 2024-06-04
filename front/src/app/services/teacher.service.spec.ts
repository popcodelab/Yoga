import {HttpClient, HttpClientModule} from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { jest } from '@jest/globals';

import { TeacherService } from './teacher.service';
import {of} from "rxjs";

describe('TeacherService', () => {
  let service: TeacherService;
  let httpClientMock = {
    get : jest.fn()
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
    service = TestBed.inject(TeacherService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get teacher details by id', () => {
    httpClientMock.get.mockReturnValue(of(null));
    service.detail('1').subscribe(teacher => {
      expect(teacher).toEqual(null);
    });
    expect(httpClientMock.get).toHaveBeenCalledWith('api/teacher/1');
  })
});
