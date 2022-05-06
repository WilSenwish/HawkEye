package com.littleyes.manager.util;

import com.littleyes.alarm.core.AlarmManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ForkJoinPool;

/**
 * <p> <b> 预警任务启动器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-08-17
 */
@Component
public class AlarmScheduleRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        ForkJoinPool.commonPool().execute(AlarmManager::schedule);
    }
}
