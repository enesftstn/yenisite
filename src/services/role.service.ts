export interface UserRole {
  role: "USER" | "ADMIN" | "MODERATOR"
  permissions: string[]
}

export class RoleService {
  private static readonly ROLE_PERMISSIONS = {
    USER: [
      "read:own-parts",
      "create:parts",
      "update:own-parts",
      "delete:own-parts",
      "create:orders",
      "read:own-orders",
    ],
    MODERATOR: ["read:all-parts", "approve:parts", "delete:any-parts", "ban:users", "read:reports"],
    ADMIN: ["*"], // All permissions
  }

  static hasPermission(userRole: string, permission: string): boolean {
    const permissions = this.ROLE_PERMISSIONS[userRole as keyof typeof this.ROLE_PERMISSIONS]
    return permissions?.includes("*") || permissions?.includes(permission) || false
  }

  static canAccessAdminPanel(userRole: string): boolean {
    return userRole === "ADMIN"
  }

  static canModerate(userRole: string): boolean {
    return userRole === "ADMIN" || userRole === "MODERATOR"
  }

  static canManageUsers(userRole: string): boolean {
    return userRole === "ADMIN"
  }

  static canDeleteAnyPart(userRole: string): boolean {
    return userRole === "ADMIN" || userRole === "MODERATOR"
  }
}
