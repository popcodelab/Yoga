import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { jest } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import {SessionApiService} from "../../services/session-api.service";
import {ActivatedRoute, Router} from "@angular/router";
import {of} from "rxjs";
import {Session} from "../../interfaces/session.interface";
import {Teacher} from "../../../../interfaces/teacher.interface";
import {TeacherService} from "../../../../services/teacher.service";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionService: SessionService;

  const mockSession: Session = {
    id: 1,
    name: "Hatha yoga course'",
    description: "Session description",
    date: new Date("2024-07-01"),
    teacher_id: 23,
    users: [1, 2, 5],
  };

  const mockTeacher: Teacher = {
    id: 23,
    lastName: "John",
    firstName: "Doe",
    createdAt: new Date("2024-06-01"),
    updatedAt: new Date("2024-06-01")
  };

  const mockActivatedRoute: { snapshot: { paramMap: { get: () => string } } } = {
    snapshot: {
      paramMap: {
        get: () => '1'
      }
    }
  }

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  }

  const mockSessionApiService = {
    detail: jest.fn().mockReturnValue(of(mockSession)),
    delete: jest.fn().mockReturnValue(of(null)),
    participate: jest.fn().mockReturnValue(of(null)),
    unParticipate: jest.fn().mockReturnValue(of(null))
  }

  const mockMatSnackBar = {
    open: jest.fn()
  }

  const mockRouter = {
    navigate: jest.fn()
  }

  const mockTeacherService = {
    detail: jest.fn().mockReturnValue(of(mockTeacher))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule
      ],
      declarations: [DetailComponent],
      providers: [
        {provide: SessionService, useValue: mockSessionService},
        {provide: SessionApiService, useValue: mockSessionApiService},
        {provide: MatSnackBar, useValue: mockMatSnackBar},
        {provide: Router, useValue: mockRouter},
        {provide: ActivatedRoute, useValue: mockActivatedRoute},
        {provide: TeacherService, useValue: mockTeacherService}
      ],
    })
      .compileComponents();

    sessionService = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('delete', () => {
    beforeEach(() => {
      component.delete();
    });
    it('should delete the session ', () => {
      expect(mockSessionApiService.delete).toHaveBeenCalled()
    })
    it('should display a message within a snackbar', () => {
      expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', {duration: 3000});
    });
    it('should navigate to sessions list page', () => {
      expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('participate', () => {
    it('should call participate method', () => {
      component.participate();
      expect(mockSessionApiService.participate).toHaveBeenCalledWith(component.sessionId, component.userId);
    });
  });

  describe('participate', () => {
    it('should call participate method', () => {
      component.participate();
      expect(mockSessionApiService.participate).toHaveBeenCalledWith(component.sessionId, component.userId);
    });
  });

  describe('unParticipate', () => {
    it('should call unParticipate method and fetch the session', () => {
      component.unParticipate();

      expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith(component.sessionId, component.userId);
    });
  });

  describe('back', () => {
    it('should go back', () => {
      const historySpy = jest.spyOn(window.history, 'back')
      component.back()
      expect(historySpy).toHaveBeenCalled();
    });
  });
});

