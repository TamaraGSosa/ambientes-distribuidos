import { Controller, Get, Req, UseGuards } from '@nestjs/common';
import { Request } from 'express';
import { Roles } from '../auth/decorators/roles.decorator';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Role } from '../users/user.interface';

@Controller('admin')
export class AdminController {
  @Get()
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles(Role.ADMIN)
  getAdminPanel(@Req() req: Request) {
    return {
      mensaje: 'Acceso autorizado al panel administrativo (rol ADMIN)',
      usuario: req.user,
    };
  }
}
