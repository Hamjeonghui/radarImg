package app.log;

import app.Application;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class LogCleaner {
    private static final Logger logger = Application.logger;

    public static void deleteOldLogs(String logBasePath) {
        try {
            // 현재 날짜에서 7일 전 계산
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            Date sevenDaysAgo = calendar.getTime();

            // 기준 날짜 로그 디렉토리 확인
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String thresholdDate = dateFormat.format(sevenDaysAgo);

            File logDir = new File(logBasePath);
            if (!logDir.exists() || !logDir.isDirectory()) {
                logger.warning("유효하지 않은 로그 경로: " + logBasePath);
                return;
            }

            // 로그 디렉토리 탐색 및 오래된 파일 삭제
            for (File yearMonthDir : logDir.listFiles()) {
                if (yearMonthDir.isDirectory()) {
                    boolean yearMonthDirEmpty = true; // 해당 년-월 폴더가 비었는지 확인

                    for (File dayDir : yearMonthDir.listFiles()) {
                        if (dayDir.isDirectory()) {
                            String dirName = yearMonthDir.getName() + dayDir.getName(); // yyyyMMdd 형식 생성
                            if (dirName.compareTo(thresholdDate) < 0) {
                                deleteDirectory(dayDir);
                                logger.info("오래된 로그 디렉토리 삭제: " + dayDir.getAbsolutePath());
                            } else {
                                yearMonthDirEmpty = false; // 아직 유효한 로그가 있으면 비어 있지 않음
                            }
                        }
                    }

                    // 년-월 폴더가 비었으면 삭제
                    if (yearMonthDirEmpty && yearMonthDir.listFiles().length == 0) {
                        deleteDirectory(yearMonthDir);
                        logger.info("빈 로그 년-월 디렉토리 삭제: " + yearMonthDir.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("오래된 로그 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    private static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                deleteDirectory(file);
            }
        }
        if (directory.delete()) {
            logger.fine("삭제 성공: " + directory.getAbsolutePath());
        } else {
            logger.warning("삭제 실패: " + directory.getAbsolutePath());
        }
    }
}