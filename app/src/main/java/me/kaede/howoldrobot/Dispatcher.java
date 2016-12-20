/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot;

import android.os.Handler;

import moe.studio.dispatcher.ExecutorDispatcher;
import moe.studio.dispatcher.Task;

/**
 * @author Kaede
 * @version 16/12/19
 */
public class Dispatcher implements Task.Dispatcher {

    private static Dispatcher sInstance = new Dispatcher();

    public static Dispatcher instance() {
        return sInstance;
    }

    private final ExecutorDispatcher mDispatcher;

    private Dispatcher() {
        mDispatcher = Task.Dispatchers.newExecutorDispatcher(2);
    }

    @Override
    public Task.Dispatcher attach(Handler scheduler) {
        return mDispatcher.attach(scheduler);
    }

    @Override
    public void start() {
        mDispatcher.start();
    }

    @Override
    public boolean isRunning() {
        return mDispatcher.isRunning();
    }

    @Override
    public void post(Runnable runnable) {
        mDispatcher.post(runnable);
    }

    @Override
    public void post(int what, Runnable runnable) {
        mDispatcher.post(what, runnable);
    }

    @Override
    public void postDelay(Runnable runnable, long millis) {
        mDispatcher.postDelay(runnable, millis);
    }

    @Override
    public void postDelay(int what, Runnable runnable, long millis) {
        mDispatcher.postDelay(what, runnable, millis);
    }

    @Override
    public boolean has(int what) {
        return mDispatcher.has(what);
    }

    @Override
    public boolean has(Runnable runnable) {
        return mDispatcher.has(runnable);
    }

    @Override
    public void finish(Runnable runnable) {
        mDispatcher.finish(runnable);
    }

    @Override
    public void shutdown() {
        mDispatcher.shutdown();
    }
}
