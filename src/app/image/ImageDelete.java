package app.image;

import app.Application;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class ImageDelete {
    private static final Logger logger = Application.logger;
    private static final String FILE_NAME_DATE_FORMAT = "yyyyMMddHHmm"; // 파일 이름에 포함된 날짜 형식
    private static final long SEVEN_DAYS_IN_MILLISECONDS = 7L * 24 * 60 * 60 * 1000; // 7일(밀리초)

    public static void deleteOldImages(String filePath) {
        // 디렉토리 객체 생성
        File directory = new File(filePath);

        if (!directory.exists() || !directory.isDirectory()) {
            logger.severe("유효하지 않은 디렉토리: " + filePath);
            return;
        }

        // 디렉토리 내 파일 리스트 가져오기
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            logger.fine("디렉토리에 파일 없음: " + filePath);
            return;
        }

        // 파일들 확인 및 삭제
        for (File file : files) {
            if (file.isFile() && isOldFile(file)) {
                try {
                    if (file.delete()) {
                        logger.fine("파일 삭제 성공: " + file.getName());
                    } else {
                        logger.fine("파일 삭제 실패: " + file.getName());
                    }
                } catch (Exception e) {
                    logger.severe("파일 삭제 중 오류 발생: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isOldFile(File file) {
        String fileName = file.getName();

        try {
            // 파일 이름에서 시간 정보 추출 (예: cmp_yyyyMMddHHmm.jpg)
            int underscoreIndex = fileName.indexOf('_');
            int dotIndex = fileName.lastIndexOf('.');
            if (underscoreIndex == -1 || dotIndex == -1 || dotIndex <= underscoreIndex) {
                logger.severe("파일 이름 형식이 올바르지 않습니다: " + fileName);
                return false; // 예상된 형식이 아님
            }

            // 시간 정보 추출 및 파싱
            String datePart = fileName.substring(underscoreIndex + 1, dotIndex);
            SimpleDateFormat dateFormat = new SimpleDateFormat(FILE_NAME_DATE_FORMAT);
            Date fileDate = dateFormat.parse(datePart);

            // 현재 시간과 파일 생성 시간 비교
            long currentTime = System.currentTimeMillis();
            long fileTime = fileDate.getTime();
            return (currentTime - fileTime) > SEVEN_DAYS_IN_MILLISECONDS;

        } catch (ParseException e) {
            logger.severe("파일 이름에서 시간 정보 파싱 실패: " + fileName);
            return false;
        }
    }
}
