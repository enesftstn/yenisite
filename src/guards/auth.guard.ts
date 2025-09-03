import { Injectable } from "@angular/core"
import type { CanActivate, Router } from "@angular/router"
import { authService } from "../services"

@Injectable({
  providedIn: "root",
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    if (authService.isAuthenticated()) {
      return true
    } else {
      // Redirect to login or show login modal
      console.log("[v0] Access denied - user not authenticated")
      return false
    }
  }
}
