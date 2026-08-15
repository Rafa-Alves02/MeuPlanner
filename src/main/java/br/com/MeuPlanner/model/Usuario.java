package br.com.MeuPlanner.model;

public class Usuario {

    private Long id;
    private String username;
    private String senhaHash;

    public Usuario() {
    }

    public Usuario(String username, String senhaHash) {
        this.username = username;
        this.senhaHash = senhaHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }
}
