package net.frozenorb.foxtrot.jedis;

import redis.clients.jedis.Jedis;

public abstract class JedisCommand<T> {

    public abstract T execute(Jedis jedis);

}