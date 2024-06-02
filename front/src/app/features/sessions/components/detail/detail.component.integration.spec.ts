import {DetailComponent} from "./detail.component";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {HttpTestingController} from "@angular/common/http/testing";
import {Router} from "@angular/router";
import {MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import {SessionInformation} from "../../../../interfaces/sessionInformation.interface";
import {Session} from "../../interfaces/session.interface";
import {RouterTestingModule} from "@angular/router/testing";
import {HttpClientModule} from "@angular/common/http";
import {ReactiveFormsModule} from "@angular/forms";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {SessionService} from "../../../../services/session.service";
import {SessionApiService} from "../../services/session-api.service";
import {TeacherService} from "../../../../services/teacher.service";
import {By} from "@angular/platform-browser";

import { jest } from '@jest/globals';
import { expect } from '@jest/globals';
import {DebugElement} from "@angular/core";

describe('DetailsComponent integration tests', () => {
  let component : DetailComponent;
  let fixture : ComponentFixture<DetailComponent>;
  let sessionApiService: SessionApiService
  let teacherService: TeacherService;
  let httpMock: HttpTestingController;
  let router : Router;
  let matSnackBar : MatSnackBar;

  const sessionInformation: SessionInformation  = {
    token :'WK5T79u5mIzjIXXi2oI9Fglmgivv7RAJ7izyj9tUyQ' ,
    type:'course',
    id:1,
    username: 'john.doe@mail.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: true
  };

  const sessionServiceMock ={
    // Provides mock session information
    sessionInformation: sessionInformation,
    // Simulates a logged-in user
    isLogged: true,
  }

  const sessionMock: Session = {
    id: 1,
    name: "Hatha yoga course'",
    description: "Session description",
    date: new Date("2024-07-01"),
    teacher_id: 23,
    users: [1, 2, 5],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule,

      ],
      declarations: [DetailComponent],
      providers: [
        {provide: SessionService, useValue: sessionServiceMock}
      ],
    }).compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    component.session = sessionMock;
    fixture.detectChanges();
  });

  test('if the user is an admin it should have a delete button', () => {
    component.isAdmin = true;
    fixture.detectChanges();
    const buttons: DebugElement[] = fixture.debugElement.queryAll(By.css('button'));
    const deleteButton: DebugElement | undefined = buttons.find((button: DebugElement) =>
      button.nativeElement.textContent.includes('Delete')
    );
    expect(deleteButton).toBeTruthy();
  });

  test('if the user is not an admin it should not have a delete button', () => {
    component.isAdmin = false;
    fixture.detectChanges();
    const buttons: DebugElement[] = fixture.debugElement.queryAll(By.css('button'));
    const deleteButton: DebugElement | undefined = buttons.find((button: DebugElement) =>
      button.nativeElement.textContent.includes('Delete')
    );
    expect(deleteButton).toBeFalsy();
  });


  it('should call the participate method when the user click on Participate button', () => {
    component.isAdmin = false;
    component.isParticipate = false;
    fixture.detectChanges();

    const buttons: DebugElement[] = fixture.debugElement.queryAll(By.css('button'));
    const participateButton: DebugElement | undefined = buttons.find((button: DebugElement) =>
      button.nativeElement.textContent.includes('Participate')
    );
    expect(participateButton).toBeTruthy();
    const componentSpy = jest.spyOn(component, 'participate');
    participateButton!.nativeElement.click();

    expect(componentSpy).toHaveBeenCalled();
  });

  it('should call the unParticipate method when the user click on "Do not participate" button ', () => {
    component.isAdmin = false;
    component.isParticipate = true;
    fixture.detectChanges();

    const buttons: DebugElement[] = fixture.debugElement.queryAll(By.css('button'));
    const participateButton: DebugElement | undefined = buttons.find((button) =>
      button.nativeElement.textContent.includes('Do not participate')
    );

    expect(participateButton).toBeTruthy();

    const componentSpy = jest.spyOn(component, 'unParticipate');

    participateButton!.nativeElement.click();

    expect(componentSpy).toHaveBeenCalled();
  });

  });
