package net.frozenorb.foxtrot.persist.maps.statistics;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class BaseStatisticMap extends PersistMap<Integer> {

    public BaseStatisticMap(String statistic) {
        super(statistic, "Statistics." + statistic);
    }

    @Override
    public String getRedisValue(Integer statistic) {
        return (String.valueOf(statistic));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer statistic) {
        return (statistic);
    }

    public int getStatistic(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setStatistic(UUID update, int statistic) {
        updateValueAsync(update, statistic);
    }

    public void incrementStatistic(UUID update, int incr) {
        updateValueAsync(update, getStatistic(update) + incr);
    }

}