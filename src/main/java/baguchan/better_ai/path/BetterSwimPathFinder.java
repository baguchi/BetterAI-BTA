package baguchan.better_ai.path;

import baguchan.better_ai.api.IBlockPathGetter;
import baguchan.better_ai.util.BlockPath;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;

public class BetterSwimPathFinder extends BetterPathFinder {
	private final World worldSource;

	public BetterSwimPathFinder(World worldSource, MobPathfinder entityPathfinder) {
		super(worldSource, entityPathfinder);
		this.worldSource = worldSource;
	}
	protected int getNeighbors(Entity entity, BetterNode pathpoint, BetterNode pathpoint1, BetterNode pathpoint2, float f) {
		int i = 0;
		int j = 0;
		BetterNode pathpoint3 = this.getBetterNode(entity, pathpoint.x, pathpoint.y, pathpoint.z + 1, pathpoint1, j);
		BetterNode pathpoint4 = this.getBetterNode(entity, pathpoint.x - 1, pathpoint.y, pathpoint.z, pathpoint1, j);
		BetterNode pathpoint5 = this.getBetterNode(entity, pathpoint.x + 1, pathpoint.y, pathpoint.z, pathpoint1, j);
		BetterNode pathpoint6 = this.getBetterNode(entity, pathpoint.x, pathpoint.y, pathpoint.z - 1, pathpoint1, j);
		BetterNode pathpoint7 = this.getBetterNode(entity, pathpoint.x, pathpoint.y - 1, pathpoint.z, pathpoint1, j);
		BetterNode pathpoint8 = this.getBetterNode(entity, pathpoint.x, pathpoint.y + 1, pathpoint.z, pathpoint1, j);

		if (pathpoint3 != null && !pathpoint3.closed && pathpoint3.distanceTo(pathpoint2) < f) {
			this.neighbors[i++] = pathpoint3;
		}

		if (pathpoint4 != null && !pathpoint4.closed && pathpoint4.distanceTo(pathpoint2) < f) {
			this.neighbors[i++] = pathpoint4;
		}

		if (pathpoint5 != null && !pathpoint5.closed && pathpoint5.distanceTo(pathpoint2) < f) {
			this.neighbors[i++] = pathpoint5;
		}

		if (pathpoint6 != null && !pathpoint6.closed && pathpoint6.distanceTo(pathpoint2) < f) {
			this.neighbors[i++] = pathpoint6;
		}

		if (pathpoint7 != null && !pathpoint7.closed && pathpoint7.distanceTo(pathpoint2) < f) {
			this.neighbors[i++] = pathpoint7;
		}

		if (pathpoint8 != null && !pathpoint8.closed && pathpoint8.distanceTo(pathpoint2) < f) {
			this.neighbors[i++] = pathpoint8;
		}

		return i;
	}


	protected BetterNode getBetterNode(Entity entity, int x, int y, int z, BetterNode pathpoint, int l) {
		BetterNode pathpoint1 = null;
		if (this.isFree(entity, x, y, z, pathpoint) == BlockPath.WATER) {
			pathpoint1 = this.getBetterNode(x, y, z);
		}

		if (pathpoint1 == null && l > 0 && this.isFree(entity, x, y + l, z, pathpoint) == BlockPath.WATER) {
			pathpoint1 = this.getBetterNode(x, y + l, z);
			y += l;
		}

		return pathpoint1;
	}

	protected BlockPath isFree(Entity entity, int x, int y, int z, BetterNode pathpoint) {
		boolean flag = false;
		boolean flag2 = false;
		boolean flag3 = false;
		if (pathpoint.x < 0) {
			flag = true;
		}
		if (pathpoint.y < 0) {
			flag2 = true;
		}
		if (pathpoint.z < 0) {
			flag3 = true;
		}

		Vec3 srcVec = Vec3.getPermanentVec3(x, y, z);
		Vec3 destVec = srcVec.add(pathpoint.x, pathpoint.y, pathpoint.z);
		AABB collisionBB = AABB.getPermanentBB(x - 1F, y - 1F, z - 1F, x, y, z).expand(pathpoint.x, pathpoint.y, pathpoint.z).move(pathpoint.x / 2, pathpoint.y / 2, pathpoint.z / 2);
		HitResult interceptPos = collisionBB.clip(srcVec, destVec);
		double possibleDist = 0.0;
		if (interceptPos != null) {
			possibleDist = srcVec.distanceTo(interceptPos.location);
		}


		for (int x1 = x + (flag ? pathpoint.x : 0); x1 < x + (flag ? 0 : pathpoint.x); ++x1) {
			for (int y1 = y + (flag2 ? pathpoint.y : 0); y1 < y + (flag2 ? 0 : pathpoint.y); ++y1) {
				for (int z1 = z + (flag3 ? pathpoint.z : 0); z1 < z + (flag3 ? 0 : pathpoint.z); ++z1) {
					double blockDistance = srcVec.distanceTo(Vec3.getPermanentVec3(x1, y1, z1));
					if (blockDistance < possibleDist) {
						int k1 = this.worldSource.getBlockId(x1, y1, z1);
						if (k1 > 0) {
							Block block = Blocks.blocksList[k1];
							Material material = block.getMaterial();
							if (material == Material.water) {
								return BlockPath.WATER;
							}

							if (material.blocksMotion()) {
								return BlockPath.BLOCKED;
							}

							if (block.getLogic() instanceof IBlockPathGetter) {
								return ((IBlockPathGetter) block.getLogic()).getBlockPath();
							}
						}
					}
				}
			}
		}

		return BlockPath.OPEN;
	}
}
