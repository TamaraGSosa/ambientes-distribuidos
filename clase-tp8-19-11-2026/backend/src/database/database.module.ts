import { Module } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { RecordEntity } from '../records/entities/record.entity';

function buildPostgresConfig(
  configService: ConfigService,
  prefix: 'DB_PRIMARY' | 'DB_REPLICA',
  name: 'primary' | 'replica',
) {
  return {
    name,
    type: 'postgres' as const,
    host: configService.get<string>(`${prefix}_HOST`, 'localhost'),
    port: Number(
      configService.get<string>(
        `${prefix}_PORT`,
        name === 'primary' ? '5433' : '5434',
      ),
    ),
    username: configService.get<string>(`${prefix}_USER`, 'postgres'),
    password: configService.get<string>(`${prefix}_PASSWORD`),
    database: configService.get<string>(`${prefix}_NAME`, 'tp8_db'),
    entities: [RecordEntity],
    synchronize: false,
    retryAttempts: 2,
    retryDelay: 1000,
    extra: {
      connectionTimeoutMillis: 3000,
    },
  };
}

@Module({
  imports: [
    TypeOrmModule.forRootAsync({
      name: 'primary',
      inject: [ConfigService],
      useFactory: (configService: ConfigService) =>
        buildPostgresConfig(configService, 'DB_PRIMARY', 'primary'),
    }),
    TypeOrmModule.forRootAsync({
      name: 'replica',
      inject: [ConfigService],
      useFactory: (configService: ConfigService) =>
        buildPostgresConfig(configService, 'DB_REPLICA', 'replica'),
    }),
  ],
  exports: [TypeOrmModule],
})
export class DatabaseModule {}
