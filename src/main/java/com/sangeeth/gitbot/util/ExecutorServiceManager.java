package com.sangeeth.gitbot.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dtsangeeth
 * @created 14 / 04 / 2020
 * @project GitComponentManager
 */
public class ExecutorServiceManager {

    public static final ExecutorService executorService = Executors.newSingleThreadExecutor();
}
