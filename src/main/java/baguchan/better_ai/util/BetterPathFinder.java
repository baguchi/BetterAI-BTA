package baguchan.better_ai.util;

import baguchan.better_ai.IPath;
import net.minecraft.core.HitResult;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockDoor;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.IdHashMap;
import net.minecraft.core.world.pathfinder.Node;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;

public class BetterPathFinder {
	private final World worldSource;
	private final BetterBinaryHeap openSet = new BetterBinaryHeap();
	private final IdHashMap closedSet = new IdHashMap();
	private final BetterNode[] neighbors = new BetterNode[32];

	public BetterPathFinder(World worldSource) {
		this.worldSource = worldSource;
	}

	public BetterPath findPath(Entity entity, Entity target, float distance) {
		return this.findPath(entity, target.x, target.bb.minY, target.z, distance);
	}

	public BetterPath findPath(Entity entity, int xt, int yt, int zt, float distance) {
		return this.findPath(entity, (double) ((float) xt + 0.5F), (double) ((float) yt + 0.5F), (double) ((float) zt + 0.5F), distance);
	}

	private BetterPath findPath(Entity entity, double xt, double yt, double zt, float distance) {
		this.openSet.clear();
		this.closedSet.clear();
		BetterNode pathpoint = this.getBetterNode(MathHelper.floor_double(entity.bb.minX), MathHelper.floor_double(entity.bb.minY), MathHelper.floor_double(entity.bb.minZ));
		BetterNode pathpoint1 = this.getBetterNode(MathHelper.floor_double(xt - (double) (entity.bbWidth / 2.0F)), MathHelper.floor_double(yt), MathHelper.floor_double(zt - (double) (entity.bbWidth / 2.0F)));
		BetterNode pathpoint2 = new BetterNode(MathHelper.floor_float(entity.bbWidth + 1.0F), MathHelper.floor_float(entity.bbHeight + 1.0F), MathHelper.floor_float(entity.bbWidth + 1.0F));
		BetterPath pathentity = this.findPath(entity, pathpoint, pathpoint1, pathpoint2, distance);

		if (entity instanceof IPath) {
			List<Node> nodeList = Lists.newArrayList();
			if (pathentity != null && pathentity.getNodes() != null) {
				for (Node node : pathentity.getNodes()) {
					if (node != null) {
						nodeList.add(node);
					}
				}
			}
			((IPath) entity).setCurrentPath(nodeList);
		}
		return pathentity;
	}

	private BetterPath findPath(Entity entity, BetterNode pathpoint, BetterNode pathpoint1, BetterNode pathpoint2, float f) {
		pathpoint.g = 0.0F;
		pathpoint.h = pathpoint.distanceTo(pathpoint1);
		pathpoint.f = pathpoint.h;
		this.openSet.clear();
		this.openSet.insert(pathpoint);
		BetterNode pathpoint3 = pathpoint;

		boolean flag = false;
		while (!this.openSet.isEmpty()) {
			BetterNode pathpoint4 = this.openSet.pop();
			if (pathpoint4.equals(pathpoint1)) {
				return this.reconstructPath(pathpoint1);
			}

			if (pathpoint4.distanceTo(pathpoint1) < pathpoint3.distanceTo(pathpoint1)) {
				pathpoint3 = pathpoint4;
			}

			pathpoint4.closed = true;
			int i = this.getNeighbors(entity, pathpoint4, pathpoint2, pathpoint1, f);

			for (int j = 0; j < i; ++j) {
				BetterNode pathpoint5 = this.neighbors[j];
				float f1 = pathpoint4.g + pathpoint4.distanceTo(pathpoint5);
				if (!pathpoint5.inOpenSet() || f1 < pathpoint5.g) {
					pathpoint5.cameFrom = pathpoint4;
					pathpoint5.g = f1;
					pathpoint5.h = pathpoint5.distanceTo(pathpoint1);
					if (pathpoint5.inOpenSet()) {
						this.openSet.changeCost(pathpoint5, pathpoint5.g + pathpoint5.h);
					} else {
						pathpoint5.f = pathpoint5.g + pathpoint5.h;
						this.openSet.insert(pathpoint5);
					}
				}
			}
			if (flag) {
				break;
			}
		}

		if (pathpoint3 == pathpoint) {
			return null;
		} else {
			return this.reconstructPath(pathpoint3);
		}
	}

