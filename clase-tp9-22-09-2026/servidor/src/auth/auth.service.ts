import { Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { UsersService } from '../users/users.service';
import { LoginDto } from './dto/login.dto';

@Injectable()
export class AuthService {
  constructor(
    private readonly usersService: UsersService,
    private readonly jwtService: JwtService,
  ) {}

  async login(loginDto: LoginDto) {
    const user = this.usersService.findByUsername(loginDto.username);
    if (!user) {
      throw new UnauthorizedException('Credenciales inválidas');
    }

    const passwordValid = await bcrypt.compare(
      loginDto.password,
      user.passwordHash,
    );
    if (!passwordValid) {
      throw new UnauthorizedException('Credenciales inválidas');
    }

    const payload = {
      sub: user.id,
      username: user.username,
      rol: user.rol,
    };

    const access_token = await this.jwtService.signAsync(payload);

    return {
      access_token,
      token_type: 'Bearer',
    };
  }
}
