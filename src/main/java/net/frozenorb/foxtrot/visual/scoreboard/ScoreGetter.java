package net.frozenorb.foxtrot.visual.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.armor.Kit;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.server.SpawnTag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public interface ScoreGetter {
    public static final long NO_SCORE = -1;

    public static final ScoreGetter SPAWN_TAG = new ScoreGetter(){
        @Override
        public String getTitle(Player player){
            return ChatColor.RED + "" + ChatColor.BOLD + "Spawn Tag";
        }

        @Override
        public long getMillis(Player player){
            if(SpawnTag.isTagged(player)){
                long diff = (SpawnTag.getSpawnTags().get(player.getName()).getExpires() - System.currentTimeMillis());

                if(diff >= 0){
                    return diff;
                }
            }

            return NO_SCORE;
        }
    };

    public static final ScoreGetter ENDERPEARL = new ScoreGetter(){
        @Override
        public String getTitle(Player player){
            return ChatColor.YELLOW + "" + ChatColor.BOLD + "Enderpearl";
        }

        @Override
        public long getMillis(Player player){
            if(FoxListener.getEnderpearlCooldown().containsKey(player.getName()) && FoxListener.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()){
                long diff = (FoxListener.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis());

                if(diff >= 0){
                    return diff;
                }
            }

            return NO_SCORE;
        }
    };

    public static final ScoreGetter PVP_TIMER = new ScoreGetter(){
        @Override
        public String getTitle(Player player){
            return ChatColor.GREEN + "" + ChatColor.BOLD + "PVP Timer";
        }

        @Override
        public long getMillis(Player player){
            if(FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(player)){
                long diff = FoxtrotPlugin.getInstance().getJoinTimerMap().getValue(player.getName()) - System.currentTimeMillis();

                if(diff >= 0){
                    return diff;
                }
            }

            return NO_SCORE;
        }
    };

    public static final ScoreGetter CLASS_WARMUP = new ScoreGetter(){
        @Override
        public String getTitle(Player player){
            return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Class Warmup";
        }

        @Override
        public long getMillis(Player player){
            if(Kit.getWarmupTasks().containsKey(player.getName())){
                long diff = (Kit.getWarmupTasks().get(player.getName()).getEnds() - System.currentTimeMillis());

                if(diff >= 0){
                    return diff;
                }
            }

            return NO_SCORE;
        }
    };

    public static final ScoreGetter[] SCORES = {
            SPAWN_TAG,
            ENDERPEARL,
            PVP_TIMER,
            CLASS_WARMUP
    };


    public String getTitle(Player player);

    public long getMillis(Player player);
}