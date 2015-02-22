package net.frozenorb.foxtrot.nametag;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NametagThread extends Thread {

    @Getter private static List<NametagUpdate> pendingUpdates = Collections.synchronizedList(new ArrayList<NametagUpdate>());

    public NametagThread() {
        super("Foxtrot - Nametag Thread");
    }

    public void run() {
        try {
            while (true) {
                for (NametagUpdate pendingUpdate : pendingUpdates) {
                    NametagManager.applyUpdate(pendingUpdate);
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