import { ValidationPipe } from '@nestjs/common';
import { NestFactory } from '@nestjs/core';
import * as fs from 'fs';
import * as path from 'path';
import { AppModule } from './app.module';

const envFilePath = path.resolve(process.cwd(), '.env');
if (fs.existsSync(envFilePath)) {
  process.loadEnvFile(envFilePath);
}

async function bootstrap() {
  const keyPathEnv = process.env.TLS_KEY_PATH || './certs/server.key';
  const certPathEnv = process.env.TLS_CERT_PATH || './certs/server.crt';

  const keyPath = path.resolve(process.cwd(), keyPathEnv);
  const certPath = path.resolve(process.cwd(), certPathEnv);

  if (!fs.existsSync(keyPath) || !fs.existsSync(certPath)) {
    throw new Error(
      `No se encontraron los archivos TLS requeridos (key: ${keyPath}, cert: ${certPath}). Ejecute el script de generación de certificados.`,
    );
  }

  const httpsOptions = {
    key: fs.readFileSync(keyPath),
    cert: fs.readFileSync(certPath),
  };

  const app = await NestFactory.create(AppModule, { httpsOptions });

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
    }),
  );

  const port = Number(process.env.PORT || 3443);
  await app.listen(port);
  console.log(`Servidor TP9 escuchando exclusivamente en https://localhost:${port}`);
}

bootstrap();
