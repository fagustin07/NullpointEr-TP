CREATE TABLE IF NOT EXISTS party (
  id BIGINT auto_increment NOT NULL,
  nombre VARCHAR(255) UNIQUE,
  numeroDeAventureros INT DEFAULT 0,
  CONSTRAINT party_pk PRIMARY KEY (id)
);