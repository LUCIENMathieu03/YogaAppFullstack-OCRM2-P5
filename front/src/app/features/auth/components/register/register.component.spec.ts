import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  let authServiceMock = {
    register: jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    component.form.reset({
      email: '',
      firstName: '',
      lastName: '',
      password: '',
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit the form and navigate to the login page', () => {
    const mockRegisterRequest = {
      email: 'newUser@studio.com',
      firstName: 'newUserFirstName',
      lastName: 'newUserLastName',
      password: 'newUserPassword',
    };

    component.form.setValue(mockRegisterRequest);

    authServiceMock.register.mockImplementation(() => {
      return of(undefined);
    });

    let router = TestBed.inject(Router);
    let routerSpy = jest.spyOn(router, 'navigate');

    component.submit();

    expect(authServiceMock.register).toHaveBeenCalledWith(mockRegisterRequest);
    expect(routerSpy).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBe(false);
  });

  it('should throw an error if something goes wrong', () => {
    const mockWrongRegisterRequest = {
      email: '',
      firstName: "all field are'nt filled",
      lastName: '',
      password: '',
    };

    component.form.setValue(mockWrongRegisterRequest);

    authServiceMock.register.mockReturnValue(
      throwError(() => new Error('Invalid form'))
    );

    component.submit();

    expect(component.onError).toBeTruthy();
  });
});
