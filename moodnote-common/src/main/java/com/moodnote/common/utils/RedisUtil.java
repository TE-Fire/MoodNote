package com.moodnote.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * 提供常见的 Redis 操作方法
 */
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== Key 相关操作 ====================

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis 判断 key 存在失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除指定 key
     *
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis 删除 key 失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 批量删除 key
     *
     * @param keys 键集合
     * @return 删除的数量
     */
    public Long delete(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("Redis 批量删除 key 失败: {}", e.getMessage());
            return 0L;
        }
    }

    /**
     * 设置 key 的过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("Redis 设置过期时间失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取 key 的过期时间
     *
     * @param key  键
     * @param unit 时间单位
     * @return 过期时间（-1 表示永不过期，-2 表示 key 不存在）
     */
    public Long getExpire(String key, TimeUnit unit) {
        try {
            return redisTemplate.getExpire(key, unit);
        } catch (Exception e) {
            log.error("Redis 获取过期时间失败: {}", e.getMessage());
            return -2L;
        }
    }

    /**
     * 查找匹配的 key
     *
     * @param pattern 匹配模式（如 user:*）
     * @return 匹配的 key 集合
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("Redis 查找 key 失败: {}", e.getMessage());
            return null;
        }
    }

    // ==================== String 类型操作 ====================

    /**
     * 设置 String 类型值
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis 设置值失败: {}", e.getMessage());
        }
    }

    /**
     * 设置 String 类型值并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis 设置值（带过期时间）失败: {}", e.getMessage());
        }
    }

    /**
     * 获取 String 类型值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis 获取值失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 String 类型值并转换为指定类型
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   类型泛型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? (T) value : null;
        } catch (Exception e) {
            log.error("Redis 获取值并转换类型失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 自增操作
     *
     * @param key   键
     * @param delta 增量值
     * @return 自增后的值
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis 自增失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 自减操作
     *
     * @param key   键
     * @param delta 减量值
     * @return 自减后的值
     */
    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("Redis 自减失败: {}", e.getMessage());
            return null;
        }
    }

    // ==================== Hash 类型操作 ====================

    /**
     * 设置 Hash 类型值
     *
     * @param key     键
     * @param hashKey 哈希键
     * @param value   值
     */
    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("Redis 设置 Hash 值失败: {}", e.getMessage());
        }
    }

    /**
     * 设置 Hash 类型值并指定过期时间
     *
     * @param key     键
     * @param hashKey 哈希键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void hSet(String key, String hashKey, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("Redis 设置 Hash 值（带过期时间）失败: {}", e.getMessage());
        }
    }

    /**
     * 获取 Hash 类型值
     *
     * @param key     键
     * @param hashKey 哈希键
     * @return 值
     */
    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("Redis 获取 Hash 值失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 Hash 类型值并转换为指定类型
     *
     * @param key     键
     * @param hashKey 哈希键
     * @param clazz   目标类型
     * @param <T>     类型泛型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForHash().get(key, hashKey);
            return value != null ? (T) value : null;
        } catch (Exception e) {
            log.error("Redis 获取 Hash 值并转换类型失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取整个 Hash 的所有键值对
     *
     * @param key 键
     * @return 键值对 Map
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Redis 获取 Hash 所有值失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 Hash 的所有键
     *
     * @param key 键
     * @return 键集合
     */
    public Set<Object> hKeys(String key) {
        try {
            return redisTemplate.opsForHash().keys(key);
        } catch (Exception e) {
            log.error("Redis 获取 Hash 键失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 Hash 的所有值
     *
     * @param key 键
     * @return 值列表
     */
    public List<Object> hValues(String key) {
        try {
            return redisTemplate.opsForHash().values(key);
        } catch (Exception e) {
            log.error("Redis 获取 Hash 值列表失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除 Hash 中的指定键
     *
     * @param key     键
     * @param hashKey 哈希键
     * @return 是否删除成功
     */
    public boolean hDelete(String key, Object... hashKey) {
        try {
            Long result = redisTemplate.opsForHash().delete(key, hashKey);
            return result != null && result > 0;
        } catch (Exception e) {
            log.error("Redis 删除 Hash 键失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断 Hash 中是否存在指定键
     *
     * @param key     键
     * @param hashKey 哈希键
     * @return 是否存在
     */
    public boolean hHasKey(String key, String hashKey) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, hashKey));
        } catch (Exception e) {
            log.error("Redis 判断 Hash 键存在失败: {}", e.getMessage());
            return false;
        }
    }

    // ==================== List 类型操作 ====================

    /**
     * 将值添加到 List 头部
     *
     * @param key   键
     * @param value 值
     * @return 添加后的 List 长度
     */
    public Long lPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("Redis List 左推失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 将值添加到 List 尾部
     *
     * @param key   键
     * @param value 值
     * @return 添加后的 List 长度
     */
    public Long rPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("Redis List 右推失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 List 头部弹出值
     *
     * @param key 键
     * @return 弹出的值
     */
    public Object lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("Redis List 左弹出失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 List 尾部弹出值
     *
     * @param key 键
     * @return 弹出的值
     */
    public Object rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("Redis List 右弹出失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 List 长度
     *
     * @param key 键
     * @return List 长度
     */
    public Long lSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("Redis 获取 List 长度失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 List 中指定范围的元素
     *
     * @param key   键
     * @param start 起始索引（0 开始）
     * @param end   结束索引（-1 表示最后一个）
     * @return 元素列表
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis 获取 List 范围元素失败: {}", e.getMessage());
            return null;
        }
    }

    // ==================== Set 类型操作 ====================

    /**
     * 向 Set 中添加值
     *
     * @param key    键
     * @param values 值
     * @return 添加的数量
     */
    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Redis Set 添加值失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Set 中移除值
     *
     * @param key    键
     * @param values 值
     * @return 移除的数量
     */
    public Long sRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("Redis Set 移除值失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 判断 Set 中是否存在指定值
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    public boolean sContains(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            log.error("Redis 判断 Set 值存在失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取 Set 的所有元素
     *
     * @param key 键
     * @return 元素集合
     */
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis 获取 Set 所有元素失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 Set 长度
     *
     * @param key 键
     * @return Set 长度
     */
    public Long sSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("Redis 获取 Set 长度失败: {}", e.getMessage());
            return null;
        }
    }

    // ==================== ZSet 类型操作 ====================

    /**
     * 向 ZSet 中添加值（带分数）
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否添加成功
     */
    public boolean zAdd(String key, Object value, double score) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            log.error("Redis ZSet 添加值失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 ZSet 中移除值
     *
     * @param key    键
     * @param values 值
     * @return 移除的数量
     */
    public Long zRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            log.error("Redis ZSet 移除值失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 ZSet 中值的分数
     *
     * @param key   键
     * @param value 值
     * @return 分数
     */
    public Double zScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("Redis 获取 ZSet 分数失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 ZSet 中指定分数范围的元素（升序）
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素集合
     */
    public Set<Object> zRange(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("Redis 获取 ZSet 范围元素失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 ZSet 长度
     *
     * @param key 键
     * @return ZSet 长度
     */
    public Long zSize(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            log.error("Redis 获取 ZSet 长度失败: {}", e.getMessage());
            return null;
        }
    }
}