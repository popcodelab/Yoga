import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { SessionService } from './services/session.service';
import { AppComponent } from './app.component';
import {Observable, of} from 'rxjs';
import { expect } from '@jest/globals';
import { jest } from '@jest/globals';
import {HttpClientModule} from "@angular/common/http";

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  const routerMock = { navigate: jest.fn() };
  const mockIsLogged: Observable<boolean> = of(true); // Mocked observable
  const sessionServiceMock = {
    $isLogged: jest.fn().mockReturnValue(mockIsLogged),
    logOut: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientModule
      ],
      declarations: [ AppComponent ],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('logout should call sessionService logout and router navigate', () => {
    component.logout();

    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });

  it('$isLogged should call sessionService $isLogged', () => {
    const result: Observable<boolean> = component.$isLogged();

    expect(sessionServiceMock.$isLogged).toHaveBeenCalled();
    expect(result).toBe(mockIsLogged);
  });
});
