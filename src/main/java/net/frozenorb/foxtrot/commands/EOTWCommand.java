package net.frozenorb.foxtrot.commands;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.EndListener;
import net.frozenorb.qlib.command.Command;

public class EOTWCommand {

    @Getter @Setter private static boolean ffaEnabled = false;
    @Getter @Setter private static long ffaActiveAt = -1L;
    
    @Command(names={ "EOTW" }, permission="foxtrot.eotw")
    public static void eotw(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        Foxtrot.getInstance().getServerHandler().setEOTW(!Foxtrot.getInstance().getServerHandler().isEOTW());

        EndListener.endActive = !Foxtrot.getInstance().getServerHandler().isEOTW();

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
            }

            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW has commenced.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All SafeZones are now Deathban.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        } else {
            sender.sendMessage(ChatColor.RED + "The server is no longer in EOTW mode.");
        }
    }

    @Command(names={ "EOTW TpAll" }, permission="foxtrot.eotw")
    public static void eotwTpAll(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (!Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "This command must be ran during EOTW. (/eotw)");
            return;
        }

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.teleport(sender.getLocation());
        }

        sender.sendMessage(ChatColor.RED + "Players teleported.");
    }

    @Command(names={ "PreEOTW" }, permission="foxtrot.eotw")
    public static void preeotw(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        Foxtrot.getInstance().getServerHandler().setPreEOTW(!Foxtrot.getInstance().getServerHandler().isPreEOTW());

        Foxtrot.getInstance().getDeathbanMap().wipeDeathbans();

        if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
            }

            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[Pre-EOTW]");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW is about to commence.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED + "PvP Protection is disabled.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All players have been un-deathbanned.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.RED + "All deathbans are now permanent.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        } else {
            sender.sendMessage(ChatColor.RED + "The server is no longer in Pre-EOTW mode.");
        }
    }

    @Command(names = {"eotw ffa"}, permission="foxtrot.eotw")
    public static void ffa(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }
 
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to enter FFA mode? This will start a countdown that cannot be cancelled. Type yes or no to confirm.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    ffaEnabled = true;
                    ffaActiveAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "FFA countdown initiated.");
                    
                    Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW " + ChatColor.GOLD.toString() + ChatColor.BOLD + "FFA" + ChatColor.RED.toString() + ChatColor.BOLD + " will commence in: 5:00.");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + "If you ally, you will be punished.");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                    });

                    Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW " + ChatColor.GOLD.toString() + ChatColor.BOLD + "FFA" + ChatColor.RED.toString() + ChatColor.BOLD + " has now commenced!");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "Good luck and have fun!");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████");
                        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                    }, 5 * 60 * 20);
                    
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "FFA initation aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

    public static boolean realFFAStarted() {
        return ffaEnabled && ffaActiveAt < System.currentTimeMillis();
    }

}