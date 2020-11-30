package com.app.msql;

import android.database.sqlite.SQLiteDatabase;

import com.app.data.ObjectReflect;
import com.app.db.SQLiteRelevance;

import java.util.List;

public abstract class MinorSQL implements IMinorSQL {
    protected final String databaseName;
    protected String tableName;

    MinorSQL(String databaseName) {
        this.databaseName = databaseName;
    }

    public MinorSQLHandler table(String tableName) {
        this.tableName = tableName;
        return (MinorSQLHandler) this;
    }

    public MinorSQLHandler model(String modelClass) {
        setTableName(modelClass);
        return (MinorSQLHandler) this;
    }

    public <T> MinorSQLHandler model(Class<T> modelClass) {
        if (modelClass != null) {
            setTableName(modelClass.getName());
        }
        return (MinorSQLHandler) this;
    }

    /**
     * 注册一个新的临时数据表
     *
     * @param modelClass 注册的类
     * @param tableTag   表的标记或者名称
     * @return 返回的为新注册表的数据库名称
     */
    public String registerTempTable(String modelClass, String tableTag) {
        ObjectReflect reflect = MSQLHelper.getReflect().get(modelClass);
        if (reflect != null) {
            SQLiteDatabase database = null;
            try {
                database = MSQLHelper.getSQLiteHelper(databaseName).getWritableDatabase();
                return SQLiteRelevance.findTableByTag(database, reflect.getCreateTable(), modelClass, tableTag);
            } finally {
                SQLiteUtils.closeDatabase(database);
            }
        }
        return null;
    }

    /**
     * 注册一个新的临时数据表
     *
     * @param modelClass 注册的类
     * @param tableTag   表的标记或者名称
     * @return 返回的为新注册表的数据库名称
     */
    public <T> String registerTempTable(Class<T> modelClass, String tableTag) {
        return registerTempTable(modelClass.getName(), tableTag);
    }

    public MinorSQLHandler tempTable(String modelClass, String tableTag) {
        return table(registerTempTable(modelClass, tableTag));
    }

    public <T> MinorSQLHandler tempTable(Class<T> modelClass, String tableTag) {
        return table(registerTempTable(modelClass, tableTag));
    }

