package com.app.msql;

import com.app.thread.AsyncCallback;
import com.app.thread.DataCallback;
import com.app.thread.Executor;

public class AsyncExecutor extends Executor {
    private AsyncCallback cb;
    private boolean isAsync = false;

    public void asyncListen(AsyncCallback callback) {
        this.cb = callback;
        this.isAsync = true;
        execute();
    }

    public void listen(AsyncCallback callback) {
        this.cb = callback;
        this.isAsync = false;
        execute();
    }

    void submit(Runnable runnable) {
        this.runTask = runnable;
    }

    void call() {
        if (cb != null) {
            if (isAsync) {
                cb.finish();
            } else {
                Executor.handler(() -> cb.finish());
            }
        }
    }
}
