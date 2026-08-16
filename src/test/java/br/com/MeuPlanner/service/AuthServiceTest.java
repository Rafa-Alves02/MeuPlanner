package br.com.MeuPlanner.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.com.MeuPlanner.exception.BusinessException;

class AuthServiceTest {

    private final AuthService authService = new AuthService();

    @Test
    void rejeitaUsernameVazio() {
        assertThrows(BusinessException.class, () -> authService.registrar("", "senha123"));
    }

    @Test
    void rejeitaSenhaCurta() {
        assertThrows(BusinessException.class, () -> authService.registrar("rafa", "123"));
    }

    @Test
    void rejeitaSenhaNula() {
        assertThrows(BusinessException.class, () -> authService.registrar("rafa", null));
    }

    @Test
    void naoAutenticaComCredenciaisVazias() {
        assertTrue(authService.autenticar("", "").isEmpty());
        assertTrue(authService.autenticar(null, null).isEmpty());
    }
}
