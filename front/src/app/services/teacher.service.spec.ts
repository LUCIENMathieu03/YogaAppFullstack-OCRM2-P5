import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;
  const pathService = 'api/teacher';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return a tab with all teacher', () => {
    const teacherTabMock = [
      {
        id: 1,
        lastName: 'DELAHAYE',
        firstName: 'Margot',
        createdAt: '2025-08-27T13:35:37',
        updatedAt: '2025-08-27T13:35:37',
      },
      {
        id: 2,
        lastName: 'THIERCELIN',
        firstName: 'Hélène',
        createdAt: '2025-08-27T13:35:37',
        updatedAt: '2025-08-27T13:35:37',
      },
    ];

    service.all().subscribe((teachers) => {
      expect(teachers).toEqual(teacherTabMock);
    });

    const req = httpMock.expectOne(`${pathService}`);
    expect(req.request.method).toBe('GET');

    req.flush(teacherTabMock);
  });

  it("should get teacher's details by id", () => {
    const teacher2 = {
      id: 2,
      lastName: 'THIERCELIN',
      firstName: 'Hélène',
      createdAt: '2025-08-27T13:35:37',
      updatedAt: '2025-08-27T13:35:37',
    };

    service.detail('2').subscribe((teacher) => {
      expect(teacher).toEqual(teacher2);
    });

    const req = httpMock.expectOne(`${pathService}/2`);

    expect(req.request.method).toBe('GET');

    req.flush(teacher2);
  });

  it("should not get teacher's details", () => {
    service.detail('4').subscribe((teacher) => {
      expect(teacher).toBeNull();
    });

    const req = httpMock.expectOne(`${pathService}/4`);

    expect(req.request.method).toBe('GET');

    req.flush({}, { status: 404, statusText: 'Not found' });
  });
});
