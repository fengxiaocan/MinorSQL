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

    /**
     * 选择数据表的模型
     *
     * @param tableName 表的名字
     * @return
     */
    public MinorSQLHandler table(String tableName) {
        this.tableName = tableName;
        return (MinorSQLHandler) this;
    }

    /**
     * 选择数据表的模型,会自动查找默认表名
     *
     * @param modelClass
     * @return
     */
    public MinorSQLHandler model(String modelClass) {
        setTableName(modelClass);
        return (MinorSQLHandler) this;
    }

    /**
     * 选择数据表的模型,会自动查找默认表名
     *
     * @param modelClass
     * @param <T>
     * @return
     */
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

    /**
     * 注册一个临时表
     *
     * @param modelClass 表的模型
     * @param tableTag   注册的表的标记
     * @return
     */
    public MinorSQLHandler tempTable(String modelClass, String tableTag) {
        return table(registerTempTable(modelClass, tableTag));
    }

    /**
     * 注册一个临时表
     *
     * @param modelClass 表的模型
     * @param tableTag   注册的表的标记
     * @return
     */
    public <T> MinorSQLHandler tempTable(Class<T> modelClass, String tableTag) {
        return table(registerTempTable(modelClass, tableTag));
    }

    /**
     * 在异步线程中注册一个临时表
     *
     * @param modelClass 表的模型
     * @param tableTag   注册的表的标记
     * @return
     */
    public AsyncDataExecutor<MinorSQLHandler> tempTableAsync(String modelClass, String tableTag) {
        AsyncDataExecutor<MinorSQLHandler> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(table(registerTempTable(modelClass, tableTag)));
            }
        });
        return executor;
    }

    /**
     * 在异步线程中注册一个临时表
     *
     * @param modelClass 表的模型
     * @param tableTag   注册的表的标记
     * @return
     */
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
     * 重新构建数据表
     *
     * @param modelClass 构建的表的映射模型
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

    /**
     * 重新构建数据表
     *
     * @param modelClass 构建的表的映射模型
     * @param <T>
     */
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

    /**
     * 运行SQL语句
     *
     * @param sql
     * @param bindArgs
     */
    public void execSQL(String sql, Object[] bindArgs) {
        SQLiteDatabase database = null;
        try {
            database = MSQLHelper.getSQLiteHelper(databaseName).getWritableDatabase();
            database.execSQL(sql, bindArgs);
        } finally {
            SQLiteUtils.closeDatabase(database);
        }
    }

    /**
     * 运行SQL语句
     *
     * @param sql
     */
    public void execSQL(String sql) {
        SQLiteDatabase database = null;
        try {
            database = MSQLHelper.getSQLiteHelper(databaseName).getWritableDatabase();
            database.execSQL(sql);
        } finally {
            SQLiteUtils.closeDatabase(database);
        }
    }

    /**
     * 异步运行SQL语句
     *
     * @param sql
     * @param bindArgs
     */
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

    /**
     * 异步运行SQL语句
     *
     * @param sql
     */
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

    /**
     * 插入新数据,会抛出异常
     *
     * @param array
     * @param <T>
     * @return
     */
    @Override
    public <T> int insertOrThrow(T... array) {
        return SQLiteUtils.insertOrThrow(databaseName, tableName, array);
    }

    /**
     * 插入新数据,不会抛出异常
     *
     * @param array
     * @param <T>
     * @return
     */
    @Override
    public <T> int insert(T... array) {
        return SQLiteUtils.insert(databaseName, tableName, array);
    }

    /**
     * 插入新数据,会抛出异常
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> int insertOrThrow(List<T> list) {
        return SQLiteUtils.insertOrThrow(databaseName, tableName, list);
    }

    /**
     * 插入新数据,不会抛出异常
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> int insert(List<T> list) {
        return SQLiteUtils.insert(databaseName, tableName, list);
    }

    /**
     * 替换或者插入数据,会抛出异常
     *
     * @param array
     * @param <T>
     * @return
     */
    @Override
    public <T> int replaceOrThrow(T... array) {
        return SQLiteUtils.replaceOrThrow(databaseName, tableName, array);
    }

    /**
     * 替换或者插入数据,不会抛出异常
     *
     * @param array
     * @param <T>
     * @return
     */
    @Override
    public <T> int replace(T... array) {
        return SQLiteUtils.replace(databaseName, tableName, array);
    }

    /**
     * 替换或者插入数据,会抛出异常
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> int replaceOrThrow(List<T> list) {
        return SQLiteUtils.replaceOrThrow(databaseName, tableName, list);
    }

    /**
     * 替换或者插入数据,不会抛出异常
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> int replace(List<T> list) {
        return SQLiteUtils.replace(databaseName, tableName, list);
    }

    /**
     * 以ID为条件删除数据,会抛出异常
     *
     * @param array
     * @param <T>
     * @return
     */
    @Override
    public <T> int deleteOrThrow(T... array) {
        return SQLiteUtils.deleteOrThrow(databaseName, tableName, array);
    }

    /**
     * 以ID为条件删除数据,不会抛出异常
     *
     * @param array
     * @param <T>
     * @return
     */
    @Override
    public <T> int delete(T... array) {
        return SQLiteUtils.deleteOrThrow(databaseName, tableName, array);
    }

    /**
     * 以ID为条件删除数据,会抛出异常
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> int deleteOrThrow(List<T> list) {
        return SQLiteUtils.deleteOrThrow(databaseName, tableName, list);
    }

    /**
     * 以ID为条件删除数据,不会抛出异常
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> int delete(List<T> list) {
        return SQLiteUtils.delete(databaseName, tableName, list);
    }

    /**
     * 以id为字段来更新数据,有错误会抛出异常
     *
     * @param array
     * @param <T>
     * @return
     */
    @Override
    public <T> int updateOrThrow(T... array) {
        return SQLiteUtils.updateOrThrow(databaseName, tableName, array);
    }

    /**
     * 以id为字段来更新数据,有错误不会抛出异常
     *
     * @param array
     * @param <T>
     * @return
     */
    @Override
    public <T> int update(T... array) {
        return SQLiteUtils.update(databaseName, tableName, array);
    }

    /**
     * 以id为字段来更新数据,有错误会抛出异常
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> int updateOrThrow(List<T> list) {
        return SQLiteUtils.updateOrThrow(databaseName, tableName, list);
    }

    /**
     * 以id为字段来更新数据,有错误不会抛出异常
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> int update(List<T> list) {
        return SQLiteUtils.update(databaseName, tableName, list);
    }

    /**
     * 根据某个行的字段来作为条件来更新,相当于 UPDATE ... WHERE column = ?
     *
     * @param column 字段名
     * @param array  数据
     * @param <T>
     * @return
     */
    @Override
    public <T> int updateOrThrow(String column, T... array) {
        return SQLiteUtils.updateOrThrow(databaseName, tableName, column, array);
    }

    /**
     * 根据某个行的字段来作为条件来更新,相当于 UPDATE ... WHERE column = ?
     *
     * @param column 字段名
     * @param array  数据
     * @param <T>
     * @return
     */
    @Override
    public <T> int update(String column, T... array) {
        return SQLiteUtils.update(databaseName, tableName, column, array);
    }

    /**
     * 根据某个行的字段来作为条件来更新,相当于 UPDATE ... WHERE column = ?
     *
     * @param column 字段名
     * @param list   数据
     * @param <T>
     * @return
     */
    @Override
    public <T> int updateOrThrow(String column, List<T> list) {
        return SQLiteUtils.updateOrThrow(databaseName, tableName, column, list);
    }

    /**
     * 根据某个行的字段来作为条件来更新,相当于 UPDATE ... WHERE column = ?
     *
     * @param column 字段名
     * @param list   数据
     * @param <T>
     * @return
     */
    @Override
    public <T> int update(String column, List<T> list) {
        return SQLiteUtils.update(databaseName, tableName, column, list);
    }

    @Override
    public <T> AsyncDataExecutor<Integer> insertOrThrowAsync(T... array) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(insertOrThrow(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> insertAsync(T... array) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(insert(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> insertOrThrowAsync(List<T> list) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(insertOrThrow(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> insertAsync(List<T> list) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(insert(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> replaceOrThrowAsync(T... array) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(replaceOrThrow(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> replaceAsync(T... array) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(replace(array));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> replaceOrThrowAsync(List<T> list) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
        executor.submit(() -> {
            synchronized (Object.class) {
                executor.call(replaceOrThrow(list));
            }
        });
        return executor;
    }

    @Override
    public <T> AsyncDataExecutor<Integer> replaceAsync(List<T> list) {
        AsyncDataExecutor<Integer> executor = new AsyncDataExecutor<>();
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
