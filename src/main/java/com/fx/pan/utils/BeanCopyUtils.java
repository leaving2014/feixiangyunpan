package com.fx.pan.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leaving
 * @date 2022/2/8 19:09
 * @version 1.0
 */

public class BeanCopyUtils {

    private BeanCopyUtils(){

    }
    public static <V> V copyBean(Object source,Class<V> clazz) {
        // 创建目标对象
        V result = null;
        try {
            result = clazz.newInstance();
            //实现属性拷贝
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回结果
        return result;

    }

    public static <O,V> List<V> copyBeanList(List<O> list,Class<V> clazz){
       return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }

}
