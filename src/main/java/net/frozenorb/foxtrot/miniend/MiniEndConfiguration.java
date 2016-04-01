package net.frozenorb.foxtrot.miniend;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.configuration.Configuration;
import net.frozenorb.qlib.configuration.annotations.ConfigData;
import net.frozenorb.qlib.configuration.annotations.ConfigSerializer;
import net.frozenorb.qlib.configuration.serializers.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MiniEndConfiguration extends Configuration {

    @ConfigData( path = "miniend.maximum_team_size" )
    @Getter
    private static int maximumTeamSize = 3;

    @ConfigData( path = "miniend.spawn_location" )
    @ConfigSerializer( serializer = LocationSerializer.class )
    @Getter
    private static Location spawnLocation = new Location( Bukkit.getWorld("world_the_end"), 500, 70, 500 );

    @ConfigData( path = "miniend.team_name" )
    @Getter
    private static String teamName = "MiniEnd";

    public MiniEndConfiguration() {
        super( Foxtrot.getInstance(), "config.yml", "./plugins/MiniEnd/" );
        load();
        save();
    }
}
