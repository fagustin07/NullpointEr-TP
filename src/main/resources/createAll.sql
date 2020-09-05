CREATE TABLE IF NOT EXISTS party (
  id INT auto_increment NOT NULL,
  numeroDeAventureros VARCHAR(255) NOT NULL,
  CONSTRAINT party_pk PRIMARY KEY (id)
);