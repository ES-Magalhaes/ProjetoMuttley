package com.muttley.inscricao;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrCodeService {
    public String gerarQrCodeBase64(String texto, int largura, int altura) {
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            // Gerar a matriz do QR Code
            BitMatrix bm = qrWriter.encode(texto, BarcodeFormat.QR_CODE, largura, altura);

            // Escreve os bits em memória como uma imagem PNG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bm, "PNG", baos);

            byte[] imgBytes = baos.toByteArray();

            // Codifica o PNG em Base64 para armazenar como string
            return Base64.getEncoder().encodeToString(imgBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar a imagem do QR Code", e);
        }
    }
}
