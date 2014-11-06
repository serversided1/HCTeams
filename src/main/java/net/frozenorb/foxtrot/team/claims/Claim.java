package net.frozenorb.foxtrot.team.claims;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Iterator;

@Data
@RequiredArgsConstructor
public class Claim implements Iterable<Coordinate> {
	int x1, y1, z1, x2, y2, z2;
    SpecialTag tag;
	String name;

	public Claim(Location corner1, Location corner2) {
		this(corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ(), corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ());
	}

	public Claim(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.x1 = Math.min(x1, x2);
		this.x2 = Math.max(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.y2 = Math.max(y1, y2);
		this.z1 = Math.min(z1, z2);
		this.z2 = Math.max(z1, z2);
	}

    public static int getPrice(Claim claim, Team team, boolean buying){
        int x = Math.abs(claim.x1 - claim.x2);
        int z = Math.abs(claim.z1 - claim.z2);

        int blocks = x * z;

        double curPrice = 0D;

        int done = 0;
        double mod = 0.4D;

        while (blocks > 0) {
            blocks--;
            done++;

            curPrice += mod;

            if (done == 250) {
                done = 0;
                mod += 0.4D;
            }
        }

        if(buying){
            if(team != null){
                curPrice += (500 * team.getClaims().size());
            }
        }

        // ALPHA
        curPrice /= 2.0D;

        return (int) curPrice;
    }

    @Override
	public boolean equals(Object o) {
        return o instanceof Claim && ((Claim) o).getMaximumPoint().equals(getMaximumPoint()) && ((Claim) o).getMinimumPoint().equals(getMinimumPoint());
    }

	/**
	 * Gets the minimum point of the region
	 * 
	 * @return minimum point
	 */
	public Location getMinimumPoint() {
		return new Location(Bukkit.getWorld("world"), Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
	}

	/**
	 * Gets the maximum point of the region
	 * 
	 * @return maximum point
	 */
	public Location getMaximumPoint() {
		return new Location(Bukkit.getWorld("world"), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
	}

	/**
	 * Return true if the point at (x,y,z) is contained within this region.
	 * 
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 * @param z
	 *            the Z coordinate
	 * @return true if the given point is within this region, false otherwise
	 */
	public boolean contains(int x, int y, int z) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
	}

	public boolean contains(int x, int z) {
		return x >= x1 && x <= x2 && z >= z1 && z <= z2;
	}

	/**
	 * Check if the given location is contained within this region.
	 * 
	 * @param l
	 *            the location to check for
	 * @return true if the location is within this region, false otherwise
	 */
	public boolean contains(Location l) {
        return "world".equals(l.getWorld().getName()) && contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

	/**
	 * Checks if the given Block is contained within the region
	 * 
	 * @param b
	 *            the block to check for
	 * @return true if the Location is within this Region, false otherwise
	 */
	public boolean contains(Block b) {
		return contains(b.getLocation());
	}

	/**
	 * Checks if the given Player's location is contained within the region
	 * 
	 * @param p
	 *            the player to check for
	 * @return true if the Location is within this region, false otherwise
	 */
	public boolean contains(Player p) {
		return contains(p.getLocation());
	}

	/**
	 * Gets a set of players that is in the region
	 * 
	 * @return set of players in the region
	 */
	public HashSet<Player> getPlayers() {
		HashSet<Player> players = new HashSet<Player>();
		for (Player p : Bukkit.getWorld("world").getPlayers()) {
			if (contains(p))
				players.add(p);
		}
		return players;
	}

	@Override
	public int hashCode() {
		return getMaximumPoint().hashCode() + getMinimumPoint().hashCode();
	}

	@Override
	public String toString() {
		Location corner1 = getMinimumPoint();
		Location corner2 = getMaximumPoint();

		return corner1.getBlockX() + ":" + corner1.getBlockY() + ":" + corner1.getBlockZ() + ":" + corner2.getBlockX() + ":" + corner2.getBlockY() + ":" + corner2.getBlockZ() + ":" + name;
	}

	public CuboidRegion getCuboidRegion() {
		CuboidRegion cr = new CuboidRegion("this", getMinimumPoint(), getMaximumPoint());

		return cr;
	}

	public String getFriendlyName() {
		return "(" + x1 + ", " + y1 + ", " + z1 + ") - (" + x2 + ", " + y2 + ", " + z2 + ")";
	}

	public Claim expand(CuboidDirection dir, int amount) {
		switch (dir) {
		case North:
			return new Claim(this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);
		case South:
			return new Claim(this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);
		case East:
			return new Claim(this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);
		case West:
			return new Claim(this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);
		case Down:
			return new Claim(this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);
		case Up:
			return new Claim(this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
		default:
			throw new IllegalArgumentException("Invalid direction " + dir);
		}
	}

	public Claim outset(CuboidDirection dir, int amount) {
		Claim c;
		switch (dir) {
		case Horizontal:
			c = expand(CuboidDirection.North, amount).expand(CuboidDirection.South, amount).expand(CuboidDirection.East, amount).expand(CuboidDirection.West, amount);
			break;
		case Vertical:
			c = expand(CuboidDirection.Down, amount).expand(CuboidDirection.Up, amount);
			break;
		case Both:
			c = outset(CuboidDirection.Horizontal, amount).outset(CuboidDirection.Vertical, amount);
			break;
		default:
			throw new IllegalArgumentException("Invalid direction " + dir);
		}
		return c;
	}

	public boolean isWithin(int x, int z, int radius) {
		return outset(CuboidDirection.Both, radius).contains(x, z);
	}

	public Location[] getCornerLocations() {
		return new Location[] {
				new Location(Bukkit.getWorld("world"), x1, y1, z1),
				new Location(Bukkit.getWorld("world"), x2, y1, z2),
				new Location(Bukkit.getWorld("world"), x1, y1, z2),
				new Location(Bukkit.getWorld("world"), x2, y1, z1) };
	}

	@Override
	public Claim clone() {
		return new Claim(x1, y1, z1, x2, y2, z2);
	}

	@Override
	public Iterator<Coordinate> iterator() {
		return new BorderIterator("world", x1, y1, z1, x2, y2, z2);
	}

	public static enum BorderDirection {
		POS_X,
		POS_Z,
		NEG_X,
		NEG_Z

	}

	public class BorderIterator implements Iterator<Coordinate> {
		private int x, z;
		private boolean next = true;
		private BorderDirection dir = BorderDirection.POS_Z;

		int maxX = getMaximumPoint().getBlockX(),
				maxZ = getMaximumPoint().getBlockZ();
		int minX = getMinimumPoint().getBlockX(),
				minZ = getMinimumPoint().getBlockZ();

		public BorderIterator(String world, int x1, int y1, int z1, int x2, int y2, int z2) {

			x = Math.min(x1, x2);
			z = Math.min(z1, z2);

		}

		@Override
		public boolean hasNext() {
			return next;
		}

		@Override
		public Coordinate next() {
			
			if (dir == BorderDirection.POS_Z) {
				if (++z == maxZ) {
					dir = BorderDirection.POS_X;
				}
			} else if (dir == BorderDirection.POS_X) {
				if (++x == maxX) {
					dir = BorderDirection.NEG_Z;
				}
			} else if (dir == BorderDirection.NEG_Z) {
				if (--z == minZ) {
					dir = BorderDirection.NEG_X;
				}
			} else if (dir == BorderDirection.NEG_X) {
				if (--x == minX) {
					next = false;
				}
			}
			
			return new Coordinate(x, z);
		}

		@Override
		public void remove() {}

	}

	public enum CuboidDirection {
		North,
		East,
		South,
		West,
		Up,
		Down,
		Horizontal,
		Vertical,
		Both,
		Unknown;

		public CuboidDirection opposite() {
			switch (this) {
			case North:
				return South;
			case East:
				return West;
			case South:
				return North;
			case West:
				return East;
			case Horizontal:
				return Vertical;
			case Vertical:
				return Horizontal;
			case Up:
				return Down;
			case Down:
				return Up;
			case Both:
				return Both;
			default:
				return Unknown;
			}
		}

	}

    public static enum SpecialTag {
        SPAWN,
        KOTH
    }

}