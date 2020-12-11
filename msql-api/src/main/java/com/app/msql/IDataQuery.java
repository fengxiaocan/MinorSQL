package com.app.msql;

import java.util.List;

interface IDataQuery {
    /**
     * 查找最后一个数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> T findLast(Class<T> modelClass);

    /**
     * 查找第一个数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> T findFirst(Class<T> modelClass);

    /**
     * 查找所有相关数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> List<T> find(Class<T> modelClass);

    /**
     * 查找最后一个数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> T findLast(String modelClass);

    /**
     * 查找第一个数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> T findFirst(String modelClass);

    /**
     * 查找所有相关数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> List<T> find(String modelClass);

    /**
     * 异步线程查找最后一个数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> AsyncDataExecutor<T> findLastAsync(Class<T> modelClass);

    /**
     * 异步线程查找第一个数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> AsyncDataExecutor<T> findFirstAsync(Class<T> modelClass);

    /**
     * 异步线程查找所有数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> AsyncDataExecutor<List<T>> findAsync(Class<T> modelClass);

    /**
     * 异步线程查找最后一个数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> AsyncDataExecutor<T> findLastAsync(String modelClass);

    /**
     * 异步线程查找第一个数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> AsyncDataExecutor<T> findFirstAsync(String modelClass);

    /**
     * 异步线程查找所有数据
     *
     * @param modelClass
     * @param <T>
     * @return
     */
    <T> AsyncDataExecutor<List<T>> findAsync(String modelClass);

    /**
     * 获取所有数据的数量
     *
     * @return
     */
    int count();

    /**
     * 异步获取所有数据的数量
     *
     * @return
     */
    AsyncDataExecutor<Integer> countAsync();
}
