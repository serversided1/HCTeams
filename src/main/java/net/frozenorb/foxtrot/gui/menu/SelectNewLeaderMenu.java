package net.frozenorb.foxtrot.gui.menu;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.gui.button.MakeLeaderButton;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
public class SelectNewLeaderMenu extends Menu {
    @NonNull @Getter Team team;


    @Override
    public String getTitle(Player player) {
        return "Leader for " + team.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        HashMap<Integer, Button> buttons = new HashMap<>();

        int index = 0;

        ArrayList<UUID> uuids = new ArrayList<>();
        uuids.addAll(team.getMembers());

        Collections.sort(uuids, new Comparator<UUID>() {
            public int compare(UUID u1, UUID u2) {
                return UUIDUtils.name(u1).compareTo(UUIDUtils.name(u2));
            }
        });

        for (UUID u : uuids) {
            buttons.put(index, new MakeLeaderButton(u, team));
            index++;

        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
