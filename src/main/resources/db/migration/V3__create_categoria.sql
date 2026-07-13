CREATE TABLE categoria (
    id UUID PRIMARY KEY,
    casa_id UUID NOT NULL,
    nome VARCHAR(255) NOT NULL,
    tipo_divisao VARCHAR(30) NOT NULL,
    responsavel_id UUID,
    eh_poupanca BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT ck_categoria_tipo_divisao CHECK (tipo_divisao IN (
        'FIXO_POR_PESSOA', 'PERCENTUAL', 'VALOR_FIXO_DIVIDIDO'
    )),
    CONSTRAINT fk_categoria_casa
        FOREIGN KEY (casa_id) REFERENCES casa (id),
    CONSTRAINT fk_categoria_responsavel
        FOREIGN KEY (responsavel_id) REFERENCES pessoa (id)
);

CREATE INDEX idx_categoria_casa_id ON categoria (casa_id);

CREATE TABLE categoria_divisao (
    id UUID PRIMARY KEY,
    categoria_id UUID NOT NULL,
    pessoa_id UUID NOT NULL,
    percentual NUMERIC(5, 2) NOT NULL,
    CONSTRAINT uk_categoria_divisao_categoria_pessoa UNIQUE (categoria_id, pessoa_id),
    CONSTRAINT ck_categoria_divisao_percentual CHECK (percentual >= 0 AND percentual <= 100),
    CONSTRAINT fk_categoria_divisao_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_categoria_divisao_pessoa
        FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
);

CREATE INDEX idx_categoria_divisao_pessoa_id ON categoria_divisao (pessoa_id);
