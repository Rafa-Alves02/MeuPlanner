CREATE TABLE entrada(
    id integer primary key AUTOINCREMENT,
    descricao TEXT NOT NULL,
    valor numeric NOT NULL,
    data_lancamento DATE NOT NULL,
    mes_referencia TEXT NOT NULL,
    tipo_recorrencia TEXT NOT NULL,
    total_parcelas INTEGER,
    parcela_atual INTEGER
);

CREATE TABLE gasto(
                      id integer primary key AUTOINCREMENT,
                      descricao TEXT NOT NULL,
                      valor numeric NOT NULL,
                      data_lancamento DATE NOT NULL,
                      mes_referencia TEXT NOT NULL,
                      tipo_recorrencia TEXT NOT NULL,
                      total_parcelas INTEGER,
                      parcela_atual INTEGER
);

CREATE TABLE fechamento_mensal(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    mes TEXT NOT NULL UNIQUE,
    total_entradas NUMERIC NOT NULL,
    total_gastos NUMERIC NOT NULL,
    saldo_final NUMERIC NOT NULL,
    status TEXT NOT NULL,
    data_fechamento DATE NOT NULL
);
