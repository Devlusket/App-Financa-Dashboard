CREATE TABLE conta_fixa (
    id UUID PRIMARY KEY,
    casa_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    nome VARCHAR(255) NOT NULL,
    valor_atual NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_conta_fixa_casa
        FOREIGN KEY (casa_id) REFERENCES casa (id),
    CONSTRAINT fk_conta_fixa_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria (id)
);

CREATE INDEX idx_conta_fixa_casa_id ON conta_fixa (casa_id);