    public AsyncDataExecutor<MinorSQLHandler> tempTableAsync(String modelClass, String tableTag) {
        AsyncDataExecutor<MinorSQLHandler> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(table(registerTempTable(modelClass, tableTag)));
            }
        });
        return executor;
    }

    public <T> AsyncDataExecutor<MinorSQLHandler> tempTableAsync(Class<T> modelClass, String tableTag) {
        AsyncDataExecutor<MinorSQLHandler> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(table(registerTempTable(modelClass, tableTag)));
            }
        });
        return executor;
    }

    /**
     * 重新创建数据表
     */
    public void rebuildTable(String modelClass) {
        try {
            ObjectReflect reflect = MSQLHelper.getReflect().get(modelClass);
            if (reflect != null) {
                if (tableName == null) {
                    tableName = reflect.getDefaultTable();
                }
                String sql = String.format(reflect.getCreateTable(), tableName, tableName);
                SQLiteUtils.rebuildTable(databaseName, tableName, sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void rebuildTable(Class<T> modelClass) {
        rebuildTable(modelClass.getName());
    }

    private void setTableName(String modelClass) {
        if (modelClass != null) {
            try {
                ObjectReflect reflect = MSQLHelper.getReflect().get(modelClass);
                if (reflect != null) {
                    tableName = reflect.getDefaultTable();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void execSQL(String sql, Object[] bindArgs) {
        SQLiteDatabase database = null;
        try {
            database = MSQLHelper.getSQLiteHelper(databaseName).getWritableDatabase();
            database.execSQL(sql, bindArgs);
        } finally {
            SQLiteUtils.closeDatabase(database);
        }
    }

    public void execSQL(String sql) {
        SQLiteDatabase database = null;
        try {
            database = MSQLHelper.getSQLiteHelper(databaseName).getWritableDatabase();
            database.execSQL(sql);
        } finally {
            SQLiteUtils.closeDatabase(database);
        }
    }

    public AsyncExecutor execSQLAsync(String sql, Object[] bindArgs) {
        AsyncExecutor executor = new AsyncExecutor();
        executor.submit(() -> {
            synchronized (Object.class) {
                execSQL(sql, bindArgs);
                executor.call();
            }
        });
        return executor;
    }

    public AsyncExecutor execSQLAsync(String sql) {
        AsyncExecutor executor = new AsyncExecutor();
        executor.submit(() -> {
            synchronized (Object.class) {
                execSQL(sql);
                executor.call();
            }
        });
        return executor;
    }


    @Override
    public <T> long insertOrThrow(T... array) {
        return SQLiteUtils.insertOrThrow(databaseName, tableName, array);
    }

    @Override
    public <T> long insert(T... array) {
        try {
            return insertOrThrow(array);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public <T> long insertOrThrow(List<T> list) {
        return SQLiteUtils.insertOrThrow(databaseName, tableName, list);
    }

    @Override
    public <T> long insert(List<T> list) {
        try {
            return insertOrThrow(list);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public <T> long replaceOrThrow(T... array) {
        return SQLiteUtils.replaceOrThrow(databaseName, tableName, array);
    }

    @Override
    public <T> long replace(T... array) {
        try {
            return replaceOrThrow(array);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public <T> long replaceOrThrow(List<T> list) {
        return SQLiteUtils.replaceOrThrow(databaseName, tableName, list);
    }

    @Override
    public <T> long replace(List<T> list) {
        try {
            return replaceOrThrow(list);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public <T> int deleteOrThrow(T... array) {
        return SQLiteUtils.deleteOrThrow(databaseName, tableName, array);
    }

    @Override
    public <T> int delete(T... array) {
        try {
            return deleteOrThrow(array);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public <T> int deleteOrThrow(List<T> list) {
        return SQLiteUtils.deleteOrThrow(databaseName, tableName, list);
    }

    @Override
    public <T> int delete(List<T> list) {
        try {
            return deleteOrThrow(list);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public <T> int updateOrThrow(T... array) {
        return SQLiteUtils.updateOrThrow(databaseName, tableName, array);
    }

    @Override
    public <T> int update(T... array) {
        try {
            return updateOrThrow(array);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public <T> int updateOrThrow(List<T> list) {
        return SQLiteUtils.updateOrThrow(databaseName, tableName, list);
    }

    @Override
    public <T> int update(List<T> list) {
        try {
            return updateOrThrow(list);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    @Override
    public <T> AsyncDataExecutor<Long> insertOrThrowAsync(T... array) {
        AsyncDataExecutor<Long> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(insertOrThrow(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Long> insertAsync(T... array) {
        AsyncDataExecutor<Long> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(insert(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Long> insertOrThrowAsync(List<T> list) {
        AsyncDataExecutor<Long> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(insertOrThrow(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Long> insertAsync(List<T> list) {
        AsyncDataExecutor<Long> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(insert(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Long> replaceOrThrowAsync(T... array) {
        AsyncDataExecutor<Long> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(replaceOrThrow(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Long> replaceAsync(T... array) {
        AsyncDataExecutor<Long> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(replace(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Long> replaceOrThrowAsync(List<T> list) {
        AsyncDataExecutor<Long> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(replaceOrThrow(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Long> replaceAsync(List<T> list) {
        AsyncDataExecutor<Long> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(replace(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> deleteOrThrowAsync(T... array) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(deleteOrThrow(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> deleteAsync(T... array) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(delete(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> deleteOrThrowAsync(List<T> list) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(deleteOrThrow(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> deleteAsync(List<T> list) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(delete(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> updateOrThrowAsync(T... array) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(updateOrThrow(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> updateAsync(T... array) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(update(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> updateOrThrowAsync(List<T> list) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(updateOrThrow(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> updateAsync(List<T> list) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(update(list));
            }
        });
        return executor;
    }
}
