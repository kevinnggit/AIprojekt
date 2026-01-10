-- Benutzer erstellen
CREATE USER java_user WITH PASSWORD 'java_password';
CREATE USER python_user WITH PASSWORD 'python_password';

-- Datenbanken erstellen
CREATE DATABASE java_db OWNER java_user;
CREATE DATABASE python_db OWNER python_user;

-- Privilegien gewähren (Optional bei einigen Postgres-Versionen, da Owner volle Rechte hat, aber Best Practice)
GRANT ALL PRIVILEGES ON DATABASE java_db TO java_user;
GRANT ALL PRIVILEGES ON DATABASE python_db TO python_user;
