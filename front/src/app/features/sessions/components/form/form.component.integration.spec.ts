import {HttpClientModule} from '@angular/common/http';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {expect} from '@jest/globals';
import {jest} from '@jest/globals';
import {SessionService} from 'src/app/services/session.service';
import {SessionApiService} from '../../services/session-api.service';

import {FormComponent} from './form.component';
import {HttpClientTestingModule, HttpTestingController, TestRequest} from '@angular/common/http/testing';
import {ActivatedRoute, Router} from '@angular/router';
import {of} from 'rxjs';
import {Session} from '../../interfaces/session.interface';

describe('FormComponent', () => {
  let formComponent: FormComponent;
  let componentFixture: ComponentFixture<FormComponent>;
  let httpMock: HttpTestingController;
  let sessionApiService: SessionApiService;
  let routerMock: Router;
  let matSnackBar: MatSnackBar;

  const sessionServiceMock = {
    sessionInformation: {
      admin: true
    }
  };

  routerMock = {
    navigate: jest.fn(),
    navigateByUrl: jest.fn(),
    url: ''
  } as any;

  const routeMock = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  };

  const matSnackBarMock = {
    open: jest.fn()
  };

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        NoopAnimationsModule, // This module disables animations, which is useful in tests to avoid animation-related issues.
        HttpClientTestingModule
      ],
      providers: [
        {provide: SessionService, useValue: sessionServiceMock},
        {provide: ActivatedRoute, useValue: routeMock},
        {provide: Router, useValue: routerMock},
        {provide: MatSnackBar, useValue: matSnackBarMock},
        SessionApiService
      ],
      declarations: [FormComponent]
    }).compileComponents();

    componentFixture = TestBed.createComponent(FormComponent);
    formComponent = componentFixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    sessionApiService = TestBed.inject(SessionApiService);
    matSnackBar = TestBed.inject(MatSnackBar);
    componentFixture.detectChanges();
  });

  it('should create a session and navigate to sessions page on successful creation', () => {
    const navigateSpy = jest.spyOn(routerMock, 'navigate');

    formComponent.sessionForm?.controls['name'].setValue('Hatha yoga course');
    formComponent.sessionForm?.controls['date'].setValue('2024-08-05');
    formComponent.sessionForm?.controls['teacher_id'].setValue('1');
    formComponent.sessionForm?.controls['description'].setValue('Hatha yoga is an ancient Indian practice ' +
      'aimed at improving both body and mind through postures.');

    formComponent.submit();

    const testRequest: TestRequest = httpMock.expectOne('api/session');
    expect(testRequest.request.method).toBe('POST');

    testRequest.flush({id: '1', name: 'Hatha yoga course'});

    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
    expect(matSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', {duration: 3000});
  });

  it('should update a session and navigate to sessions list page', () => {
    formComponent.onUpdate = true;
    (formComponent as any).id = '1';
    const navigateSpy = jest.spyOn(routerMock, 'navigate');

    formComponent.sessionForm?.controls['name'].setValue('Yin Yoga Session');
    formComponent.sessionForm?.controls['date'].setValue('2024-07-05');
    formComponent.sessionForm?.controls['teacher_id'].setValue('1');
    formComponent.sessionForm?.controls['description'].setValue('Yin Yoga is slow-paced style of yoga');

    formComponent.submit();

    const testRequest: TestRequest = httpMock.expectOne('api/session/1');
    expect(testRequest.request.method).toBe('PUT');

    testRequest.flush({id: '1', name: 'Yin Yoga Session'});

    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
    expect(matSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', {duration: 3000})
  });

  it('should navigate to the sessions list page if user is not an admin', () => {
    sessionServiceMock.sessionInformation.admin = false;
    formComponent.ngOnInit();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should initialize the form a session edition', () => {
    formComponent.onUpdate = true;
    (formComponent as any).id = '1';
    const session: Session = {
      id: 1,
      name: 'Hatha Yoga Session',
      date: new Date('2023-05-20'),
      teacher_id: 1,
      description: 'Hatha yoga is an ancient Indian practice aimed at improving both body and mind through postures.',
      users: []
    };

    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(session));

    formComponent.ngOnInit();
    expect(formComponent.sessionForm?.value).toEqual({
      "name": "",
      "date": "",
      "teacher_id": "",
      "description": ""
    });
  });

});
