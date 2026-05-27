export function defineExtension(extension) {
  return extension
}

export class PermissionError extends Error {
  constructor(permission) {
    super(`Extension permission denied: ${permission}`)
    this.permission = permission
  }
}

export function requirePermission(ctx, permission) {
  if (!ctx.extension.permissions?.includes(permission)) {
    throw new PermissionError(permission)
  }
}
