package br.com.MeuPlanner.app;

import java.text.Normalizer;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class BancoAvatar {

    private BancoAvatar() {}

    public static Node criar(String banco, double tamanho) {
        var imagem = BancoAvatar.class.getResourceAsStream("/images/bancos/" + slug(banco) + ".png");
        if (imagem != null) {
            return new ImageView(new Image(imagem, tamanho, tamanho, true, true));
        }
        return avatarPadrao(banco, tamanho);
    }

    private static Node avatarPadrao(String banco, double tamanho) {
        Circle circulo = new Circle(tamanho / 2, corDe(banco));
        Text texto = new Text(inicialDe(banco));
        texto.setFill(Color.WHITE);
        texto.setFont(Font.font(tamanho * 0.45));

        StackPane pane = new StackPane(circulo, texto);
        pane.setPrefSize(tamanho, tamanho);
        pane.setMaxSize(tamanho, tamanho);
        return pane;
    }

    private static Color corDe(String banco) {
        return switch (slug(banco)) {
            case "nubank" -> Color.web("#8A05BE");
            case "bradesco" -> Color.web("#CC092F");
            case "inter" -> Color.web("#FF7A00");
            default -> Color.web("#3a3a3a");
        };
    }

    private static String inicialDe(String banco) {
        return (banco == null || banco.isBlank()) ? "?" : banco.trim().substring(0, 1).toUpperCase();
    }

    private static String slug(String banco) {
        if (banco == null || banco.isBlank()) return "outro";
        String semAcento = Normalizer.normalize(banco.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.replaceAll("[^a-z0-9]+", "-");
    }
}
