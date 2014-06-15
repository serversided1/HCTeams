package net.frozenorb.foxtrot.jedis;

import redis.clients.jedis.Jedis;

/***
 * Abstract class used to create jedis command instances
 * 
 * @author Kerem Celik
 * 
 */
public abstract class JedisCommand<T> {

	/**
	 * Executes the command on the given Jedis instance.
	 * 
	 * @param jedis
	 *            the jedis instance to execute the command under
	 * @return any value chosen to be returned
	 */
	public abstract T execute(Jedis jedis);

}
