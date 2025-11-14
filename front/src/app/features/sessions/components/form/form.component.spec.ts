import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import { ActivatedRoute, Router } from '@angular/router';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule,
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService,
      ],
      declarations: [FormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create the component and init form in create mode', () => {
    jest.spyOn(router, 'navigate');
    jest.spyOn(component as any, 'initForm');

    // Simuler URL sans update
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');

    component.ngOnInit();

    expect(component.onUpdate).toBe(false);
    expect(component['initForm']).toHaveBeenCalledWith();
    expect(component.sessionForm).toBeDefined();
  });

  it('should init form in update mode with session data', () => {
    jest.spyOn(router, 'navigate');
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/123');

    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue('123');

    const sessionApiService = TestBed.inject(SessionApiService);
    const mockSession = {
      id: '123',
      name: 'Test session',
      date: '2025-11-01',
      teacher_id: '1',
      description: 'Description',
    };

    // Mock réponse API detail
    jest.spyOn(sessionApiService, 'detail').mockReturnValue({
      subscribe: (fn: (session: any) => void) => fn(mockSession),
    } as any);

    component.ngOnInit();

    expect(component.onUpdate).toBe(true);
    expect(component.sessionForm?.value.name).toBe(mockSession.name);
    expect(component.sessionForm?.value.teacher_id).toBe(
      mockSession.teacher_id
    );
    expect(component.sessionForm?.value.description).toBe(
      mockSession.description
    );
    expect(component.sessionForm?.value.date).toBe(mockSession.date);
  });

  it('should navigate to sessions and show snackbar on submit in creation mode', () => {
    const sessionApiService = TestBed.inject(SessionApiService);
    const matSnackBar = TestBed.inject(MatSnackBar);
    const router = TestBed.inject(Router);

    jest.spyOn(sessionApiService, 'create').mockReturnValue({
      subscribe: (fn: any) => fn({}),
    } as any);
    jest
      .spyOn(router, 'navigate')
      .mockImplementation(() => Promise.resolve(true));
    jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);

    component.onUpdate = false;
    (component as any).initForm();
    component.sessionForm?.patchValue({
      name: 'Session 1',
      date: '2025-11-01',
      teacher_id: '1',
      description: 'Desc',
    });

    component.submit();

    expect(sessionApiService.create).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    expect(matSnackBar.open).toHaveBeenCalledWith(
      'Session created !',
      'Close',
      { duration: 3000 }
    );
  });

  it('should call update and navigate with snackbar on submit in update mode', () => {
    const sessionApiService = TestBed.inject(SessionApiService);
    const matSnackBar = TestBed.inject(MatSnackBar);
    const router = TestBed.inject(Router);

    jest.spyOn(sessionApiService, 'update').mockReturnValue({
      subscribe: (fn: any) => fn({}),
    } as any);
    jest.spyOn(router, 'navigate');
    jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);

    component.onUpdate = true;

    (component as any)['id'] = '123';
    (component as any).initForm();
    component.sessionForm?.patchValue({
      name: 'Session 1',
      date: '2025-11-01',
      teacher_id: '1',
      description: 'Desc',
    });

    component.submit();

    expect(sessionApiService.update).toHaveBeenCalledWith(
      '123',
      expect.any(Object)
    );
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    expect(matSnackBar.open).toHaveBeenCalledWith(
      'Session updated !',
      'Close',
      { duration: 3000 }
    );
  });

  it('should invalidate form if required fields are empty', () => {
    (component as any).initForm();
    const form = component.sessionForm!;

    form.controls['name'].setValue('');
    form.controls['date'].setValue('');
    form.controls['teacher_id'].setValue('');
    form.controls['description'].setValue('');

    expect(form.invalid).toBeTruthy();

    expect(form.controls['description'].invalid).toBeTruthy();
    expect(form.controls['name'].invalid).toBeTruthy();
    expect(form.controls['date'].invalid).toBeTruthy();
    expect(form.controls['teacher_id'].invalid).toBeTruthy();
  });
});
