package baguchan.better_ai.util;

public enum BlockPath {
	BLOCKED(-1.0F),
	OPEN(0.0F),
	WALKABLE(0.0F),
	WALKABLE_DOOR(0.0F),
	WATER(-1.0F),
	DANGER(-1.0F),
	FIRE(-8.0F),
	LAVA(-8.0F),
	DOOR_OPEN(0.0F),
	DOOR_WOOD_CLOSED(-1.0F),
	DOOR_IRON_CLOSED(-1.0F),
	;

	private final float malus;

	private BlockPath(float p_77123_) {
		this.malus = p_77123_;
	}

	public float getMalus() {
		return this.malus;
	}
}
