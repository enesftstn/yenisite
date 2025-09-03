import { Injectable } from "@angular/core"
import type { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from "@angular/common/http"
import { type Observable, throwError } from "rxjs"
import { catchError, switchMap } from "rxjs/operators"
import { authService } from "../services"

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add auth token to requests
    const token = authService.getAuthToken()

    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      })
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle 401 errors by attempting token refresh
        if (error.status === 401 && token) {
          return authService.refreshToken().pipe(
            switchMap(() => {
              // Retry the original request with new token
              const newToken = authService.getAuthToken()
              const retryReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`,
                },
              })
              return next.handle(retryReq)
            }),
            catchError(() => {
              // Refresh failed, logout user
              authService.logout()
              return throwError(error)
            }),
          )
        }

        return throwError(error)
      }),
    )
  }
}
