import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const authServiceMock = {
    login: jest.fn(),
  };

  const sessionServiceMock = {
    logIn: jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        SessionService,
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: AuthService, useValue: authServiceMock },
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    component.form.reset({
      email: '',
      password: '',
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit the form and navigate to the session page', () => {
    const loginRequest = {
      email: 'yoga@studio.com',
      password: '1234',
    };
    const mockedSessionInformation: SessionInformation = {
      token: 'Faux token',
      type: 'post',
      id: 1,
      username: 'yoga@studio.com',
      firstName: 'yoga',
      lastName: 'studio',
      admin: true,
    };

    authServiceMock.login.mockImplementation(() =>
      of(mockedSessionInformation)
    );

    component.form.setValue(loginRequest);

    const router = TestBed.inject(Router);
    const routerSpy = jest.spyOn(router, 'navigate');

    component.submit();

    expect(authServiceMock.login).toHaveBeenCalledWith(loginRequest);

    expect(sessionServiceMock.logIn).toHaveBeenCalledWith(
      mockedSessionInformation
    );

    expect(routerSpy).toHaveBeenCalledWith(['/sessions']);

    expect(component.onError).toBe(false);
  });

  it('should throw an error if login fail', () => {
    const loginRequest = {
      email: 'yoga@studio.com',
      password: 'exemple de mauvais mot de passe',
    };

    authServiceMock.login.mockImplementation(() =>
      throwError(() => new Error('Unauthorized'))
    );

    component.form.setValue(loginRequest);

    component.submit();

    expect(authServiceMock.login).toHaveBeenCalledWith(loginRequest);
    expect(component.onError).toBe(true);
  });
});
