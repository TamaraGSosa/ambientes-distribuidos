export function extractDbErrorMessage(error: unknown): string {
  if (error instanceof AggregateError && Array.isArray(error.errors) && error.errors.length > 0) {
    const details = error.errors
      .map((inner) => (inner instanceof Error ? inner.message || inner.name : String(inner)))
      .filter(Boolean);
    if (details.length > 0) {
      return details.join(' | ');
    }
  }

  if (error instanceof Error) {
    const cause = (error as Error & { cause?: unknown }).cause;
    if (error.message) {
      return error.message;
    }
    if (cause instanceof Error && cause.message) {
      return cause.message;
    }
    const code = (error as Error & { code?: string }).code;
    if (code) {
      return code;
    }
    return error.name || 'Database connection error';
  }

  return 'Error desconocido de base de datos';
}
