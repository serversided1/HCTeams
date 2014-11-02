package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.mBasic.Basic;
import org.bukkit.entity.Player;

public class SetBalCommand {

    @Command(names={ "SetBal" }, permissionNode="foxtrot.setbal")
    public static void setBal(Player sender, @Param(name="Target") String target, @Param(name="Amount") float value) {
        Basic.get().getEconomyManager().setBalance(target, value);
    }

}