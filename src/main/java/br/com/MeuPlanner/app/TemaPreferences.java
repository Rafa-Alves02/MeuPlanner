package br.com.MeuPlanner.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class TemaPreferences {

    private static final Path ARQUIVO = Path.of(System.getProperty("user.home"), ".meuplanner", "ui.properties");
    private static final String COR_PADRAO = "#45f3ff";

    private TemaPreferences() {}

    public static String corAcentoSalva() {
        return carregar().getProperty("cor.acento", COR_PADRAO);
    }

    public static void salvarCorAcento(String corHex) {
        Properties props = carregar();
        props.setProperty("cor.acento", corHex);
        salvar(props);
    }

    private static Properties carregar() {
        Properties props = new Properties();
        if (Files.exists(ARQUIVO)) {
            try (InputStream in = Files.newInputStream(ARQUIVO)) {
                props.load(in);
            } catch (IOException ignored) {
            }
        }
        return props;
    }

    private static void salvar(Properties props) {
        try {
            Files.createDirectories(ARQUIVO.getParent());
            try (OutputStream out = Files.newOutputStream(ARQUIVO)) {
                props.store(out, null);
            }
        } catch (IOException ignored) {
        }
    }
}
