import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { Session } from '../interfaces/session.interface';

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all sessions', () => {
    const mockSessions: Session[] = [
      {
        id: 1,
        name: 'session 1',
        date: new Date('2012-01-01T00:00:00.000+00:00'),
        teacher_id: 1,
        description: 'my description',
        users: [],
        createdAt: new Date('2025-11-11T00:11:06.301346'),
        updatedAt: new Date('2025-11-11T00:11:06.55567'),
      },
      {
        id: 2,
        name: 'session 2',
        date: new Date('2012-01-01T00:00:00.000+00:00'),
        teacher_id: 2,
        description: 'my description',
        users: [],
        createdAt: new Date('2025-11-11T00:11:06.301346'),
        updatedAt: new Date('2025-11-11T00:11:06.55567'),
      },
    ];

    service.all().subscribe((sessions) => {
      expect(sessions.length).toBe(2);
      expect(sessions).toEqual(mockSessions);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });

  it('should fetch session detail', () => {
    const session: Session = {
      id: 1,
      name: 'session 1',
      date: new Date('2012-01-01T00:00:00.000+00:00'),
      teacher_id: 1,
      description: 'my description',
      users: [],
      createdAt: new Date('2025-11-11T00:11:06.301346'),
      updatedAt: new Date('2025-11-11T00:11:06.55567'),
    };
    const id = '1';

    service.detail(id).subscribe((result) => {
      expect(result).toEqual(session);
    });

    const req = httpMock.expectOne(`api/session/${id}`);
    expect(req.request.method).toBe('GET');
    req.flush(session);
  });

  it('should create a session', () => {
    const session: Session = {
      id: 1,
      name: 'New session',
      date: new Date('2012-01-01T00:00:00.000+00:00'),
      teacher_id: 1,
      description: 'new description',
      users: [],
      createdAt: new Date('2025-11-11T00:11:06.301346'),
      updatedAt: new Date('2025-11-11T00:11:06.55567'),
    };

    service.create(session).subscribe((result) => {
      expect(result).toEqual(session);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(session);
    req.flush(session);
  });

  it('should update a session', () => {
    const id = '1';
    const session: Session = {
      name: 'session to update',
      date: new Date('2012-01-01T00:00:00.000+00:00'),
      teacher_id: 1,
      description: 'my description',
      users: [],
    };

    service.update(id, session).subscribe((result) => {
      expect(result).toEqual(session);
    });

    const req = httpMock.expectOne(`api/session/${id}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(session);
    req.flush(session);
  });

  it('should delete a session', () => {
    const id = '1';

    service.delete(id).subscribe((response) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`api/session/${id}`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should participate to a session', () => {
    const id = '1';
    const userId = 'user1';

    service.participate(id, userId).subscribe((response) => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne(`api/session/${id}/participate/${userId}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush(null);
  });

  it('should unParticipate from a session', () => {
    const id = '1';
    const userId = 'user1';

    service.unParticipate(id, userId).subscribe((response) => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne(`api/session/${id}/participate/${userId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
