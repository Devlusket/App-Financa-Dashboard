CREATE TABLE pessoa (
    id UUID PRIMARY KEY,
    casa_id UUID NOT NULL,
    nome VARCHAR(255) NOT NULL,
    CONSTRAINT fk_pessoa_casa
        FOREIGN KEY (casa_id) REFERENCES casa (id)
);

CREATE INDEX idx_pessoa_casa_id ON pessoa (casa_id);