	private int getNeighbors(Entity entity, BetterNode pathpoint, BetterNode pathpoint1, BetterNode pathpoint2, float f) {
		int i = 0;
		int j = 0;
		if (this.isFree(entity, pathpoint.x, pathpoint.y + 1, pathpoint.z, pathpoint1) == 1) {
			j = 1;
		}

		BetterNode pathpoint3 = this.getBetterNode(entity, pathpoint.x, pathpoint.y, pathpoint.z + 1, pathpoint1, j);
		BetterNode pathpoint4 = this.getBetterNode(entity, pathpoint.x - 1, pathpoint.y, pathpoint.z, pathpoint1, j);
		BetterNode pathpoint5 = this.getBetterNode(entity, pathpoint.x + 1, pathpoint.y, pathpoint.z, pathpoint1, j);
		BetterNode pathpoint6 = this.getBetterNode(entity, pathpoint.x, pathpoint.y, pathpoint.z - 1, pathpoint1, j);
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

		return i;
	}

	private BetterNode getBetterNode(Entity entity, int x, int y, int z, BetterNode pathpoint, int l) {
		BetterNode pathpoint1 = null;
		if (this.isFree(entity, x, y, z, pathpoint) == 1) {
			pathpoint1 = this.getBetterNode(x, y + l, z);
		}


		if (entity instanceof IPath) {
			if (!((IPath) entity).canSwimLava()) {
				if (this.isFree(entity, x, y - 1, z, pathpoint) == -2) {
					return null;
				}
			}
		}

		if (pathpoint1 == null && l > 0 && this.isFree(entity, x, y + l, z, pathpoint) == 1) {
			pathpoint1 = this.getBetterNode(x, y + l, z);
			y += l;
		}

		if (pathpoint1 != null) {
			int i1 = 0;
			int j1 = 0;

			while (y > 0 && (j1 = this.isFree(entity, x, y - 1, z, pathpoint)) == 1) {
				++i1;
				if (i1 >= 4) {
					return null;
				}

				--y;
				if (y > 0) {
					pathpoint1 = this.getBetterNode(x, y, z);
				}
			}

			if (j1 == -2) {
				return null;
			}
		}

		return pathpoint1;
	}

	private final BetterNode getBetterNode(int x, int y, int z) {
		int l = BetterNode.createHash(x, y, z);
		BetterNode pathpoint = (BetterNode) this.closedSet.get(l);
		if (pathpoint == null) {
			pathpoint = new BetterNode(x, y, z);
			this.closedSet.add(l, pathpoint);
		}

		return pathpoint;
	}

	private int isFree(Entity entity, int x, int y, int z, BetterNode pathpoint) {
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

		Vec3d srcVec = Vec3d.createVector(x, y, z);
		Vec3d destVec = srcVec.addVector(pathpoint.x, pathpoint.y, pathpoint.z);
		AABB collisionBB = AABB.getBoundingBox(x - 1F, y - 1F, z - 1F, x, y, z).expand(pathpoint.x, pathpoint.y, pathpoint.z).offset(pathpoint.x / 2, pathpoint.y / 2, pathpoint.z / 2);
		HitResult interceptPos = collisionBB.func_1169_a(srcVec, destVec);
		double possibleDist = 0.0;
		if (interceptPos != null) {
			possibleDist = srcVec.distanceTo(interceptPos.location);
		}


		for (int x1 = x + (flag ? pathpoint.x : 0); x1 < x + (flag ? 0 : pathpoint.x); ++x1) {
			for (int y1 = y + (flag2 ? pathpoint.y : 0); y1 < y + (flag2 ? 0 : pathpoint.y); ++y1) {
				for (int z1 = z + (flag3 ? pathpoint.z : 0); z1 < z + (flag3 ? 0 : pathpoint.z); ++z1) {
					double blockDistance = srcVec.distanceTo(Vec3d.createVector(x1, y1, z1));
					if (blockDistance < possibleDist) {
						int k1 = this.worldSource.getBlockId(x1, y1, z1);
						if (k1 > 0) {
							if (Block.blocksList[k1] instanceof BlockDoor) {
								int l1 = this.worldSource.getBlockMetadata(x1, y1, z1);
								if (!BlockDoor.isOpen(l1)) {
									return 0;
								}
							} else {
								Material material = Block.blocksList[k1].blockMaterial;
								if (material.blocksMotion()) {
									return 0;
								}

								if (material == Material.water) {
									return -1;
								}

								if (material == Material.lava || material == Material.fire) {
									return -2;
								}
							}
						}
					}
				}
			}
		}


		return 1;
	}

	private BetterPath reconstructPath(BetterNode p_77435_) {
		List<BetterNode> list = com.google.common.collect.Lists.newArrayList();
		BetterNode node = p_77435_;
		list.add(0, p_77435_);
		int i = 1;

		while (node.cameFrom != null) {
			node = node.cameFrom;
			list.add(i, node);
			i++;
		}

		return new BetterPath(list.toArray(list.toArray(new BetterNode[i])));
	}
}
