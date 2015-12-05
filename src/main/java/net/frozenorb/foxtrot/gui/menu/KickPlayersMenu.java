package net.frozenorb.foxtrot.gui.menu;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.gui.button.ChangePromotionStatusButton;
import net.frozenorb.foxtrot.gui.button.KickPlayerButton;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class KickPlayersMenu extends Menu {
    @NonNull
    @Getter
    Team team;


    @Override
    public String getTitle(Player player) {
        return "Players in " + team.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        HashMap<Integer, Button> buttons = new HashMap<>();

        int index = 0;

        for (UUID uuid : team.getMembers()) {
            buttons.put(index, new KickPlayerButton(uuid, team));

            index++;
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
