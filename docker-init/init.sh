#!/bin/bash
set -e

echo "=== Initializing Multiple PostgreSQL Databases ==="

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE student_db;
    CREATE DATABASE course_db;
    CREATE DATABASE grade_db;
EOSQL

echo "Applying schemas and sample data..."
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "student_db" -f /sql/01-student_db.sql
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "course_db" -f /sql/02-course_db.sql
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "grade_db" -f /sql/03-grade_db.sql

echo "=== All 3 Databases successfully initialized! ==="
