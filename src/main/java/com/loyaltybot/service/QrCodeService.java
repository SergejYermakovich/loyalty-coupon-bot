package com.loyaltybot.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeService {

    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;

    /**
     * Сгенерировать QR-код для UserCoupon
     * Формат данных: userCouponId (можно расширить до JSON)
     */
    public byte[] generateUserCouponQr(Long userCouponId) throws IOException, WriterException {
        String qrData = String.valueOf(userCouponId);
        return generateQrCode(qrData);
    }

    /**
     * Сгенерировать QR-код для бизнеса (для сканирования пользователем)
     */
    public byte[] generateBusinessQr(Long businessId) throws IOException, WriterException {
        String qrData = String.format("business:%d", businessId);
        return generateQrCode(qrData);
    }

    /**
     * Парсинг QR-кода от бизнеса
     * @return businessId или null если не валидный
     */
    public Long parseBusinessQr(String qrData) {
        if (qrData == null || !qrData.startsWith("business:")) {
            return null;
        }
        try {
            return Long.parseLong(qrData.substring("business:".length()));
        } catch (NumberFormatException e) {
            log.warn("Invalid business QR data: {}", qrData);
            return null;
        }
    }

    /**
     * Базовая генерация QR-кода
     */
    private byte[] generateQrCode(String data) throws IOException, WriterException {
        BitMatrix matrix = new MultiFormatWriter()
            .encode(data, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
        
        return outputStream.toByteArray();
    }

    /**
     * Сгенерировать QR-код с кастомными размерами
     */
    public byte[] generateQrCodeWithSize(String data, int width, int height) 
            throws IOException, WriterException {
        BitMatrix matrix = new MultiFormatWriter()
            .encode(data, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
        
        return outputStream.toByteArray();
    }
}
