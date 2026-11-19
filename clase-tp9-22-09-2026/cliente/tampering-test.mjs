/**
 * Script de verificación end-to-end sobre HTTPS y prueba de Tampering JWT.
 * Ejecutar con el servidor TP9 levantado en https://localhost:3443:
 *   node TP9/cliente/tampering-test.mjs
 */

import https from 'node:https';

// Se acepta el certificado autofirmado únicamente en este script de prueba local.
const agent = new https.Agent({ rejectUnauthorized: false });
const BASE_URL = process.env.TP9_URL || 'https://localhost:3443';

function request(method, path, body = null, token = null) {
  return new Promise((resolve, reject) => {
    const url = new URL(path, BASE_URL);
    const headers = {};
    let payloadStr = null;

    if (body) {
      payloadStr = JSON.stringify(body);
      headers['Content-Type'] = 'application/json';
      headers['Content-Length'] = Buffer.byteLength(payloadStr);
    }
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const req = https.request(
      url,
      { method, headers, agent },
      (res) => {
        let data = '';
        res.on('data', (chunk) => (data += chunk));
        res.on('end', () => {
          let parsed = data;
          try {
            parsed = JSON.parse(data);
          } catch {
            // mantener texto plano si no es JSON
          }
          resolve({ status: res.statusCode, body: parsed });
        });
      },
    );

    req.on('error', reject);
    if (payloadStr) req.write(payloadStr);
    req.end();
  });
}

function assertStatus(name, actual, expected) {
  const ok = actual === expected;
  console.log(`[${ok ? 'OK' : 'FAIL'}] ${name} -> HTTP ${actual} (esperado: ${expected})`);
  if (!ok) {
    process.exitCode = 1;
  }
}

async function main() {
  console.log(`=== Pruebas TP9 contra ${BASE_URL} ===\n`);

  // 1. Login ADMIN
  const loginAdmin = await request('POST', '/login', {
    username: 'admin',
    password: 'Admin123!',
  });
  assertStatus('Prueba 1a: POST /login (admin)', loginAdmin.status, 200);

  // 2. Login USER
  const loginUser = await request('POST', '/login', {
    username: 'usuario',
    password: 'Usuario123!',
  });
  assertStatus('Prueba 1b: POST /login (usuario)', loginUser.status, 200);

  const adminToken = loginAdmin.body?.access_token;
  const userToken = loginUser.body?.access_token;

  // 3. Acceso autorizado a GET /profile
  const profileOk = await request('GET', '/profile', null, userToken);
  assertStatus('Prueba 2: GET /profile con JWT válido', profileOk.status, 200);

  // 4. Rechazo por falta de token
  const profileNoToken = await request('GET', '/profile');
  assertStatus('Prueba 3a: GET /profile sin token', profileNoToken.status, 401);

  // 5. Rechazo por token inválido malformado
  const profileInvalid = await request('GET', '/profile', null, 'token.invalido.firma');
  assertStatus('Prueba 3b: GET /profile con token inválido', profileInvalid.status, 401);

  // 6. Autorización por roles: USER -> /admin (403) y ADMIN -> /admin (200)
  const adminForbidden = await request('GET', '/admin', null, userToken);
  assertStatus('Prueba complementaria a: GET /admin con rol USER', adminForbidden.status, 403);

  const adminOk = await request('GET', '/admin', null, adminToken);
  assertStatus('Prueba complementaria b: GET /admin con rol ADMIN', adminOk.status, 200);

  // 7. Prueba de Tampering (alteración de payload sin refirmar)
  console.log('\n=== Procedimiento de Tampering ===');
  const [headerB64, payloadB64, signatureB64] = userToken.split('.');
  const decodedPayload = JSON.parse(Buffer.from(payloadB64, 'base64url').toString('utf8'));
  console.log('Payload original:', decodedPayload);

  const alteredPayload = { ...decodedPayload, rol: 'ADMIN' };
  const alteredPayloadB64 = Buffer.from(JSON.stringify(alteredPayload), 'utf8').toString('base64url');
  const tamperedToken = `${headerB64}.${alteredPayloadB64}.${signatureB64}`;

  console.log('Payload alterado:', alteredPayload);
  console.log('Firma original conservada (sin refirmar).');

  const tamperingAdmin = await request('GET', '/admin', null, tamperedToken);
  assertStatus('Prueba 4a: GET /admin con token alterado (Tampering)', tamperingAdmin.status, 401);

  const tamperingProfile = await request('GET', '/profile', null, tamperedToken);
  assertStatus('Prueba 4b: GET /profile con token alterado (Tampering)', tamperingProfile.status, 401);
}

main().catch((err) => {
  console.error('Error ejecutando las pruebas:', err);
  process.exit(1);
});
