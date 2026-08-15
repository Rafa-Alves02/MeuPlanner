package br.com.MeuPlanner.ofx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import br.com.MeuPlanner.exception.OfxParseException;

class OfxParserTest {

    private static final String OFX_VALIDO = """
            OFXHEADER:100
            DATA:OFXSGML
            VERSION:102
            SECURITY:NONE
            ENCODING:USASCII
            CHARSET:1252
            COMPRESSION:NONE
            OLDFILEUID:NONE
            NEWFILEUID:NONE

            <OFX>
            <BANKMSGSRSV1>
            <STMTTRNRS>
            <STMTRS>
            <BANKTRANLIST>
            <DTSTART>20260801
            <DTEND>20260810
            <STMTTRN>
            <TRNTYPE>DEBIT
            <DTPOSTED>20260805120000
            <TRNAMT>-89.90
            <FITID>2026080500001
            <MEMO>Supermercado Extra
            </STMTTRN>
            <STMTTRN>
            <TRNTYPE>CREDIT
            <DTPOSTED>20260807120000
            <TRNAMT>1500.00
            <FITID>2026080700002
            <MEMO>Salário
            </STMTTRN>
            </BANKTRANLIST>
            </STMTRS>
            </STMTTRNRS>
            </BANKMSGSRSV1>
            </OFX>
            """;

    @TempDir
    File pasta;

    @Test
    void parseiaTransacoesDoArquivoOfx() throws IOException {
        File arquivo = escrever(OFX_VALIDO);

        List<TransacaoOfx> transacoes = OfxParser.parse(arquivo);

        assertEquals(2, transacoes.size());

        TransacaoOfx gasto = transacoes.get(0);
        assertEquals("2026080500001", gasto.fitid());
        assertEquals(LocalDate.of(2026, 8, 5), gasto.data());
        assertEquals(new BigDecimal("-89.90"), gasto.valor());
        assertEquals("Supermercado Extra", gasto.descricao());
        assertFalse(gasto.isEntrada());

        TransacaoOfx entrada = transacoes.get(1);
        assertEquals("2026080700002", entrada.fitid());
        assertEquals(new BigDecimal("1500.00"), entrada.valor());
        assertTrue(entrada.isEntrada());
    }

    @Test
    void rejeitaArquivoSemTransacoes() throws IOException {
        File arquivo = escrever("<OFX></OFX>");

        assertThrows(OfxParseException.class, () -> OfxParser.parse(arquivo));
    }

    @Test
    void rejeitaTransacaoSemFitid() throws IOException {
        String semFitid = """
                <OFX>
                <STMTTRN>
                <DTPOSTED>20260805120000
                <TRNAMT>-50.00
                <MEMO>Sem FITID
                </STMTTRN>
                </OFX>
                """;
        File arquivo = escrever(semFitid);

        assertThrows(OfxParseException.class, () -> OfxParser.parse(arquivo));
    }

    private File escrever(String conteudo) throws IOException {
        File arquivo = new File(pasta, "extrato.ofx");
        Files.writeString(arquivo.toPath(), conteudo, StandardCharsets.ISO_8859_1);
        return arquivo;
    }
}
