package com.fx.pan.utils;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author leaving
 * @date 2022/1/11 22:41
 * @version 1.0
 */

@Component
public class RedisCache {

    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将值放入缓存并设置时间-秒
     *
     * @param key   键
     * @param value 值
     * @param time  时间（单位：秒），如果值为负数，则永久
     */
    public void set(String key, Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key
     * @param hkey
     */
    public void delCacheMapValue(final String key, final String hkey) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, hkey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<T> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    // 添加Zset
    public <T> Boolean addZset(final String key, final T value, final double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    // 修改Zset中的值
    public <T> Double updateZset(final String key, final T value, final Long score) {
        return redisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    // 获取Zset中key的值
    public <T> Double getZset(final String key, final T value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    // 获取Zset中的Score
    public <T> Double getZsetScore(final String key, final T value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    // 获取Zset中前N名
    public <T> List<Long> getZsetRangeList(final String key, final long start, final long end) {
        // 按照score 从小到大取20个
        Set<Long> set = redisTemplate.opsForZSet().reverseRangeByScore(key, 0, 999999999999999999L);
        List<Long> rankList = new ArrayList<>(set.size());
        for (Long sub : set) {
            rankList.add(sub);
        }
        return rankList;
    }

    // 获取前N名
    public <T> Set<T> getZsetTop(final String key, final long start, final long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    // 获取Zset的长度
    public <T> Long getZsetSize(final String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    // 删除Zset中的值
    public <T> Long deleteZset(final String key, final T value) {
        return redisTemplate.opsForZSet().remove(key, value);
    }

    // 获取Zset的所有
    public <T> Set<T> getZsetAll(final String key) {
        return redisTemplate.opsForZSet().range(key, 0, -1);
    }

    /**
     * 设置bitMap
     *
     * @param key
     * @param offset
     * @param value
     * @return
     */
    public Boolean setBitMap(final String key, final long offset, final boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    public Boolean mark(String key, long offset, boolean tag) {
        return redisTemplate.opsForValue().setBit(key, offset, tag);
    }

    /**
     * 判断是否标记过
     *
     * @param key
     * @param offest
     * @return
     */
    public Boolean getBitMap(String key, long offest) {
        return redisTemplate.opsForValue().getBit(key, offest);
    }

    /**
     * 统计计数
     *
     * @param key
     * @return
     */
    public long bitCount(String key) {
        return (long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.bitCount(key.getBytes());
            }
        });
    }


    // 获取bitMap中所有标记的位置的数组
    public List<Long> bitPos(String key) {
        return (List<Long>) redisTemplate.execute(new RedisCallback<List<Long>>() {
            @Override
            public List<Long> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return Collections.singletonList(redisConnection.bitPos(key.getBytes(), true));
            }
        });
    }

    public Set<Long> getBitMapIndex(String key) {
        return (Set<Long>) redisTemplate.execute(new RedisCallback<Set<Long>>() {
            @Override
            public Set<Long> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return Collections.singleton(redisConnection.bitPos(key.getBytes(), true));
            }
        });
    }

    /**
     * 自增1
     *
     * @param key
     */
    public void incr(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自减1
     *
     * @param key
     */
    public void decr(String key) {
        redisTemplate.opsForValue().decrement(key);
    }

    // 更新redis list中的数据
    public <T> void updateCacheList(final String key, final Integer index, final Object value) {
        // redisTemplate.opsForList().rightPushAll(key, Object);
        redisTemplate.opsForList().remove(key, 1, value);
        // redisTemplate.opsForList().set(key, index, value);
    }

    // 删除 List中的数据
    public void deleteCacheList(final String key, final Object value) {
        redisTemplate.opsForList().remove(key, 1, value);
    }


    /**
     * 获取自增长值
     *
     * @param key 键
     * @return 返回增长之后的值
     */
    public Long getIncr(String key) {
        Long count = redisTemplate.opsForValue().increment(key, 1);
        return count;
    }

    //判断对象是否存在
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


}
