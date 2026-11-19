#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  DO \$\$
  BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${REPLICATION_USER}') THEN
      CREATE ROLE ${REPLICATION_USER} WITH REPLICATION LOGIN ENCRYPTED PASSWORD '${REPLICATION_PASSWORD}';
    END IF;
  END
  \$\$;

  SELECT pg_create_physical_replication_slot('replica_slot')
  WHERE NOT EXISTS (
    SELECT 1 FROM pg_replication_slots WHERE slot_name = 'replica_slot'
  );
EOSQL

# Habilitar autenticacion de replicacion y red interna en pg_hba.conf
echo "host replication ${REPLICATION_USER} 0.0.0.0/0 scram-sha-256" >> "$PGDATA/pg_hba.conf"
echo "host all all 0.0.0.0/0 scram-sha-256" >> "$PGDATA/pg_hba.conf"
