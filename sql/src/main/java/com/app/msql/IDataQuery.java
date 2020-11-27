package com.app.msql;

import java.util.List;

interface IDataQuery {
    <T> T findLast(Class<T> modelClass);

    <T> T findFirst(Class<T> modelClass);

    <T> List<T> find(Class<T> modelClass);

    <T> T findLast(String modelClass);

    <T> T findFirst(String modelClass);

    <T> List<T> find(String modelClass);

    <T> AsyncDataExecutor<T> findLastAsync(Class<T> modelClass);

    <T> AsyncDataExecutor<T> findFirstAsync(Class<T> modelClass);

    <T> AsyncDataExecutor<List<T>> findAsync(Class<T> modelClass);

    <T> AsyncDataExecutor<T> findLastAsync(String modelClass);

    <T> AsyncDataExecutor<T> findFirstAsync(String modelClass);

    <T> AsyncDataExecutor<List<T>> findAsync(String modelClass);

    int count();

    AsyncDataExecutor<Integer> countAsync();
}
