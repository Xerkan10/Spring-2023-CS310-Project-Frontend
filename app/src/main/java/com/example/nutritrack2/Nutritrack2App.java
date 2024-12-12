package com.example.nutritrack2;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Nutritrack2App extends Application {
    ExecutorService srv = Executors.newCachedThreadPool();

    public ExecutorService getExecutorService() {
        return srv;
    }
}
