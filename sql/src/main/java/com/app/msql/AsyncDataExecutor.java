package com.app.msql;

import com.app.thread.DataCallback;
import com.app.thread.Executor;

public class AsyncDataExecutor<T> extends Executor {
    private DataCallback<T> cb;
    private boolean isAsync = false;

    public void asyncListen(DataCallback<T> callback) {
        this.cb = callback;
        this.isAsync = true;
        execute();
    }

    public void listen(DataCallback<T> callback) {
        this.cb = callback;
        this.isAsync = false;
        execute();
    }

    void submit(Runnable runnable) {
        this.runTask = runnable;
    }

    void call(T t) {
        if (cb != null) {
            if (isAsync) {
                cb.finish(t);
            } else {
                Executor.handler(() -> cb.finish(t));
            }
        }
    }
}
