package br.com.MeuPlanner.service;

import java.util.Optional;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.MeuPlanner.exception.BusinessException;
import br.com.MeuPlanner.model.Usuario;
import br.com.MeuPlanner.repository.UsuarioRepository;

public class AuthService {

    private static final int CUSTO_BCRYPT = 12;

    private final UsuarioRepository usuarioRepo = new UsuarioRepository();

    public Optional<Usuario> autenticar(String username, String senha) {
        if (username == null || username.isBlank() || senha == null || senha.isEmpty()) {
            return Optional.empty();
        }
        return usuarioRepo.buscarPorUsername(username.trim())
                .filter(usuario -> BCrypt.verifyer().verify(senha.toCharArray(), usuario.getSenhaHash()).verified);
    }

    public Usuario registrar(String username, String senha) {
        if (username == null || username.isBlank())
            throw new BusinessException("Usuário é obrigatório!");
        if (senha == null || senha.length() < 6)
            throw new BusinessException("Senha deve ter pelo menos 6 caracteres!");

        String usernameNormalizado = username.trim();
        if (usuarioRepo.existeUsername(usernameNormalizado))
            throw new BusinessException("Esse usuário já existe!");

        String hash = BCrypt.withDefaults().hashToString(CUSTO_BCRYPT, senha.toCharArray());
        Usuario usuario = new Usuario(usernameNormalizado, hash);
        usuarioRepo.salvar(usuario);
        return usuario;
    }
}
