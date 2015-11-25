package com.ibm.nrdaemon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Cloud on 19/11/2015.
 */
public class ThreadPool {

    public static void main(String [] args) throws InterruptedException {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable worker = new Daemon();
        executor.scheduleAtFixedRate(worker, 0, 40, TimeUnit.SECONDS);
    }
}
