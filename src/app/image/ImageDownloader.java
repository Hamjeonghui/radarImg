package app.image;

import app.Application;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class ImageDownloader {
    private static final Logger logger = Application.logger;

    public static void downloadImage(String imageUrl, String directoryPath, String fileName) {
        // 파일 경로 생성
        String filePath = directoryPath + File.separator + fileName;
        File destinationFile = new File(filePath);

        // 이미 파일이 존재하는지 확인
        if (destinationFile.exists()) {
            logger.fine("이미 저장한 파일: " + fileName);
            return;
        }

        try {
            // 디렉토리 확인 및 생성
            File directory = new File(directoryPath);
            if (!directory.exists() && directory.mkdirs()) {
                logger.fine("디렉토리 생성: " + directory.getAbsolutePath());
            }

            // URL 객체 생성
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // HTTP 응답 코드 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 입력 스트림 생성
                try (InputStream inputStream = connection.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

                    // 버퍼를 사용해 파일 쓰기
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                logger.info("이미지 저장 성공: " + filePath);
            } else {
                logger.severe("HTTP 요청 실패. 응답 코드: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            logger.severe("이미지 다운로드 중 오류 발생: " + e.getMessage());
        }
    }
}