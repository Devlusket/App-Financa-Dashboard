CREATE TABLE lancamento (
    id UUID PRIMARY KEY,
    casa_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor NUMERIC(19, 2) NOT NULL,
    data DATE NOT NULL,
    mes_referencia VARCHAR(7) NOT NULL,
    responsavel_pagamento_id UUID,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDENTE',
    CONSTRAINT ck_lancamento_status CHECK (status IN ('PAGO', 'PENDENTE')),
    CONSTRAINT ck_lancamento_mes_referencia CHECK (mes_referencia ~ '^[0-9]{4}-(0[1-9]|1[0-2])$'),
    CONSTRAINT fk_lancamento_casa
        FOREIGN KEY (casa_id) REFERENCES casa (id),
    CONSTRAINT fk_lancamento_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_lancamento_responsavel_pagamento
        FOREIGN KEY (responsavel_pagamento_id) REFERENCES pessoa (id)
);

CREATE INDEX idx_lancamento_casa_mes_referencia ON lancamento (casa_id, mes_referencia);
CREATE INDEX idx_lancamento_categoria_id ON lancamento (categoria_id);
