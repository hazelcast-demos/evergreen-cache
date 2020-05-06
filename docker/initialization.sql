CREATE DATABASE IF NOT EXISTS app;

USE app;

CREATE TABLE IF NOT EXISTS Person (
  id BIGINT PRIMARY KEY NOT NULL,
  firstName VARCHAR(50) NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  birthdate DATE
);

INSERT INTO Person(id, firstName, lastName, birthdate) VALUES (1, 'John', 'Doe', '1971-02-03');
INSERT INTO Person(id, firstName, lastName, birthdate) VALUES (2, 'Jane', 'Doe', '1974-05-06');
INSERT INTO Person(id, firstName, lastName)            VALUES (3, 'John', 'Roe');
