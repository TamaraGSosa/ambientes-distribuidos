import { Injectable, UnauthorizedException } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { Role } from '../../users/user.interface';

export interface JwtPayload {
  sub: string;
  username: string;
  rol: Role;
  iat?: number;
  exp?: number;
}

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(configService: ConfigService) {
    const secret = configService.get<string>('JWT_SECRET');
    if (!secret) {
      throw new Error('JWT_SECRET no está configurado en las variables de entorno');
    }

    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: secret,
    });
  }

  validate(payload: JwtPayload) {
    if (!payload.sub || !payload.rol || !payload.exp) {
      throw new UnauthorizedException('El token no contiene los claims requeridos');
    }

    return {
      sub: payload.sub,
      username: payload.username,
      rol: payload.rol,
    };
  }
}
