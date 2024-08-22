package baguchan.better_ai.util;

public class BlockPath {
	public static final BlockPath BLOCKED = new BlockPath(-1.0F);
	public static final BlockPath OPEN = new BlockPath(0.0F);
	public static final BlockPath WALKABLE = new BlockPath(0.0F);
	public static final BlockPath WALKABLE_DOOR = new BlockPath(0.0F);
	public static final BlockPath WATER = new BlockPath(0.0F);
	public static final BlockPath DANGER = new BlockPath(8.0F);
	public static final BlockPath DAMAGE = new BlockPath(-1.0F);
	public static final BlockPath DANGER_FIRE = new BlockPath(8.0F);
	public static final BlockPath DAMAGE_FIRE = new BlockPath(16.0F);
	public static final BlockPath LAVA = new BlockPath(-1.0F);
	public static final BlockPath DOOR_OPEN = new BlockPath(0.0F);
	public static final BlockPath DOOR_WOOD_CLOSED = new BlockPath(-1.0F);
	public static final BlockPath DOOR_IRON_CLOSED = new BlockPath(-1.0F);
	public static final BlockPath FENCE = new BlockPath(-1.0F);
	public static final BlockPath UNPASSABLE_RAIL = new BlockPath(-1.0F);
	public static final BlockPath TRAPDOOR = new BlockPath(0.0F);
	private final float malus;

	private BlockPath(float p_77123_) {
		this.malus = p_77123_;
	}

	public float getMalus() {
		return this.malus;
	}
}
