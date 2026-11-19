#!/bin/sh
set -e

PGDATA="/var/lib/postgresql/data"

if [ ! -s "$PGDATA/PG_VERSION" ]; then
  echo "[replica] Esperando a que el nodo Primary (${PRIMARY_HOST}:${PRIMARY_PORT}) acepte conexiones..."
  until pg_isready -h "$PRIMARY_HOST" -p "$PRIMARY_PORT" -U "$POSTGRES_USER"; do
    sleep 2
  done

  echo "[replica] Limpiando directorio PGDATA e iniciando pg_basebackup..."
  rm -rf "${PGDATA:?}/"*

  PGPASSWORD="$REPLICATION_PASSWORD" pg_basebackup \
    -h "$PRIMARY_HOST" \
    -p "$PRIMARY_PORT" \
    -U "$REPLICATION_USER" \
    -D "$PGDATA" \
    -Fp \
    -Xs \
    -P \
    -R \
    -S replica_slot

  chown -R postgres:postgres "$PGDATA"
  chmod 0700 "$PGDATA"
  echo "[replica] Backup base completado. standby.signal configurado."
fi

echo "[replica] Iniciando PostgreSQL en modo hot_standby (solo lectura)..."
exec su-exec postgres postgres -c hot_standby=on
