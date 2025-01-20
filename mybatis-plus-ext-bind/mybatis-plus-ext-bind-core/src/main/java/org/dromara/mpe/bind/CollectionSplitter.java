package org.dromara.mpe.bind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionSplitter {

    /**
     * 将一维集合拆分为指定最大长度的二维集合
     *
     * @param collection 原始一维集合
     * @param maxLength  每个子集合的最大长度
     * @param <T>        集合元素类型
     * @return 拆分后的二维集合
     */
    public static <T> List<List<T>> splitList(Collection<T> collection, int maxLength) {
        if (collection == null || maxLength <= 0) {
            throw new IllegalArgumentException("输入参数不合法");
        }

        List<T> list = new ArrayList<>(collection); // 将集合转为 List，保证支持索引操作
        List<List<T>> result = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i += maxLength) {
            // 子集合的结束索引，避免越界
            int end = Math.min(size, i + maxLength);
            result.add(new ArrayList<>(list.subList(i, end)));
        }
        return result;
    }
}
