package app.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LoggerConfig {

    public static Logger createLogger(String loggerName) {
        Logger logger = Logger.getLogger(loggerName);

        try {
            // 현재 날짜 가져오기
            Date now = new Date();
            String yearMonth = new SimpleDateFormat("yyyyMM").format(now); // yyyyMM
            String day = new SimpleDateFormat("dd").format(now);          // dd
            String logBasePath = "logs" + File.separator + yearMonth + File.separator + day + File.separator;

            // 로그 디렉토리 확인 및 생성
            File logDirectory = new File(logBasePath);
            if (!logDirectory.exists() && logDirectory.mkdirs()) {
                System.out.println("로그 디렉토리 생성: " + logDirectory.getAbsolutePath());
            }

            // 로그 설정 초기화
            LogManager.getLogManager().reset();
            logger.setLevel(Level.ALL);

            // 콘솔 핸들러 추가
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            // 파일 핸들러 추가
            FileHandler debugFileHandler = createFileHandler(logBasePath + "debug.log", Level.FINE);
            logger.addHandler(debugFileHandler);

            FileHandler errorFileHandler = createFileHandler(logBasePath + "error.log", Level.SEVERE);
            logger.addHandler(errorFileHandler);

        } catch (IOException e) {
            System.err.println("로거 설정 중 오류 발생: " + e.getMessage());
        }

        return logger;
    }

    private static FileHandler createFileHandler(String path, Level level) throws IOException {
        FileHandler fileHandler = new FileHandler(path, true);
        fileHandler.setLevel(level);
        fileHandler.setFormatter(new SimpleFormatter());
        return fileHandler;
    }
}