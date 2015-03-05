package net.frozenorb.foxtrot.persist;

import redis.clients.jedis.Jedis;

public abstract class JedisCommand<T> {

    public abstract T execute(Jedis jedis);

}