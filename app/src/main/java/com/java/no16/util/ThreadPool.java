package com.java.no16.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class ThreadPool {
    public static ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(6);
    public static void clearExecut(){
        executor.getQueue().clear();
    }
}
