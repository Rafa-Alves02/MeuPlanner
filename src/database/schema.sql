CREATE DATABASE IF NOT EXISTS meuplanner
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE meuplanner;

CREATE TABLE IF NOT EXISTS usuarios (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    senha_hash  VARCHAR(100) NOT NULL,
    criado_em   DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS contas (
                                      id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      nome            VARCHAR(100) NOT NULL,
                                      tipo            ENUM('CORRENTE','POUPANCA','CARTEIRA','INVESTIMENTO') NOT NULL,
    banco           VARCHAR(50),
    saldo_inicial   DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    saldo_atual     DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    criado_em       DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categorias (
                                          id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          nome    VARCHAR(100) NOT NULL,
                                          tipo    ENUM('ENTRADA','SAIDA') NOT NULL,
    cor     VARCHAR(7) NOT NULL DEFAULT '#607D8B'
);

CREATE TABLE IF NOT EXISTS entradas (
                                        id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        descricao        VARCHAR(255) NOT NULL,
                                        valor            DECIMAL(15,2) NOT NULL,
                                        data_lancamento  DATE NOT NULL,
                                        mes_referencia   VARCHAR(7) NOT NULL,
                                        tipo_recorrencia ENUM('UNICA','RECORRENTE','PARCELAMENTO','FINANCIAMENTO') NOT NULL,
    parcela_atual    INT,
    total_parcelas   INT,
    conta_id         BIGINT NOT NULL,
    categoria_id     BIGINT,
    fitid_ofx        VARCHAR(100),
    FOREIGN KEY (conta_id) REFERENCES contas(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    UNIQUE KEY uk_entradas_conta_fitid (conta_id, fitid_ofx)
);

CREATE TABLE IF NOT EXISTS gastos (
                                      id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      descricao        VARCHAR(255) NOT NULL,
                                      valor            DECIMAL(15,2) NOT NULL,
                                      data_lancamento  DATE NOT NULL,
                                      mes_referencia   VARCHAR(7) NOT NULL,
                                      tipo_gasto       ENUM('FIXO','VARIAVEL','LAZER','MEDICO','CONSUMO') NOT NULL,
    tipo_recorrencia ENUM('UNICA','RECORRENTE','PARCELAMENTO','FINANCIAMENTO') NOT NULL,
    parcela_atual    INT,
    total_parcelas   INT,
    conta_id         BIGINT NOT NULL,
    categoria_id     BIGINT,
    fitid_ofx        VARCHAR(100),
    FOREIGN KEY (conta_id) REFERENCES contas(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    UNIQUE KEY uk_gastos_conta_fitid (conta_id, fitid_ofx)
);

CREATE TABLE IF NOT EXISTS transferencias (
                                              id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              conta_origem_id  BIGINT NOT NULL,
                                              conta_destino_id BIGINT NOT NULL,
                                              valor            DECIMAL(15,2) NOT NULL,
                                              data_transferencia DATE NOT NULL,
                                              descricao        VARCHAR(255),
                                              FOREIGN KEY (conta_origem_id) REFERENCES contas(id),
                                              FOREIGN KEY (conta_destino_id) REFERENCES contas(id)
);

CREATE TABLE IF NOT EXISTS fechamento_mensal (
                                                 id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 mes_referencia   VARCHAR(7) NOT NULL UNIQUE,
                                                 total_entradas   DECIMAL(15,2) NOT NULL,
                                                 total_gastos     DECIMAL(15,2) NOT NULL,
                                                 saldo_final      DECIMAL(15,2) NOT NULL,
                                                 status           ENUM('POSITIVO','NEGATIVO') NOT NULL,
    data_fechamento  DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS metas (
                                     id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     descricao    VARCHAR(255) NOT NULL,
                                     valor_alvo   DECIMAL(15,2) NOT NULL,
                                     valor_atual  DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                                     data_limite  DATE NOT NULL,
                                     conta_id     BIGINT,
                                     concluida    BOOLEAN DEFAULT FALSE,
                                     FOREIGN KEY (conta_id) REFERENCES contas(id)
);

CREATE TABLE IF NOT EXISTS alertas (
                                       id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       descricao      VARCHAR(255) NOT NULL,
                                       categoria_id   BIGINT,
                                       valor_limite   DECIMAL(15,2) NOT NULL,
                                       mes_referencia VARCHAR(7) NOT NULL,
                                       disparado      BOOLEAN DEFAULT FALSE,
                                       FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Categorias padrão
INSERT INTO categorias (nome, tipo, cor) VALUES
                                             ('Salário',      'ENTRADA', '#4CAF50'),
                                             ('Freelance',    'ENTRADA', '#8BC34A'),
                                             ('Investimentos','ENTRADA', '#009688'),
                                             ('Alimentação',  'SAIDA',   '#F44336'),
                                             ('Transporte',   'SAIDA',   '#FF9800'),
                                             ('Saúde',        'SAIDA',   '#E91E63'),
                                             ('Lazer',        'SAIDA',   '#9C27B0'),
                                             ('Moradia',      'SAIDA',   '#3F51B5'),
                                             ('Educação',     'SAIDA',   '#00BCD4'),
                                             ('Outros',       'SAIDA',   '#607D8B');