package redis.clients.jedis; 

import java.util.Collection; 
import java.util.List; 
import java.util.Map; 
import java.util.Set; 
import java.util.regex.Pattern; 

import redis.clients.jedis.BinaryClient.LIST_POSITION; 
import redis.clients.util.Hashing; 

import java.io.IOException; 

import redis.clients.jedis.Client.LIST_POSITION; 
import redis.clients.util.Sharded; 

public  class  ShardedJedis  extends Sharded<Jedis, JedisShardInfo>  implements JedisCommands {
	
    public ShardedJedis(List<JedisShardInfo> shards) {
        super(shards);
    }


	

    public ShardedJedis(List<JedisShardInfo> shards, Hashing algo) {
        super(shards, algo);
    }


	

    public ShardedJedis(List<JedisShardInfo> shards, Pattern keyTagPattern) {
        super(shards, keyTagPattern);
    }


	

    public ShardedJedis(List<JedisShardInfo> shards, Hashing algo, Pattern keyTagPattern) {
        super(shards, algo, keyTagPattern);
    }


	

    public String set(String key, String value) {
        Jedis j = getShard(key);
        return j.set(key, value);
    }


	

    public String get(String key) {
        Jedis j = getShard(key);
        return j.get(key);
    }


	

    public Integer exists(String key) {
        Jedis j = getShard(key);
        return j.exists(key);
    }


	

    public String type(String key) {
        Jedis j = getShard(key);
        return j.type(key);
    }


	

    public Integer expire(String key, int seconds) {
        Jedis j = getShard(key);
        return j.expire(key, seconds);
    }


	

    public Integer expireAt(String key, long unixTime) {
        Jedis j = getShard(key);
        return j.expireAt(key, unixTime);
    }


	

    public Integer ttl(String key) {
        Jedis j = getShard(key);
        return j.ttl(key);
    }


	

    public String getSet(String key, String value) {
        Jedis j = getShard(key);
        return j.getSet(key, value);
    }


	

    public Integer setnx(String key, String value) {
        Jedis j = getShard(key);
        return j.setnx(key, value);
    }


	

    public String setex(String key, int seconds, String value) {
        Jedis j = getShard(key);
        return j.setex(key, seconds, value);
    }


	

    public Integer decrBy(String key, int integer) {
        Jedis j = getShard(key);
        return j.decrBy(key, integer);
    }


	

    public Integer decr(String key) {
        Jedis j = getShard(key);
        return j.decr(key);
    }


	

    public Integer incrBy(String key, int integer) {
        Jedis j = getShard(key);
        return j.incrBy(key, integer);
    }


	

    public Integer incr(String key) {
        Jedis j = getShard(key);
        return j.incr(key);
    }


	

    public Integer append(String key, String value) {
        Jedis j = getShard(key);
        return j.append(key, value);
    }


	

    public String substr(String key, int start, int end) {
        Jedis j = getShard(key);
        return j.substr(key, start, end);
    }


	

    public Integer hset(String key, String field, String value) {
        Jedis j = getShard(key);
        return j.hset(key, field, value);
    }


	

    public String hget(String key, String field) {
        Jedis j = getShard(key);
        return j.hget(key, field);
    }


	

    public Integer hsetnx(String key, String field, String value) {
        Jedis j = getShard(key);
        return j.hsetnx(key, field, value);
    }


	

    public String hmset(String key, Map<String, String> hash) {
        Jedis j = getShard(key);
        return j.hmset(key, hash);
    }


	

    public List<String> hmget(String key, String... fields) {
        Jedis j = getShard(key);
        return j.hmget(key, fields);
    }


	

    public Integer hincrBy(String key, String field, int value) {
        Jedis j = getShard(key);
        return j.hincrBy(key, field, value);
    }


	

    public Integer hexists(String key, String field) {
        Jedis j = getShard(key);
        return j.hexists(key, field);
    }


	

    public Integer hdel(String key, String field) {
        Jedis j = getShard(key);
        return j.hdel(key, field);
    }


	

    public Integer hlen(String key) {
        Jedis j = getShard(key);
        return j.hlen(key);
    }


	

    public Set<String> hkeys(String key) {
        Jedis j = getShard(key);
        return j.hkeys(key);
    }


	

    public Collection<String> hvals(String key) {
        Jedis j = getShard(key);
        return j.hvals(key);
    }


	

    public Map<String, String> hgetAll(String key) {
        Jedis j = getShard(key);
        return j.hgetAll(key);
    }


	

    public Integer rpush(String key, String string) {
        Jedis j = getShard(key);
        return j.rpush(key, string);
    }


	

    public Integer lpush(String key, String string) {
        Jedis j = getShard(key);
        return j.lpush(key, string);
    }


	

    public Integer llen(String key) {
        Jedis j = getShard(key);
        return j.llen(key);
    }


	

    public List<String> lrange(String key, int start, int end) {
        Jedis j = getShard(key);
        return j.lrange(key, start, end);
    }


	

