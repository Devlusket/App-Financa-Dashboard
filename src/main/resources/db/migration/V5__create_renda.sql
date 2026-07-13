CREATE TABLE renda (
    id UUID PRIMARY KEY,
    pessoa_id UUID NOT NULL,
    mes_referencia VARCHAR(7) NOT NULL,
    valor_fixo NUMERIC(19, 2) NOT NULL,
    CONSTRAINT uk_renda_pessoa_mes_referencia UNIQUE (pessoa_id, mes_referencia),
    CONSTRAINT ck_renda_mes_referencia CHECK (mes_referencia ~ '^[0-9]{4}-(0[1-9]|1[0-2])$'),
    CONSTRAINT fk_renda_pessoa
        FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
);

CREATE INDEX idx_renda_pessoa_id ON renda (pessoa_id);
