package net.frozenorb.foxtrot.gui.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.Utilities.Interfaces.Callback;
import net.frozenorb.foxtrot.gui.button.BooleanButton;
import net.frozenorb.foxtrot.gui.button.MuteButton;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kerem on 12/3/2015.
 */

@AllArgsConstructor
public class MuteMenu extends Menu {
    private Team team;


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 1) {
                buttons.put(i, new MuteButton(5, team));

            } else if (i == 3) {
                buttons.put(i, new MuteButton(15, team));

            } else if (i == 5) {
                buttons.put(i, new MuteButton(30, team));

            } else if (i == 7) {
                buttons.put(i, new MuteButton(60, team));

            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));

            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Mute " + team.getName();
    }
}
