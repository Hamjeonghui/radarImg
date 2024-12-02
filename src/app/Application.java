package app;

import app.image.ImageDownloader;
import app.log.LogCleaner;
import app.log.LoggerConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static app.image.ImageDelete.deleteOldImages;
import static app.image.ImageDownloader.downloadImage;

public class Application {
    private static final Logger logger = LoggerConfig.createLogger(ImageDownloader.class.getName());
    private static String basePath = "D:" + File.separator + "WEBDATA" + File.separator + "RADAR" + File.separator;
    private static String apiUrl = "http://global.amo.go.kr/radar/cgi-bin/nph-rdr_cmp_img?";

    public static void main(String[] args) {

        logger.info("스케줄러 실행 >>>>>>>>>>");

        // ScheduledExecutorService 생성
        int th=1; //스레드 수
        int interval=2; //실행 주기
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(th);

        // 7일 전 로그 삭제
        scheduler.scheduleAtFixedRate(() -> {
            LogCleaner.deleteOldLogs("logs");
        }, 0, 1, TimeUnit.DAYS); // 매일 한 번 실행

        // ScheduledExecutorService 동작
        scheduler.scheduleAtFixedRate(() -> {
            String cmp="HSR";
            String tm = getPreviousFiveMinuteTime();
            String size="1000";

            String imageUrl = apiUrl +
                    "&cmp="+ cmp +
                    "&obs=ECHO" +
                    "&color=C4" +
                    "&ZRa=148" +
                    "&ZRb=1.59" +
                    "&title=1" +
                    "&legend=1" +
                    "&lonlat=0" +
                    "&center=0" +
                    "&topo=0" +
                    "&typ=0" +
                    "&wv=0" +
                    "&aws=01" +
                    "&wt=0" +
                    "&gov=KMA" +
                    "&x1=-10" +
                    "&y1=-10" +
                    "&x2=-10" +
                    "&y2=-10" +
                    "&fir=0" +
                    "&routes=0" +
                    "&qcd=EXT" +
                    "&tm=" + tm +
                    "&map=HR" +
                    "&runway=ICN" +
                    "&xp=477.92602012643" +
                    "&yp=477.92602012643" +
                    "&lat=37.461854" +
                    "&lon=126.440609"+
                    "&zoom=17.0667" +
                    "&size=" + size;

                    String fileName= cmp + "_" + tm + ".jpg";

            // 동적 경로 생성
            String yearMonth = tm.substring(0, 6); // YYYYMM
            String day = tm.substring(6, 8); // DD
            String filePath=basePath + yearMonth + File.separator + day + File.separator;

            try {
                downloadImage(imageUrl, filePath, fileName);
                deleteOldImages(filePath);
            } catch (Exception e) {
                logger.severe("스케줄러 실행 오류: " + e.getMessage());
            }
        }, 0, interval, TimeUnit.MINUTES);

        // 애플리케이션 종료 시 스케줄러 종료 (옵션)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("스케줄러 종료 >>>>>>>>>>");
            scheduler.shutdown();
        }));
    }

    //이미지 조회 가능 시간(5의 배수로 내림처리 - 5분)으로 포맷팅
    public static String getPreviousFiveMinuteTime() {
        // 현재 시간 가져오기
        Calendar now = Calendar.getInstance();

        // 분 단위를 5의 배수로 내림 처리
        int minute = now.get(Calendar.MINUTE);
        int roundedMinute = (minute / 5) * 5;
        now.set(Calendar.MINUTE, roundedMinute);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        // 5분을 이전으로 이동
        now.add(Calendar.MINUTE, -5);

        // 포맷팅 (yyyyMMddHHmm)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        return formatter.format(now.getTime());
    }
}
