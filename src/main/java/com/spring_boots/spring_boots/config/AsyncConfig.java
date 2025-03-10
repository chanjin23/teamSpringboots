package com.spring_boots.spring_boots.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    private final int coreCount = Runtime.getRuntime().availableProcessors();

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        // corePoolSize: 기본적으로 유지되는 쓰레드 수
        taskExecutor.setCorePoolSize(coreCount * 2);

        // maxPoolSize: 최대 쓰레드 수
        taskExecutor.setMaxPoolSize(coreCount * 4);

        // queueCapacity: 작업을 대기시킬 큐의 크기
        taskExecutor.setQueueCapacity(100);

        // 쓰레드 이름 접두사 설정
        taskExecutor.setThreadNamePrefix("Executor-");

        // 종료 시 모든 쓰레드를 종료하기 위한 설정
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);

        // 애플리케이션 종료 전에 대기하는 시간 설정
        taskExecutor.setAwaitTerminationSeconds(60);

        return taskExecutor;
    }
}
