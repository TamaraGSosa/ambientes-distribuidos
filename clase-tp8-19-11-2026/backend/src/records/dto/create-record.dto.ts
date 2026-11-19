import { IsNotEmpty, IsOptional, IsString, MaxLength } from 'class-validator';

export class CreateRecordDto {
  @IsString()
  @IsNotEmpty({ message: 'El campo name es obligatorio.' })
  @MaxLength(120, { message: 'El campo name no puede superar los 120 caracteres.' })
  name: string;

  @IsOptional()
  @IsString()
  description?: string;
}
