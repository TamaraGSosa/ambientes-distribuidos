import { Injectable } from '@nestjs/common';
import { Role, User } from './user.interface';

/**
 * Repositorio en memoria de usuarios de demostración.
 * Decisión de alcance académico: se omite una base de datos externa,
 * manteniendo las contraseñas exclusivamente como hashes bcrypt.
 */
@Injectable()
export class UsersService {
  private readonly users: User[] = [
    {
      id: '1',
      username: 'admin',
      // Hash bcrypt (cost 10) de la credencial de demostración para rol ADMIN
      passwordHash: '$2b$10$3n0pfqBtFNCwa/P8ST6SzO0bPoIvq6EmEJAlN33bRt7XJ.dHp7sOi',
      rol: Role.ADMIN,
    },
    {
      id: '2',
      username: 'usuario',
      // Hash bcrypt (cost 10) de la credencial de demostración para rol USER
      passwordHash: '$2b$10$o7p3rh2j6VAqSOSPO6Duse7alo.DCpfocb4wCi2PCbmBxrchOOtaG',
      rol: Role.USER,
    },
  ];

  findByUsername(username: string): User | undefined {
    return this.users.find((u) => u.username === username);
  }
}
