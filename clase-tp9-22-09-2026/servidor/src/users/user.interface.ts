export enum Role {
  ADMIN = 'ADMIN',
  USER = 'USER',
}

export interface User {
  id: string;
  username: string;
  passwordHash: string;
  rol: Role;
}
