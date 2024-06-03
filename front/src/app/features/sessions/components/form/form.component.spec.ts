import {HttpClientModule} from '@angular/common/http';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {expect} from '@jest/globals';
import {SessionService} from 'src/app/services/session.service';
import {SessionApiService} from '../../services/session-api.service';

import {FormComponent} from './form.component';
import {Session} from "../../interfaces/session.interface";

import {jest} from '@jest/globals';
import {of} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {DebugElement} from "@angular/core";
import {By} from "@angular/platform-browser";

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const mockSession: Session = {
    id: 1,
    name: "Hatha yoga course'",
    description: "Session description",
    date: new Date("2024-07-01"),
    teacher_id: 23,
    users: [1, 2, 5],
  };

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  }

  const mockRouter = {
    navigate: jest.fn(),
    url: "/update"
  }

  const mockSessionApiService = {
    detail: jest.fn().mockReturnValue(of(mockSession)),
    create: jest.fn().mockReturnValue(of(mockSession)),
    update: jest.fn().mockReturnValue(of(mockSession))
  }

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue("1")
      }
    }
  }

  const mockMatSnackBar = {
    open: jest.fn()
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        {provide: SessionService, useValue: mockSessionService},
        {provide: SessionApiService, useValue: mockSessionApiService},
        {provide: ActivatedRoute, useValue: mockActivatedRoute},
        {provide: Router, useValue: mockRouter},
        {provide: MatSnackBar, useValue: mockMatSnackBar}
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to sessions list if user is not an admin', () => {
    mockSessionService.sessionInformation!.admin = false;
    const navigate = jest.spyOn(mockRouter, 'navigate');
    component.ngOnInit();
    expect(navigate).toHaveBeenCalledWith(['/sessions']);
  });

  describe('Session edition mode', () => {
    beforeEach(() => {
      mockRouter.url = '/update';
      component.ngOnInit();
      fixture.detectChanges();
    });

    it('should fetch session details and initialize the form with them', () => {

      component.ngOnInit()
      expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
      expect(component.sessionForm).toBeDefined();
    });
  });

  describe('submit', () => {
    beforeEach(() => {
      component.ngOnInit()
      fixture.detectChanges()
    })
    test('if create method is called if onUpdate property is false, display a message in the SnackBar ' +
      'and navigate to sessions list ', () => {
      const session = component.sessionForm?.value
      component.onUpdate = false;
      component.submit();

      expect(mockSessionApiService.create).toHaveBeenCalledWith(session)
      expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', {duration: 3000});
      expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
    });

    beforeEach(() => {
      mockRouter.url = "/update"
    })
    test('if update method is called if onUpdate property is true, display a message in the SnackBar ' +
      'and navigate to sessions list ', () => {
      const session = component.sessionForm?.value
      component.onUpdate = true;
      component.submit();

      expect(mockSessionApiService.update).toHaveBeenCalledWith('1', session);
      expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', {duration: 3000});
      expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });


  describe('form fields', () => {
    beforeEach(() => {
      component.ngOnInit()
      fixture.detectChanges()
    })
    test('if all the fields are presents on the form to create or edit a session', () => {
      const nativeEl = fixture.nativeElement;

      const form = nativeEl.querySelector('form');
      expect(form).toBeTruthy();

      const nameInput = form.querySelector('input[formcontrolname=name]');
      expect(nameInput).toBeTruthy();

      const dateInput = form.querySelector('input[formcontrolname=date]');
      expect(dateInput).toBeTruthy();

      const teacherSelect = form.querySelector('mat-select[formcontrolname=teacher_id]');
      expect(teacherSelect).toBeTruthy();

      const descriptionTextarea = form.querySelector('textarea[formcontrolname=description]');
      expect(descriptionTextarea).toBeTruthy();
    });

    it('should disable the submit button if the form is invalid', () => {
      const nativeEl = fixture.nativeElement;

      component.sessionForm?.controls['name'].setValue('');
      fixture.detectChanges();

      const submitButton: HTMLButtonElement = nativeEl.querySelector('button[type="submit"]') as HTMLButtonElement;
      expect(submitButton.disabled).toBeTruthy();
    });

    it('should have a valid form when all fields are filled', () => {
      component.sessionForm?.controls['name'].setValue('Hatha yoga course');
      component.sessionForm?.controls['description'].setValue('Hatha yoga is an ancient Indian practice ' +
        'aimed at improving both body and mind through postures.');
      component.sessionForm?.controls['date'].setValue('2024-08-05');
      component.sessionForm?.controls['teacher_id'].setValue('1');

      expect(component.sessionForm?.valid).toBeTruthy();
    });
  });

});
