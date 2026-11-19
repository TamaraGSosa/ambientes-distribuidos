import { IsNotEmpty, IsOptional, IsString, MaxLength } from 'class-validator';

export class UpdateRecordDto {
  @IsOptional()
  @IsString()
  @IsNotEmpty({ message: 'El campo name no puede estar vacio.' })
  @MaxLength(120, { message: 'El campo name no puede superar los 120 caracteres.' })
  name?: string;

  @IsOptional()
  @IsString()
  description?: string;
}
