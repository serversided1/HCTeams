package net.frozenorb.foxtrot.nametag;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NametagThread extends Thread {

    // We use a Map here for a few reasons...
    // 1) Why the heck not
    // 2) There's no good concurrent set implementation
    // 3) Sets are backed by Maps anyway so...
    @Getter private static Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    public NametagThread() {
        super("Foxtrot - Nametag Thread");
    }

    public void run() {
        try {
            while (true) {
                for (NametagUpdate pendingUpdate : pendingUpdates.keySet()) {
                    try {
                        NametagManager.applyUpdate(pendingUpdate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                pendingUpdates.clear();

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}