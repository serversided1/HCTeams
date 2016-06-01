package net.frozenorb.foxtrot.minerworld.blockregen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public class BlockData {

    private final Material type;
    private final byte data;

}
