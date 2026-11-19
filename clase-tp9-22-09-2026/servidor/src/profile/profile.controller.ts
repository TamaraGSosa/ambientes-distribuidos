import { Controller, Get, Req, UseGuards } from '@nestjs/common';
import { Request } from 'express';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';

@Controller('profile')
export class ProfileController {
  @Get()
  @UseGuards(JwtAuthGuard)
  getProfile(@Req() req: Request) {
    return {
      mensaje: 'Acceso autorizado al perfil mediante HTTPS y JWT válido',
      usuario: req.user,
    };
  }
}
