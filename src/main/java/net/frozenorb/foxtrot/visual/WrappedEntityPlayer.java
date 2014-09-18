package net.frozenorb.foxtrot.visual;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public class WrappedEntityPlayer extends EntityPlayer {
	public String listName;
	public int ping = 0;

	public WrappedEntityPlayer(GameProfile gp, String name) {
		super(MinecraftServer.getServer(), ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(), gp, new PlayerInteractManager(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle()));
		this.listName = name;
	}

}
