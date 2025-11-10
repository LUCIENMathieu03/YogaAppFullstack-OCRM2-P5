import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from '../../../../services/session.service';
import { DetailComponent } from './detail.component';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { FlexLayoutModule } from '@angular/flex-layout';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import {
  BrowserAnimationsModule,
  NoopAnimationsModule,
} from '@angular/platform-browser/animations';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let service: SessionService;
  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
  };
  const mockSessionApiService = {
    delete: jest.fn(),
    detail: jest.fn(() => {
      return of({
        id: 1,
        name: 'Mock session',
        date: '2025-01-01T00:00:00.000Z',
        teacher_id: 2,
        description: 'Description mock',
        users: [1],
        createdAt: '2025-01-01T00:00:00.000Z',
        updatedAt: '2025-01-01T00:00:00.000Z',
      });
    }),
    participate: jest.fn(),
    unParticipate: jest.fn(),
  };
  const mockTeacherService = {
    detail: jest.fn(),
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        FlexLayoutModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
      ],
    }).compileComponents();
    service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should return to the precedent page', () => {
    const backSpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(backSpy).toHaveBeenCalled();
  });
  it('should delete a session, show snackbar, and navigate to the session page', () => {
    component.sessionId = '1';

    const router = TestBed.inject(Router);
    const routerSpy = jest
      .spyOn(router, 'navigate')
      .mockReturnValue(Promise.resolve(true));
    const snackBar = TestBed.inject(MatSnackBar);
    const snackBarSpy = jest.spyOn(snackBar, 'open');
    mockSessionApiService.delete.mockReturnValue(of(null));

    component.delete();

    expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
    expect(snackBarSpy).toHaveBeenCalledWith('Session deleted !', 'Close', {
      duration: 3000,
    });
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should add a participation to a session', () => {
    component.sessionId = '1';
    mockSessionApiService.participate.mockImplementation(() => of(null));

    component.participate();

    expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', '1'); //sessionId userId
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(mockTeacherService.detail).toHaveBeenCalledWith('2');
  });

  it('should remove a participation to a session', () => {
    component.sessionId = '1';
    mockSessionApiService.unParticipate.mockImplementation(() => of(null));

    component.unParticipate();

    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(mockTeacherService.detail).toHaveBeenCalledWith('2');
  });
});
