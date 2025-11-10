import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  const pathService = 'api/user';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get a user by Id', () => {
    const mockUser: User = {
      admin: true,
      id: 1,
      firstName: 'Thomas',
      lastName: 'Mann',
      email: 'thomas@mountain.com',
      password: 'password',
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    service.getById('1').subscribe((user) => {
      // Vérifie que la réponse contient bien les données mockées
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne(`${pathService}/1`);

    expect(req.request.method).toBe('GET');

    req.flush(mockUser);
  });

  it('should delete a user by id and receive success response ', () => {
    service.delete('1').subscribe((response) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${pathService}/1`);

    expect(req.request.method).toBe('DELETE');

    req.flush({}, { status: 200, statusText: 'OK' });
  });

  it('should not delete a user and receive unsucces response ', () => {
    service.delete('53').subscribe((response) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${pathService}/53`);

    expect(req.request.method).toBe('DELETE');

    req.flush({}, { status: 404, statusText: 'Not found' });
  });
});
