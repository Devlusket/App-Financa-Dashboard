CREATE TABLE renda_adicional (
    id UUID PRIMARY KEY,
    renda_id UUID NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_renda_adicional_renda
        FOREIGN KEY (renda_id) REFERENCES renda (id)
);

CREATE INDEX idx_renda_adicional_renda_id ON renda_adicional (renda_id);
