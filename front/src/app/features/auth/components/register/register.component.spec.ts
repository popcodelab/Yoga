import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {of, throwError} from "rxjs";

import { jest } from '@jest/globals';

describe('RegisterComponent', () => {
  let registerComponent: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  // Mocks

  let mockAuthService =  {
    register: jest.fn()
  }

  let mockRoute = {
    navigate : jest.fn()
  }



  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService},
        { provide : Router, useValue : mockRoute}
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    registerComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(registerComponent).toBeTruthy();
  });

  describe('submit', () => {
    it('should register a new user and then navigate to login page', () => {
      mockAuthService.register.mockReturnValue(of(fixture))

      registerComponent.submit()

      expect(mockRoute.navigate).toHaveBeenCalledWith(['/login'])
      expect(registerComponent.onError).toBe(false)
    })

    test('if it display an error message', () => {
      const ERROR_MESSAGE='An error occurred';
      mockAuthService.register.mockImplementation(() => {
        return throwError(ERROR_MESSAGE);
      })

      registerComponent.submit()

      expect(registerComponent.onError).toBe(true)
    })

    // Errors
    it('should display an error message when an error occurs', () => {
      registerComponent.onError = true;
      fixture.detectChanges();
      const formElement: HTMLElement = fixture.nativeElement;
      const errorMessage: Element | null = formElement.querySelector('span.error');
      expect(errorMessage).toBeTruthy();
      expect(errorMessage!.textContent).toContain('An error occurred');
    });

  })

});
