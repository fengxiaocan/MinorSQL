package com.app.msql;

import java.util.List;

interface IMinorSQL {
    <T> long insertOrThrow(T... array);

    <T> long insert(T... array);

    <T> long insertOrThrow(List<T> list);

    <T> long insert(List<T> list);

    <T> long replaceOrThrow(T... array);

    <T> long replace(T... array);

    <T> long replaceOrThrow(List<T> list);

    <T> long replace(List<T> list);

    <T> int deleteOrThrow(T... array);

    <T> int delete(T... array);

    <T> int deleteOrThrow(List<T> list);

    <T> int delete(List<T> list);

    <T> int updateOrThrow(T... array);

    <T> int update(T... array);

    <T> int updateOrThrow(List<T> list);

    <T> int update(List<T> list);


    <T> AsyncDataExecutor<Long> insertOrThrowAsync(T... array);

    <T> AsyncDataExecutor<Long> insertAsync(T... array);

    <T> AsyncDataExecutor<Long> insertOrThrowAsync(List<T> list);

    <T> AsyncDataExecutor<Long> insertAsync(List<T> list);

    <T> AsyncDataExecutor<Long> replaceOrThrowAsync(T... array);

    <T> AsyncDataExecutor<Long> replaceAsync(T... array);

    <T> AsyncDataExecutor<Long> replaceOrThrowAsync(List<T> list);

    <T> AsyncDataExecutor<Long> replaceAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> deleteOrThrowAsync(T... array);

    <T> AsyncDataExecutor<Integer> deleteAsync(T... array);

    <T> AsyncDataExecutor<Integer> deleteOrThrowAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> deleteAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> updateOrThrowAsync(T... array);

    <T> AsyncDataExecutor<Integer> updateAsync(T... array);

    <T> AsyncDataExecutor<Integer> updateOrThrowAsync(List<T> list);

    <T> AsyncDataExecutor<Integer> updateAsync(List<T> list);
}
