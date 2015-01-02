package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.listener.BorderListener;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.conversations.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class RemoveExcessMobsCommand {

    public static List<String> APPROVED_USERS = Arrays.asList("itsjhalt", "_Phacad3", "macguy8");

    @Command(names={ "removeexcessmobs" }, permissionNode="op")
    public static void removeExcessMobs(Player sender) {
        if (!APPROVED_USERS.contains(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        ConversationFactory factory = new ConversationFactory(FoxtrotPlugin.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to remove all excess mobs? This action CANNOT be reversed. Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    new BukkitRunnable() {

                        int worldBorder = BorderListener.BORDER_SIZE;
                        int chunksInSector = worldBorder / 16;
                        int chunks = (worldBorder * 2) / 16; // x2 to get the negatives as well, / 16 to get in chunks.
                        int chunksTotal = chunks * chunks;
                        int chunksDone = 0;
                        int chunksX = -chunksInSector;
                        int totalRemoved = 0;
                        int chunksZ = -chunksInSector;

                        public void run() {
                            for (int i = 0; i < 400; i++) {
                                tick();
                            }
                        }

                        public void tick() {
                            if (chunksX <= chunksInSector) {
                                processChunk(chunksX, chunksZ);
                                chunksX++;
                            } else {
                                chunksX = -chunksInSector;
                                chunksZ++;

                                if (chunksZ > chunksInSector) {
                                    System.out.println("Done!");
                                    cancel();
                                }
                            }
                        }

                        public void processChunk(int x, int z) {
                            chunksDone++;
                            Chunk chunk = FoxtrotPlugin.getInstance().getServer().getWorlds().get(0).getChunkAt(x, z);
                            int entInChunk = 0;
                            int maxEntInChunk = 10;
                            int removed = 0;

                            for (Entity entity : chunk.getEntities()) {
                                if (entity instanceof Monster) {
                                    continue;
                                }

                                entInChunk++;

                                if (entInChunk > maxEntInChunk) {
                                    entity.remove();
                                    removed++;
                                }
                            }

                            System.out.println("Processing [" + chunk.getX() + ", " + chunk.getZ() + "] - Entities: " + entInChunk + " [" + ((float) chunksDone / (float) chunksTotal) * 100F + "%]");

                            totalRemoved += removed;

                            if (removed != 0) {
                                cc.getForWhom().sendRawMessage("Removed " + removed + " mobs in [" + chunk.getX() + ", " + chunk.getZ() + "] - Total: " + totalRemoved);
                            }
                        }

                    }.runTaskTimer(FoxtrotPlugin.getInstance(), 1L, 1L);

                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Mob cleanup aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

}