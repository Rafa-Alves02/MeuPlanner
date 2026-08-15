package br.com.MeuPlanner.ofx;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.MeuPlanner.exception.OfxParseException;

public final class OfxParser {

    private static final Pattern TRANSACAO = Pattern.compile("<STMTTRN>(.*?)</STMTTRN>", Pattern.DOTALL);
    private static final Pattern TAG = Pattern.compile("<(\\w+)>([^<\\r\\n]*)");
    private static final DateTimeFormatter DATA_OFX = DateTimeFormatter.ofPattern("yyyyMMdd");

    private OfxParser() {}

    public static List<TransacaoOfx> parse(File arquivo) {
        String conteudo = lerComCharsetCorreto(arquivo);
        List<TransacaoOfx> transacoes = new ArrayList<>();

        Matcher matcherTransacao = TRANSACAO.matcher(conteudo);
        while (matcherTransacao.find()) {
            transacoes.add(parseTransacao(matcherTransacao.group(1)));
        }

        if (transacoes.isEmpty()) {
            throw new OfxParseException("Nenhuma transação encontrada no arquivo — confirme que é um OFX válido.");
        }
        return transacoes;
    }

    private static TransacaoOfx parseTransacao(String bloco) {
        String fitid = null;
        String dtposted = null;
        String trnamt = null;
        String memo = null;
        String name = null;

        Matcher matcherTag = TAG.matcher(bloco);
        while (matcherTag.find()) {
            String tag = matcherTag.group(1).toUpperCase();
            String valor = matcherTag.group(2).trim();
            switch (tag) {
                case "FITID" -> fitid = valor;
                case "DTPOSTED" -> dtposted = valor;
                case "TRNAMT" -> trnamt = valor;
                case "MEMO" -> memo = valor;
                case "NAME" -> name = valor;
                default -> { }
            }
        }

        if (fitid == null || fitid.isBlank() || dtposted == null || trnamt == null) {
            throw new OfxParseException("Transação incompleta no OFX (faltando FITID, DTPOSTED ou TRNAMT).");
        }

        LocalDate data = LocalDate.parse(dtposted.substring(0, 8), DATA_OFX);
        BigDecimal valor = new BigDecimal(trnamt.trim().replace(",", "."));
        String descricao = (memo != null && !memo.isBlank()) ? memo
                : (name != null && !name.isBlank()) ? name
                : "Importado do OFX";

        return new TransacaoOfx(fitid, data, valor, descricao);
    }

    private static String lerComCharsetCorreto(File arquivo) {
        try {
            byte[] bytes = Files.readAllBytes(arquivo.toPath());
            String cabecalho = new String(bytes, 0, Math.min(bytes.length, 512), StandardCharsets.ISO_8859_1);
            Charset charset = cabecalho.contains("CHARSET:1252") || cabecalho.contains("CHARSET:8859-1")
                    ? StandardCharsets.ISO_8859_1
                    : StandardCharsets.UTF_8;
            return new String(bytes, charset);
        } catch (IOException e) {
            throw new OfxParseException("Não foi possível ler o arquivo OFX: " + e.getMessage());
        }
    }
}
