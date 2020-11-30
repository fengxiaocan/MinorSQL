package com.app.data;

import android.content.ContentValues;

public abstract class SQLSupport implements MISQLSupport {

    private static final String UN_SUPPORT_EXCEPTION = "请使用 @SQLite 注解类 并在 SQLiteHelper.init 中注册";

    /************* 增 **************/

    /**
     * 保存到数据库中,即使已经保存在数据库
     * 如果有成员变量以@Column(unique=true)标记,则会根据该成员变量的名的条件来查询更新到数据库中,不存在则保存
     */
    @Override
    public int insert() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /**
     * 保存到数据库中,如果哟异常,抛出异常
     * 如果有成员变量以@Column(unique=true)标记,则会根据该成员变量的名的条件来查询更新到数据库中,不存在则保存
     */
    @Override
    public int insertOrThrow() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /**
     * 保存到指定的表的数据库中
     * 如果有成员变量以@Column(unique=true)标记,则会根据该成员变量的名的条件来查询更新到数据库中,不存在则保存
     */
    @Override
    public int insert(String tableName) {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /**
     * 保存到指定的表的数据库中
     * 如果有成员变量以@Column(unique=true)标记,则会根据该成员变量的名的条件来查询更新到数据库中,不存在则保存
     */
    @Override
    public int insertOrThrow(String tableName) {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /************* 删 **************/

    /**
     * 删除,以ID为条件查找
     * 如果有成员变量以@Column(unique=true)标记,则会根据该成员变量的名的条件来查询并删除
     */
    @Override
    public int delete() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /**
     * 删除对应的表的数据,以ID为条件查找
     * 如果有成员变量以@Column(unique=true)标记,则会根据该成员变量的名的条件来查询并删除
     */
    @Override
    public int delete(String tableName) {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /************* 改 **************/

    /**
     * 以ID为条件更新到数据库
     */
    @Override
    public int update() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }


    /**
     * 以ID为条件更新到数据库
     */
    @Override
    public int update(String tableName) {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }


    /**
     * 以ID为条件更新到数据库
     */
    @Override
    public int updateOrThrow() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }


    /**
     * 以ID为条件更新到数据库
     */
    @Override
    public int updateOrThrow(String tableName) {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }


    /******************替换或插入*****************/

    /**
     * 根据条件更新到数据库或者保存到数据库
     */
    @Override
    public int replace(String tableName) {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /**
     * 根据条件更新到数据库或者保存到数据库
     */
    @Override
    public int replaceOrThrow(String tableName) {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    @Override
    public int replace() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    @Override
    public int replaceOrThrow(){
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /**
     * 获取数据库ID
     *
     * @return
     */
    @Override
    public long getSQLiteID() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /**
     * 获取SQLite的Values
     *
     * @return
     */
    @Override
    public void SQLiteValues(ContentValues value) {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /**
     * 清除保存状态
     */
    @Override
    public void clearSavedState() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    /**
     * 判断是否保存到数据库中
     *
     * @return
     */
    @Override
    public boolean isSaved() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }

    @Override
    public String defaultTableName() {
        throw new UnsupportedOperationException(UN_SUPPORT_EXCEPTION);
    }
}
