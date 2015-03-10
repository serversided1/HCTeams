package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class TransferableLivesMap extends PersistMap<Integer> {

    public TransferableLivesMap() {
        super("TransferableLives", "Lives.Transferable");
    }

    @Override
    public String getRedisValue(Integer lives) {
        return (String.valueOf(lives));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer lives) {
        return (lives);
    }

    public int getLives(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setLives(UUID update, int lives) {
        updateValue(update, lives);
    }

}