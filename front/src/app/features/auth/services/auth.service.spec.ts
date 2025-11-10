import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { AuthService } from './auth.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

describe('AuthService', () => {
  let httpMock: HttpTestingController;
  let service: AuthService;
  const pathService = 'api/auth';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(AuthService);
  });

  it('Should register a user with a valid register request', () => {
    const registerRequest = {
      lastName: 'toto',
      firstName: 'toto',
      email: 'toto3@toto.com',
      password: 'test!1234',
    };

    const responseMessage = {
      message: 'User registered successfully!',
    };

    service.register(registerRequest).subscribe((response) => {
      expect(response).toEqual(responseMessage);
    });

    const req = httpMock.expectOne(`${pathService}/register`);

    expect(req.request.method).toBe('POST');

    req.flush(responseMessage);
  });

  it('Should not register a user with a invalid register request', () => {
    const registerRequest = {
      lastName: 'toto',
      firstName: '',
      email: '',
      password: '',
    };

    service.register(registerRequest).subscribe({
      next: () => fail('expected an error'),
      error: (response) => {
        expect(response.status).toBe(400);
        expect(response.error.message).toBe('Bad request');
      },
    });

    const req = httpMock.expectOne(`${pathService}/register`);

    expect(req.request.method).toBe('POST');

    req.flush('', {
      status: 400,
      statusText: 'Bad Request',
    });
  });

  it('Should log a user with valid information', () => {
    const login = {
      email: 'yoga@studio.com',
      password: 'UserPassword',
    };

    const requestResponse: SessionInformation = {
      token:
        'eyJhbGciOiJIUzUxMiJ9.token.9dEtbV3M4lavPxnllWiwYLoHWMDd8sDvwKmAjEATigDpG3tsIorngKdvLfsDxYUk5fwfq64fp7ad4wKFKNzeCQ',
      type: 'Bearer',
      id: 1,
      username: 'yoga@studio.com',
      firstName: 'Admin',
      lastName: 'Admin',
      admin: true,
    };

    service.login(login).subscribe((response) => {
      expect(response).toBe(requestResponse);
    });

    const req = httpMock.expectOne(`${pathService}/login`);

    expect(req.request.method).toBe('POST');

    req.flush(requestResponse);
  });

  it('Should not log a user with invalid information', () => {
    const login = {
      email: 'yoga@studio.com',
      password: '',
    };

    const responseMessage = {
      message: 'Bad request!',
    };

    service.login(login).subscribe({
      next: () => fail('expected an error'),
      error: (response) => {
        expect(response.status).toBe(400);
        expect(response).toBe(responseMessage);
      },
    });

    const req = httpMock.expectOne(`${pathService}/login`);

    expect(req.request.method).toBe('POST');

    req.flush('', {
      status: 400,
      statusText: 'Bad Request',
    });
  });
});
