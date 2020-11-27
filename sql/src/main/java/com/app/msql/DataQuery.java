package com.app.msql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.data.ObjectReflect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class DataQuery extends MinorWhere implements IDataQuery {

    public DataQuery(String databaseName, String table) {
        super(databaseName, table);
    }

    private <T> T findMove(String modelClass, String sql, boolean isMoveToLast) {
        ObjectReflect reflect = MSQLHelper.getReflect().get(modelClass);
        Object data = null;
        if (reflect != null) {
            SQLiteDatabase database = null;
            Cursor query = null;
            try {
                database = getDatabase();
                query = database.rawQuery(sql, selectionArgs());
                if (isMoveToLast) {
                    if (query.moveToLast()) {
                        data = reflect.invokeCursorToData(query);
                    }
                } else {
                    if (query.moveToFirst()) {
                        data = reflect.invokeCursorToData(query);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                SQLiteUtils.closeCursor(query);
                SQLiteUtils.closeDatabase(database);
            }
        }
        return (T) data;
    }

    protected abstract String conditions();

    protected abstract String[] selectionArgs();

    @Override
    public <T> T findLast(Class<T> modelClass) {
        return findLast(modelClass.getName());
    }

    @Override
    public <T> T findFirst(Class<T> modelClass) {
        return findFirst(modelClass.getName());
    }

    @Override
    public <T> List<T> find(Class<T> modelClass) {
        return find(modelClass.getName());
    }

    @Override
    public <T> T findLast(String modelClass) {
        return findMove(modelClass, conditions(), true);
    }

    @Override
    public <T> T findFirst(String modelClass) {
        return findMove(modelClass, conditions(), false);
    }

    @Override
    public <T> List<T> find(String modelClass) {
        return find(modelClass, conditions());
    }

    @Override
    public <T> AsyncDataExecutor<T> findLastAsync(Class<T> modelClass) {
        AsyncDataExecutor<T> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(findLast(modelClass));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<T> findFirstAsync(Class<T> modelClass) {
        AsyncDataExecutor<T> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(findFirst(modelClass));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<List<T>> findAsync(Class<T> modelClass) {
        AsyncDataExecutor<List<T>> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(find(modelClass));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<T> findLastAsync(String modelClass) {
        AsyncDataExecutor<T> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(findLast(modelClass));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<T> findFirstAsync(String modelClass) {
        AsyncDataExecutor<T> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(findFirst(modelClass));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<List<T>> findAsync(String modelClass) {
        AsyncDataExecutor<List<T>> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(find(modelClass));
            }
        });
        return executor;
    }

    private <T> List<T> find(String modelClass, String sql) {
        ObjectReflect reflect = MSQLHelper.getReflect().get(modelClass);
        List<T> list = new ArrayList<>();
        if (reflect != null) {
            SQLiteDatabase database = null;
            Cursor query = null;
            try {
                database = getDatabase();
                query = database.rawQuery(sql, selectionArgs());
                reflect.invokeCursorToList(list, query);
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                SQLiteUtils.closeCursor(query);
                SQLiteUtils.closeDatabase(database);
            }
        }
        return list;
    }

    @Override
    public int count() {
        SQLiteDatabase database = null;
        Cursor query = null;
        try {
            database = getDatabase();
            query = database.rawQuery(conditions(), selectionArgs());
            return query.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            SQLiteUtils.closeCursor(query);
            SQLiteUtils.closeDatabase(database);
        }
    }

    @Override
    public AsyncDataExecutor<Integer> countAsync() {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(count());
            }
        });
        return executor;
    }
}