    public String ltrim(String key, int start, int end) {
        Jedis j = getShard(key);
        return j.ltrim(key, start, end);
    }


	

    public String lindex(String key, int index) {
        Jedis j = getShard(key);
        return j.lindex(key, index);
    }


	

    public String lset(String key, int index, String value) {
        Jedis j = getShard(key);
        return j.lset(key, index, value);
    }


	

    public Integer lrem(String key, int count, String value) {
        Jedis j = getShard(key);
        return j.lrem(key, count, value);
    }


	

    public String lpop(String key) {
        Jedis j = getShard(key);
        return j.lpop(key);
    }


	

    public String rpop(String key) {
        Jedis j = getShard(key);
        return j.rpop(key);
    }


	

    public Integer sadd(String key, String member) {
        Jedis j = getShard(key);
        return j.sadd(key, member);
    }


	

    public Set<String> smembers(String key) {
        Jedis j = getShard(key);
        return j.smembers(key);
    }


	

    public Integer srem(String key, String member) {
        Jedis j = getShard(key);
        return j.srem(key, member);
    }


	

    public String spop(String key) {
        Jedis j = getShard(key);
        return j.spop(key);
    }


	

    public Integer scard(String key) {
        Jedis j = getShard(key);
        return j.scard(key);
    }


	

    public Integer sismember(String key, String member) {
        Jedis j = getShard(key);
        return j.sismember(key, member);
    }


	

    public String srandmember(String key) {
        Jedis j = getShard(key);
        return j.srandmember(key);
    }


	

    public Integer zadd(String key, double score, String member) {
        Jedis j = getShard(key);
        return j.zadd(key, score, member);
    }


	

    public Set<String> zrange(String key, int start, int end) {
        Jedis j = getShard(key);
        return j.zrange(key, start, end);
    }


	

    public Integer zrem(String key, String member) {
        Jedis j = getShard(key);
        return j.zrem(key, member);
    }


	

    public Double zincrby(String key, double score, String member) {
        Jedis j = getShard(key);
        return j.zincrby(key, score, member);
    }


	

    public Integer zrank(String key, String member) {
        Jedis j = getShard(key);
        return j.zrank(key, member);
    }


	

    public Integer zrevrank(String key, String member) {
        Jedis j = getShard(key);
        return j.zrevrank(key, member);
    }


	

    public Set<String> zrevrange(String key, int start, int end) {
        Jedis j = getShard(key);
        return j.zrevrange(key, start, end);
    }


	

    public Set<Tuple> zrangeWithScores(String key, int start, int end) {
        Jedis j = getShard(key);
        return j.zrangeWithScores(key, start, end);
    }


	

    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
        Jedis j = getShard(key);
        return j.zrevrangeWithScores(key, start, end);
    }


	

    public Integer zcard(String key) {
        Jedis j = getShard(key);
        return j.zcard(key);
    }


	

    public Double zscore(String key, String member) {
        Jedis j = getShard(key);
        return j.zscore(key, member);
    }


	

    public List<String> sort(String key) {
        Jedis j = getShard(key);
        return j.sort(key);
    }


	

    public List<String> sort(String key, SortingParams sortingParameters) {
        Jedis j = getShard(key);
        return j.sort(key, sortingParameters);
    }


	

    public Integer zcount(String key, double min, double max) {
        Jedis j = getShard(key);
        return j.zcount(key, min, max);
    }


	

    public Set<String> zrangeByScore(String key, double min, double max) {
        Jedis j = getShard(key);
        return j.zrangeByScore(key, min, max);
    }


	

    public Set<String> zrangeByScore(String key, double min, double max,
            int offset, int count) {
        Jedis j = getShard(key);
        return j.zrangeByScore(key, min, max, offset, count);
    }


	

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        Jedis j = getShard(key);
        return j.zrangeByScoreWithScores(key, min, max);
    }


	

    public Set<Tuple> zrangeByScoreWithScores(String key, double min,
            double max, int offset, int count) {
        Jedis j = getShard(key);
        return j.zrangeByScoreWithScores(key, min, max, offset, count);
    }


	

    public Integer zremrangeByRank(String key, int start, int end) {
        Jedis j = getShard(key);
        return j.zremrangeByRank(key, start, end);
    }


	

    public Integer zremrangeByScore(String key, double start, double end) {
        Jedis j = getShard(key);
        return j.zremrangeByScore(key, start, end);
    }


	

    public Integer linsert(String key, LIST_POSITION where, String pivot,
            String value) {
        Jedis j = getShard(key);
        return j.linsert(key, where, pivot, value);
    }


	

    <<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647844869255/fstmerge_var1_3337247407234505723
public void disconnect() throws IOException {
        for (JedisShardInfo jedis : getAllShards()) {
            jedis.getResource().quit();
            jedis.getResource().disconnect();
        }
    }
=======
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/result/merge/fstmerge_tmp1647844869255/fstmerge_var2_2657935689454163454


	

    


}
