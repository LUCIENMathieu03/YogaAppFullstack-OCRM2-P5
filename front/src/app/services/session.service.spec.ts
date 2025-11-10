import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not be logged', (done) => {
    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBeFalsy();
      done();
    });
  });

  it('should be logged', (done) => {
    const mockUser: SessionInformation = {
      token: 'tokentokentokentokentokentokentoken',
      type: 'Bearer',
      id: 5,
      username: 'mathieu.lucien@studio.com',
      firstName: 'Mathieu',
      lastName: 'Lucien',
      admin: true,
    };

    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBeTruthy();
      done();
    });

    service.logIn(mockUser);
  });

  it('should be logged out', () => {
    const mockUser: SessionInformation = {
      token: 'tokentokentokentokentokentokentoken',
      type: 'Bearer',
      id: 5,
      username: 'mathieu.lucien@studio.com',
      firstName: 'Mathieu',
      lastName: 'Lucien',
      admin: true,
    };

    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBeFalsy();
    });

    service.logIn(mockUser);
    service.logOut();
  });

  it('should set sessionInformation when logIn', () => {
    const mockUser: SessionInformation = {
      token: 'tokentokentokentokentokentokentoken',
      type: 'Bearer',
      id: 5,
      username: 'mathieu.lucien@studio.com',
      firstName: 'Mathieu',
      lastName: 'Lucien',
      admin: true,
    };
    service.logIn(mockUser);
    expect(service.sessionInformation).toEqual(mockUser);
  });

  it('should clear sessionInformation when logOut', () => {
    const mockUser: SessionInformation = {
      token: 'tokentokentokentokentokentokentoken',
      type: 'Bearer',
      id: 5,
      username: 'mathieu.lucien@studio.com',
      firstName: 'Mathieu',
      lastName: 'Lucien',
      admin: true,
    };

    service.logIn(mockUser);
    service.logOut();
    expect(service.sessionInformation).toBeUndefined();
  });
});
