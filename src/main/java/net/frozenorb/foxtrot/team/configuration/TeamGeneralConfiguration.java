package net.frozenorb.foxtrot.team.configuration;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.configuration.Configuration;
import net.frozenorb.qlib.configuration.annotations.ConfigData;

import java.util.ArrayList;
import java.util.List;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 27/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class TeamGeneralConfiguration extends Configuration {

    @ConfigData( path = "teams.disallowed_names")
    @Getter
    private static List<String> disallowedNames = new ArrayList<>(  );

    public TeamGeneralConfiguration() {
        super( Foxtrot.getInstance() , "config.yml", "./plugins/Teams/" );
        disallowedNames.add( "glowstone" );
        disallowedNames.add( "miniend" );
        load();
        save();
    }
}
