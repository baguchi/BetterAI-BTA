package baguchan.better_ai.path;

import baguchan.better_ai.util.BlockPath;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.pathfinder.Node;

public class BetterNode extends Node {
	public final int x;
	public final int y;
	public final int z;
	public final int hash;
	int heapIdx = -1;
	public float g;
	public float h;
	public float f;
	public BetterNode cameFrom;
	public float costMalus;
	public boolean closed = false;
	public BlockPath type = BlockPath.BLOCKED;

	public BetterNode(int x, int y, int z) {
		super(x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
		this.hash = createHash(x, y, z);
	}

	public static int createHash(int i, int j, int k) {
		return j & 255 | (i & 32767) << 8 | (k & 32767) << 24 | (i >= 0 ? 0 : Integer.MIN_VALUE) | (k >= 0 ? 0 : '耀');
	}

	public float distanceManhattan(Vec3 p_77307_) {
		float f = (float) Math.abs(p_77307_.x - this.x);
		float f1 = (float) Math.abs(p_77307_.y - this.y);
		float f2 = (float) Math.abs(p_77307_.z - this.z);
		return f + f1 + f2;
	}

	public float distanceTo(Node other) {
		float f = (float) (other.x - this.x);
		float f1 = (float) (other.y - this.y);
		float f2 = (float) (other.z - this.z);
		return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
	}

	public boolean equals(Object that) {
		if (!(that instanceof BetterNode)) {
			return false;
		} else {
			BetterNode nThat = (BetterNode) that;
			return this.hash == nThat.hash && this.x == nThat.x && this.y == nThat.y && this.z == nThat.z;
		}
	}

	public int hashCode() {
		return this.hash;
	}

	public boolean inOpenSet() {
		return this.heapIdx >= 0;
	}

	public String toString() {
		return this.x + ", " + this.y + ", " + this.z;
	}
}
