package com.fx.pan.utils;

import com.fx.pan.common.Constants;
import com.fx.pan.domain.FileBean;
import com.fx.pan.factory.FxUtils;
import com.fx.pan.service.FileService;
import com.fx.pan.utils.file.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * 离线下载服务工具 使用了wget下载器
 *
 * @author leaving
 * @date 2022/5/7 9:02
 * @version 1.0
 */

@Component
public class WgetUtil {

    @Value("${fx.storageType}")
    private Integer storageType;

    private static String wgetDir;

    @Value("${fx.wgetDir}")
    public void setWgetDir(String wgetDir) {
        this.wgetDir = wgetDir;
    }

    @Autowired
    private FileService fs;

    private static FileService fileService;

    public @PostConstruct
    void init1() {
        fileService = fs;
    }

    private static RedisCache redisCache;

    @Autowired
    private RedisCache rc;

    public @PostConstruct
    void init() {
        redisCache = rc;
    }

    /**
     * wget
     *
     * @param option      wget参数
     * @param url         下载地址
     * @param logConsumer 输出日志消费者
     * @throws IOException
     * @throws InterruptedException
     * @throws RuntimeException     wget执行异常
     */
    public static void wget(String option, String url, Consumer<String> logConsumer) throws IOException,
            InterruptedException {
        option = option != null ? option + " " : "";
        String cmd = "wget " + option + url;
        System.out.println("cmd: " + cmd);
        //执行cmd命令
        File dir = new File(wgetDir);
        String command = "c:\\windows\\system32\\cmd.exe /c " + cmd;
        Runtime r = Runtime.getRuntime();
        Process process = r.exec(command, null, dir);
        String lastMsg = reader(process.getErrorStream(), logConsumer);
        if (0 != process.waitFor()) {
            throw new RuntimeException("wget执行异常：" + lastMsg);
        }
    }

    /**
     * wget 百分比进度输出（每个百分比只输出一条）
     *
     * @param downloadPath     wget参数
     * @param url              下载地址
     * @param progressConsumer 下载进度消费者
     * @throws IOException
     * @throws InterruptedException
     */
    @Async("taskExecutor")
    public void wgetProgressRation(String downloadPath, String fileSavePath, String fileName, String url, Long t,
                                   Long userId,
                                   String type,
                                   Consumer<Progress> progressConsumer) throws Exception {
        Map<String, Boolean> progressRationMap = new HashMap<>(135); // 101 / 0.75 + 1
        // https://mirrors.aliyun.com/apache/accumulo/2.0.1/accumulo-2.0.1-src.tar.gz
        String option = "-O " + downloadPath;
        wget(option, url, line -> {
            if (line.contains("%")) {
                // 格式：12900K .......... .......... .......... .......... ..........  2% 5.49M 9m38s
                String progressRation = line.replaceAll(".* ([0-9]{1,3})% .*", "$1");
                if (progressRationMap.putIfAbsent(progressRation, true) == null) {
                    progressConsumer.accept(new Progress(progressRation, line));
                    redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId +
                                    ":" + t,
                            Integer.parseInt(progressRation));
                }
            } else if (line.contains("saved")) {
                progressConsumer.accept(new Progress("100", "文件已完全下载(无需下载)"));
                File file = new File(downloadPath);
                Date date = new Date();
                String dateStr = DateUtil.formatDate(date, "yyyyMMdd");
                String md5 = null;
                try {
                    md5 = Md5Utils.md5HashCode32(downloadPath);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                // 获取文本扩展名
                String fileUrl = dateStr + "/" + md5 + "." + FileUtils.getFileExt(fileName);
                FileBean fileBean = new FileBean();
                try {
                    // System.out.println("file.getAbsolutePath():" + file.getAbsolutePath());
                    fileBean = BeanCopyUtils.copyBean(FileUtils.getFileBeanByPath(file.getAbsolutePath(),
                            fileBean.getFilePath(), new Date(t), storageType,
                            userId), FileBean.class);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                fileBean.setFileName(fileName);
                fileBean.setOrigin(2);
                fileService.save(fileBean);
                redisCache.setCacheObject(Constants.REDIS_DATA_SUFFIX + "-" + type + "-" + userId + "-file" +
                                ":" + t,
                        fileBean);
                if (fileBean.getFileType() == 1 || fileBean.getFileType() == 2) {
                    try {
                        ImageUtil.startGenerateThumbnail(FxUtils.getStaticPath() + "/" + fileUrl, fileBean, true,
                                0.3);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("文件最终保存路径" + fileSavePath + "/" + fileUrl);
                file.renameTo(new File(fileSavePath + "/" + fileUrl));
            }
        });
    }

    private static String reader(InputStream inputStream, Consumer<String> consumer) throws IOException {
        AtomicReference<String> lastMsg = new AtomicReference<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            bufferedReader.lines().filter(line -> !line.isEmpty()).forEach(line -> {
                lastMsg.set(line);
                if (consumer != null) {
                    consumer.accept(line);
                }
            });
        }
        return lastMsg.get();
    }

    public static class Progress {
        private String progress;
        private String log;

        public Progress(String progress, String log) {
            this.progress = progress;
            this.log = log;
        }

        public String toJSONString() {
            return "{\"progress\":" + progress + ", \"log\":\"" + log + "\"}";
        }
    }
}
