package br.com.MeuPlanner.model;

public class Categoria {
    public enum TipoCategoria {
    ENTRADA, SAIDA
}

    private Long id; 

    private String nome;

    private TipoCategoria tipo;

    private String cor; 

    public Categoria() {}

    public Categoria(String nome, TipoCategoria tipo, String cor) {
        this.nome = nome;
        this.tipo = tipo;
        this.cor = cor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public TipoCategoria getTipo() {
    return tipo;
}

public void setTipo(TipoCategoria tipo) {
    this.tipo = tipo;
}

    @Override
    public String toString() {
        return nome; 
    }

}