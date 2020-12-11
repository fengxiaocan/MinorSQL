package com.app.msql;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.data.ObjectReflect;

import java.util.HashMap;
import java.util.Map;


public final class MSQLHelper {
    private static int sVersion = 1;
    private static Context mContext;
    private static String defaultDbName = "MSQLite.db";
    private static Map<String, ObjectReflect> reflect = new HashMap<>();

    private MSQLHelper() {
    }

    static Map<String, ObjectReflect> getReflect() {
        return reflect;
    }

    public static String DefaultDbName() {
        return defaultDbName;
    }

    /**
     * 获取默认的数据库操作工具
     *
     * @return
     */
    public static SQLiteOpenHelper getSQLiteHelper() {
        return getSQLiteHelper(defaultDbName);
    }

    /**
     * 获取对应名称的数据库操作工具
     *
     * @param databaseName 数据名
     * @return
     */
    public static SQLiteOpenHelper getSQLiteHelper(String databaseName) {
        return getSQLite(databaseName == null ? defaultDbName : databaseName);
    }

    public static MinorSQL SQL() {
        return new MinorSQLHandler(defaultDbName);
    }

    public static MinorSQL SQL(String databaseName) {
        return new MinorSQLHandler(databaseName);
    }

    /**
     * 初始化数据库
     *
     * @param context 上下文
     * @param version 数据库版本号
     */
    public static void init(Context context, int version) {
        mContext = context.getApplicationContext();
        sVersion = version;
    }

    /**
     * 初始化数据库
     *
     * @param context       上下文
     * @param defaultDbName 默认的数据库名称
     * @param version       数据库版本号
     */
    public static void init(Context context, String defaultDbName, int version) {
        init(context, version);
        MSQLHelper.defaultDbName = defaultDbName;
    }


    /**
     * 注册数据库的类,在创建数据库之前调用
     *
     * @param classArgs
     */
    private static void register(Class... classArgs) {
        for (Class objArg : classArgs) {
            getClassForName(objArg.getName());
        }
    }

    /**
     * 注册数据库的类,在创建数据库之前调用
     *
     * @param classArgs
     */
    private static void register(String... classArgs) {
        for (String argName : classArgs) {
            getClassForName(argName);
        }
    }

    private static void initDatabase(String[] databaseName) {
        SQLiteUtils.closeDatabase(getSQLiteHelper().getWritableDatabase());
        if (databaseName != null) {
            for (String database : databaseName) {
                SQLiteUtils.closeDatabase(getSQLiteHelper(database).getWritableDatabase());
            }
        }
    }

    /**
     * 初始化创建数据库，如果在初始化之后再调用register注册数据库类型，后面注册的类型无效
     *
     * @param databaseName 数据库的名称
     * @param classArgs    需要注册的数据库的类的全路径
     */
    public static void initDatabase(String[] databaseName, String... classArgs) {
        register(classArgs);
        initDatabase(databaseName);
    }


    /**
     * 初始化创建数据库，如果在初始化之后再调用register注册数据库类型，后面注册的类型无效
     *
     * @param databaseName 数据库的名称
     * @param classArgs    需要注册的数据库的类
     */
    public static void initDatabase(String[] databaseName, Class... classArgs) {
        register(classArgs);
        initDatabase(databaseName);
    }


    /**
     * 初始化创建数据库，如果在初始化之后再调用register注册数据库类型，后面注册的类型无效
     *
     * @param classArgs 需要注册的数据库的类
     */
    public static void registerDatabase(String... classArgs) {
        register(classArgs);
        initDatabase(null);
    }

    /**
     * 初始化创建数据库，如果在初始化之后再调用register注册数据库类型，后面注册的类型无效
     *
     * @param classArgs 需要注册的数据库的类
     */
    public static void registerDatabase(Class... classArgs) {
        register(classArgs);
        initDatabase(null);
    }

    /**
     * 初始化创建数据库，如果在初始化之后再调用register注册数据库类型，后面注册的类型无效
     *
     * @param databaseName 数据库的名称
     * @param classArgs    需要注册的数据库的类的全路径
     */
    public static AsyncExecutor initDatabaseAsync(String[] databaseName, String... classArgs) {
        AsyncExecutor executor = new AsyncExecutor();
        executor.submit(() -> {
            synchronized (Object.class) {
                initDatabase(databaseName, classArgs);
                executor.call();
            }
        });
        return executor;
    }

    /**
     * 初始化创建数据库，如果在初始化之后再调用register注册数据库类型，后面注册的类型无效
     *
     * @param databaseName 数据库的名称
     * @param classArgs    需要注册的数据库的类
     */
    public static AsyncExecutor initDatabaseAsync(String[] databaseName, Class... classArgs) {
        AsyncExecutor executor = new AsyncExecutor();
        executor.submit(() -> {
            synchronized (Object.class) {
                initDatabase(databaseName, classArgs);
                executor.call();
            }
        });
        return executor;
    }

    /**
     * 初始化创建数据库，如果在初始化之后再调用register注册数据库类型，后面注册的类型无效
     *
     * @param classArgs 需要注册的数据库的类
     */
    public static AsyncExecutor registerDatabaseAsync(String... classArgs) {
        AsyncExecutor executor = new AsyncExecutor();
        executor.submit(() -> {
            synchronized (Object.class) {
                registerDatabase(classArgs);
                executor.call();
            }
        });
        return executor;
    }

    /**
     * 初始化创建数据库，如果在初始化之后再调用register注册数据库类型，后面注册的类型无效
     *
     * @param classArgs 需要注册的数据库的类
     */
    public static AsyncExecutor registerDatabaseAsync(Class... classArgs) {
        AsyncExecutor executor = new AsyncExecutor();
        executor.submit(() -> {
            synchronized (Object.class) {
                registerDatabase(classArgs);
                executor.call();
            }
        });
        return executor;
    }

    private static SQLiteOpenHelper getSQLite(String databaseName) {
        return new MSQLiteDatabase(mContext, databaseName, sVersion);
    }

    private static void getClassForName(String argName) {
        try {
            String className = argName + "$SQL";

            Class<?> aClass = Class.forName(className);
            reflect.put(argName, new ObjectReflect(aClass));
        } catch (ClassNotFoundException e) {
            throw new ObjectReflect.UnRegisterMSQLException();
        }
    }

    public static QueryHandler query(String tableName) {
        return new QueryHandler(MSQLHelper.DefaultDbName(), tableName);
    }

    public static OperationHandler operation(String tableName) {
        return new OperationHandler(MSQLHelper.DefaultDbName(), tableName);
    }
}
