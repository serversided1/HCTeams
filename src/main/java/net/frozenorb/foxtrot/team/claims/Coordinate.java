package net.frozenorb.foxtrot.team.claims;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Coordinate {

    @Getter @Setter int x;
    @Getter @Setter int z;

    @Override
    public String toString() {
        return (x + ", " + z);
    }

}