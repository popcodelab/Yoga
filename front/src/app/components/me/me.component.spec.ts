import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';

import { jest } from '@jest/globals';
import { expect } from '@jest/globals';


import { MeComponent } from './me.component';
import {of} from "rxjs";
import {Router} from "@angular/router";
import {UserService} from "../../services/user.service";

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockUser = {
    id: 1,
    email: 'user@mail.com',
    lastName: 'userLN',
    firstName: 'userFN',
    admin: false,
    password: 'user0001',
    createdAt: new Date()
  };

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    },
    logOut : jest.fn()
  }


  const mockUserService = {
    getById: jest.fn().mockReturnValue(of(mockUser)),
    delete : jest.fn().mockReturnValue(of({}))
  };

  const mockMatSnackBar = {
    open : jest.fn()
  }

  const mockRouter = {
    navigate : jest.fn()
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService},
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: Router, useValue: mockRouter }
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('NgOnInit', () => {
    test('if it calls userService.getById with session ID and update user', () => {
      component.ngOnInit()
      expect(component.user).toEqual(mockUser)
      expect(mockUserService.getById).toHaveBeenCalledWith("1")
    });
  });

  // Test of the back navigation
  describe('back', () => {
    it('should go back', () => {
      const historySpy: any = jest.spyOn(window.history, 'back')
      component.back()
      expect(historySpy).toHaveBeenCalled();
    });
  });

  // Test of the full user deletion process
  describe('delete', () => {

    beforeEach(() => {
      component.delete();
    });

    it('should open a snackbar with deletion message', () => {
      expect(mockMatSnackBar.open).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 });
    });

    it('should call sessionService logOut method ', () => {
      expect(mockSessionService.logOut).toHaveBeenCalled();
    });

    it('should navigate to home page', () => {
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
    });
  });

});
