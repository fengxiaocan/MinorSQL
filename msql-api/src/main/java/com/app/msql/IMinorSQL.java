package com.app.msql;

import java.util.List;

interface IMinorSQL {
    <T> int insertOrThrow(T... array);

    <T> int insert(T... array);

    <T> int insertOrThrow(List<T> list);

    <T> int insert(List<T> list);

    <T> int replaceOrThrow(T... array);

    <T> int replace(T... array);

    <T> int replaceOrThrow(List<T> list);

    <T> int replace(List<T> list);

    <T> int deleteOrThrow(T... array);

    <T> int delete(T... array);

    <T> int deleteOrThrow(List<T> list);

    <T> int delete(List<T> list);

    <T> int updateOrThrow(T... array);

    <T> int update(T... array);

    <T> int updateOrThrow(List<T> list);

    <T> int update(List<T> list);

    <T> int updateOrThrow(String column,T... array);

    <T> int update(String column,T... array);

    <T> int updateOrThrow(String column,List<T> list);

    <T> int update(String column,List<T> list);


    <T> AsyncDataExecutor<Integer> insertOrThrowAsync(T... array);

    <T> AsyncDataExecutor<Integer> insertAsync(T... array);

    <T> AsyncDataExecutor<Integer> insertOrThrowAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> insertAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> replaceOrThrowAsync(T... array);

    <T> AsyncDataExecutor<Integer> replaceAsync(T... array);

    <T> AsyncDataExecutor<Integer> replaceOrThrowAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> replaceAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> deleteOrThrowAsync(T... array);

    <T> AsyncDataExecutor<Integer> deleteAsync(T... array);

    <T> AsyncDataExecutor<Integer> deleteOrThrowAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> deleteAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> updateOrThrowAsync(T... array);

    <T> AsyncDataExecutor<Integer> updateAsync(T... array);

    <T> AsyncDataExecutor<Integer> updateOrThrowAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> updateAsync(List<T> list);
}
