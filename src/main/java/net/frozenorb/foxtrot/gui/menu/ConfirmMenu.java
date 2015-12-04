package net.frozenorb.foxtrot.gui.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.Utilities.Interfaces.Callback;
import net.frozenorb.foxtrot.gui.button.BooleanButton;
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
public class ConfirmMenu extends Menu {
    private String title;
    private Callback<Boolean> response;


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9;i++) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, response));

            } else if (i ==5) {
                buttons.put(i, new BooleanButton(false, response));

            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));

            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return title;
    }
}
