package com.fx.pan.config.threadPool;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


/**
 * @author leaving
 * @date 2022/4/28 22:21
 * @version 1.0
 */

@Configuration
@EnableAsync
public class ThreadConfig implements AsyncConfigurer {

    @Bean("taskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        // ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // executor.setCorePoolSize(20);//核心线程数目
        // executor.setMaxPoolSize(Integer.MAX_VALUE);//最大线程数目
        // executor.setQueueCapacity(20);//队列中最大的数目
        // executor.initialize();
        // return executor;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        executor.setCorePoolSize(10);
        //线程池维护线程的最大数量
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        //允许的空闲时间
        executor.setKeepAliveSeconds(60);
        // 线程池名称
        executor.setThreadNamePrefix("taskExecutor-");
        // WaitForTasksToCompleteOnShutdown=true（默认为false），表明等待所有线程执行完
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //主线程会处于一种等待的状态，等待线程池中所有的线程都运行完毕后才继续运行
        executor.setAwaitTerminationSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }

}
