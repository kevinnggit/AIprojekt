\getenv java_db_user JAVA_DB_USER
\getenv java_db_password JAVA_DB_PASSWORD
\getenv java_db_name JAVA_DB_NAME
\getenv python_db_user PYTHON_DB_USER
\getenv python_db_password PYTHON_DB_PASSWORD
\getenv python_db_name PYTHON_DB_NAME

-- Benutzer aus Umgebungsvariablen erstellen
CREATE USER :"java_db_user" WITH PASSWORD :'java_db_password';
CREATE USER :"python_db_user" WITH PASSWORD :'python_db_password';

-- Datenbanken aus Umgebungsvariablen erstellen
CREATE DATABASE :"java_db_name" OWNER :"java_db_user";
CREATE DATABASE :"python_db_name" OWNER :"python_db_user";

-- Privilegien gewähren
GRANT ALL PRIVILEGES ON DATABASE :"java_db_name" TO :"java_db_user";
GRANT ALL PRIVILEGES ON DATABASE :"python_db_name" TO :"python_db_user";
