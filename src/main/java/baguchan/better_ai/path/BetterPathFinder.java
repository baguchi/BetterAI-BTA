package baguchan.better_ai.path;

import baguchan.better_ai.api.IBlockPathGetter;
import baguchan.better_ai.api.IPath;
import baguchan.better_ai.api.IPathGetter;
import baguchan.better_ai.util.BlockPath;
import com.google.common.collect.Lists;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.util.collection.IntHashMap;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weathers;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static baguchan.better_ai.util.BlockPath.*;

public class BetterPathFinder {
	private final World worldSource;
	private final BetterBinaryHeap openSet = new BetterBinaryHeap();
	private final IntHashMap closedSet = new IntHashMap();
	protected final BetterNode[] neighbors = new BetterNode[32];
	private BetterPath path;
	public final MobPathfinder mobPathfinder;

	public BetterPathFinder(World worldSource, MobPathfinder mobPathfinder) {
		this.worldSource = worldSource;
		this.mobPathfinder = mobPathfinder;
	}


	public boolean moveTo(Entity entity, Entity target, float distance) {
		BetterPath path = this.findPath(entity, target, distance);
		return path != null && this.moveTo(path);
	}

	public boolean moveTo(Entity entity, int x, int y, int z, float distance) {
		BetterPath path = this.findPath(entity, x, y, z, distance);
		return path != null && this.moveTo(path);
	}

	public boolean isDone() {
		return this.path == null || this.path.isDone();
	}

	public boolean isInProgress() {
		return !this.isDone();
	}

	public BetterPath getPath() {
		return path;
	}

	public boolean moveTo(@Nullable BetterPath p_26537_) {
		if (p_26537_ == null) {
			this.path = null;
			return false;
		} else {
			if (!p_26537_.sameAs(this.path)) {
				this.path = p_26537_;
			}

			if (this.isDone()) {
				return false;
			} else {
				this.trimPath();
				if (this.path.getNodes().length <= 0) {
					return false;
				} else {
					//this.speedModifier = p_26538_;
					return true;
				}
			}
		}
	}

	protected void trimPath() {
		if (this.path != null) {

			if (this.worldSource.canBlockSeeTheSky(MathHelper.floor(this.mobPathfinder.x), MathHelper.floor(this.mobPathfinder.y + 0.5F), MathHelper.floor(this.mobPathfinder.z))) {
				return;
			}

			for (int i = 0; i < this.path.getNodes().length; i++) {
				BetterNode node = this.path.getNodes()[i];
				if (((IPathGetter) mobPathfinder).canHideFromSkyLight() && this.worldSource.isDaytime() && this.worldSource.canBlockSeeTheSky(MathHelper.floor(node.x), MathHelper.floor(node.y), MathHelper.floor(node.z)) && (this.worldSource.getCurrentWeather() != Weathers.OVERWORLD_FOG || this.worldSource.weatherManager.getWeatherPower() < 0.75F)) {
					this.path.truncateNodes(i);
					return;
				}
			}

		}
	}

	public BetterPath findPath(Entity entity, Entity target, float distance) {
		return this.findPath(entity, target.x, target.bb.minY, target.z, distance);
	}

	public BetterPath findPath(Entity entity, int xt, int yt, int zt, float distance) {
		return this.findPath(entity, (float) xt + 0.5F, (double) ((float) yt - 0.5F), (double) ((float) zt + 0.5F), distance);
	}

	protected BetterPath findPath(Entity entity, double xt, double yt, double zt, float distance) {
		this.openSet.clear();
		this.closedSet.clear();
		BetterNode pathpoint = this.getBetterNode(MathHelper.floor(entity.bb.minX), MathHelper.floor(entity.bb.minY), MathHelper.floor(entity.bb.minZ));
		BetterNode pathpoint1 = this.getBetterNode(MathHelper.floor(xt - (double) (entity.bbWidth / 2.0F)), MathHelper.floor(yt), MathHelper.floor(zt - (double) (entity.bbWidth / 2.0F)));
		BetterNode pathpoint2 = new BetterNode(MathHelper.floor_float(entity.bbWidth + 1.0F), MathHelper.floor_float(entity.bbHeight + 1.0F), MathHelper.floor_float(entity.bbWidth + 1.0F));
		BetterPath pathentity = this.findPath(entity, pathpoint, pathpoint1, pathpoint2, distance);

		if (entity instanceof IPath) {
			List<BetterNode> nodeList = Lists.newArrayList();
			if (pathentity != null && pathentity.getNodes() != null) {
				for (BetterNode node : pathentity.getNodes()) {
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

		while (!this.openSet.isEmpty()) {
			BetterNode pathpoint4 = this.openSet.pop();
			if (pathpoint4.equals(pathpoint1)) {
				return this.reconstructPath(pathpoint1);
			}

			pathpoint3 = pathpoint4;


			pathpoint4.closed = true;
			int i = this.getNeighbors(entity, pathpoint4, pathpoint2, pathpoint1, f);

			for (int j = 0; j < i; ++j) {
				BetterNode pathpoint5 = this.neighbors[j];
				float f1 = pathpoint4.g + pathpoint4.distanceTo(pathpoint5) + pathpoint4.costMalus;
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
		}

		if (pathpoint3 == pathpoint) {
			return null;
		} else {
			return this.reconstructPath(pathpoint3);
		}
	}

	protected int getNeighbors(Entity entity, BetterNode pathpoint, BetterNode pathpoint1, BetterNode pathpoint2, float f) {
		int i = 0;
		int j = 0;
		BlockPath pathtype = this.getBlockPathStatic(worldSource, pathpoint.x, pathpoint.y + 1, pathpoint.z);
		BlockPath pathtype1 = this.getBlockPathStatic(worldSource, pathpoint.x, pathpoint.y, pathpoint.z);
		if (mobPathfinder instanceof IPathGetter && ((IPathGetter) this.mobPathfinder).getPathfindingMalus(this.mobPathfinder, pathtype) >= 0.0F) {
			j = MathHelper.floor_float(Math.max(1.0F, 1.0F));
		}

		double d0 = getFloorLevel(pathpoint.x, pathpoint.y, pathpoint.z);

		for (Direction direction : Direction.horizontalDirections) {
			BetterNode pathpoint3 = this.findAcceptedNode(pathpoint.x + direction.getOffsetX(), pathpoint.y, pathpoint.z + direction.getOffsetZ(), j, d0, direction);
			if (pathpoint3 != null && !pathpoint3.closed && pathpoint3.distanceTo(pathpoint2) < f) {
				if (isNeighborValid(pathpoint3, pathpoint))
					this.neighbors[i++] = pathpoint3;
			}
		}

		for (Direction direction1 : Direction.horizontalDirections) {
			Direction direction2 = direction1.rotate(1);
			if (this.isDiagonalValid(pathpoint, this.neighbors[direction1.getId()], this.neighbors[direction2.getId()])) {

				BetterNode node1 = this.findAcceptedNode(
					pathpoint.x + direction1.getOffsetX() + direction2.getOffsetX(),
					pathpoint.y,
					pathpoint.z + direction1.getOffsetZ() + direction2.getOffsetZ(),
					j,
					d0,
					direction1
				);
				if (this.isDiagonalValid(node1)) {
					neighbors[i++] = node1;
				}
			}
		}

		return i;
	}

	@Nullable
	protected BetterNode findAcceptedNode(int x, int y, int z, int height, double floorHeight, Direction direction) {
		BetterNode node = null;
		double d0 = this.getFloorLevel(x, y, z);
		int mobJumpHeight = 1;
		if (d0 - floorHeight > mobJumpHeight) {
			return null;
		} else {
			BlockPath pathtype = this.getBlockPathStatic(this.worldSource, x, y, z);
			BlockPath pathtype2 = this.getBlockPathStatic(this.worldSource, x, y + 1, z);
			if (this.mobPathfinder instanceof IPathGetter) {
				float f = ((IPathGetter) this.mobPathfinder).getPathfindingMalus(this.mobPathfinder, pathtype == null ? BlockPath.BLOCKED : pathtype);
				if (f >= 0.0F) {
					node = this.getNodeAndUpdateCostToMax(x, y, z, pathtype, f);
				}
			}

			if (doesBlockHavePartialCollision(pathtype) && node != null && node.costMalus >= 0.0F && !this.canReachWithoutCollision(node)) {
				node = null;
			}

			if (pathtype != WALKABLE && (!this.isAmphibious() || pathtype != WATER)) {
				if ((node == null || node.costMalus < 0.0F)
					&& height > 0
					&& (pathtype != BlockPath.FENCE || this.canWalkOverFences())
					&& pathtype != BlockPath.UNPASSABLE_RAIL
					&& pathtype != BlockPath.TRAPDOOR
					&& pathtype != DANGER_FIRE
					&& pathtype != DANGER) {
					node = this.tryJumpOn(x, y, z, height, floorHeight, direction);
				} else if (!this.isAmphibious() && pathtype == WATER && !this.canFloat()) {
					node = this.tryFindFirstNonWaterBelow(x, y, z, node);
				} else if (pathtype == BlockPath.OPEN) {
					node = this.tryFindFirstGroundNodeBelow(x, y, z);
				} else if (doesBlockHavePartialCollision(pathtype) && node == null) {
					node = this.getClosedNode(x, y, z, pathtype);
				}

				return node;
			} else {
				return node;
			}
		}
	}

	protected boolean isDiagonalValid(BetterNode p_326907_, @Nullable BetterNode p_326803_, @Nullable BetterNode p_326821_) {
		if (p_326821_ == null || p_326803_ == null || p_326821_.y > p_326907_.y || p_326803_.y > p_326907_.y) {
			return false;
		} else if (p_326803_.type != BlockPath.WALKABLE_DOOR && p_326821_.type != BlockPath.WALKABLE_DOOR) {
			boolean flag = p_326821_.type == BlockPath.FENCE && p_326803_.type == BlockPath.FENCE && (double) this.mobPathfinder.bbWidth < 0.5;
			return (p_326821_.y < p_326907_.y || p_326821_.costMalus >= 0.0F || flag) && (p_326803_.y < p_326907_.y || p_326803_.costMalus >= 0.0F || flag);
		} else {
			return false;
		}
	}

	protected boolean isDiagonalValid(@Nullable BetterNode p_77630_) {
		if (p_77630_ == null || p_77630_.closed) {
			return false;
		} else {
			return p_77630_.type == BlockPath.WALKABLE_DOOR ? false : p_77630_.costMalus >= 0.0F;
		}
	}

	@Nullable
	private BetterNode tryFindFirstNonWaterBelow(int p_326959_, int p_326927_, int p_326932_, @Nullable BetterNode p_326880_) {
		p_326927_--;

		int minBuild = 0;
		while (p_326927_ > minBuild) {
			BlockPath pathtype = this.getBlockPathStatic(worldSource, p_326959_, p_326927_, p_326932_);
			if (pathtype != WATER) {
				return p_326880_;
			}

			p_326880_ = this.getNodeAndUpdateCostToMax(p_326959_, p_326927_, p_326932_, pathtype, ((IPathGetter) this.mobPathfinder).getPathfindingMalus(this.mobPathfinder, pathtype));
			p_326927_--;
		}

		return p_326880_;
	}

	private static boolean doesBlockHavePartialCollision(BlockPath p_326827_) {
		return p_326827_ == BlockPath.FENCE || p_326827_ == BlockPath.DOOR_WOOD_CLOSED || p_326827_ == BlockPath.DOOR_IRON_CLOSED;
	}


	@Nullable
	private BetterNode tryJumpOn(
		int x,
		int y,
		int z,
		int height,
		double floor,
		Direction direction
	) {
		BetterNode node = this.findAcceptedNode(x, y + 1, z, height - 1, floor, direction);
		if (node == null) {
			return null;
		} else if (this.mobPathfinder.bbWidth >= 1.0F) {
			return node;
		} else if (node.type != BlockPath.OPEN && node.type != WALKABLE) {
			return node;
		} else {
			double d0 = (double) (x - direction.getOffsetX()) + 0.5;
			double d1 = (double) (z - direction.getOffsetZ()) + 0.5;
			double d2 = (double) this.mobPathfinder.bbHeight / 2.0;
			AABB aabb = AABB.getPermanentBB(
				d0 - d2,
				this.getFloorLevel((int) d0, (y + 1), (int) d1) + 0.001,
				d1 - d2,
				d0 + d2,
				(double) this.mobPathfinder.bbHeight + this.getFloorLevel(node.x, node.y, node.z) - 0.002,
				d1 + d2
			);
			return !this.worldSource.getCubes(mobPathfinder, aabb).isEmpty() ? null : node;
		}
	}

	private boolean canReachWithoutCollision(BetterNode p_77625_) {
		AABB aabb = this.mobPathfinder.bb;
		Vec3 vec3 = Vec3.getPermanentVec3(
			(double) p_77625_.x - this.mobPathfinder.x + (aabb.maxX - aabb.minX) / 2.0,
			(double) p_77625_.y - this.mobPathfinder.y + (aabb.maxY - aabb.minY) / 2.0,
			(double) p_77625_.z - this.mobPathfinder.z + (aabb.maxZ - aabb.minZ) / 2.0
		);
		int i = (int) Math.ceil(vec3.length() / aabb.getSize());
		vec3 = Vec3.getPermanentVec3(vec3.x * (double) (1.0F / (float) i), vec3.y * (double) (1.0F / (float) i), vec3.z * (double) (1.0F / (float) i));

		for (int j = 1; j <= i; j++) {
			aabb = aabb.move(vec3.x, vec3.y, vec3.z);
			if (!this.worldSource.getCubes(mobPathfinder, aabb).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	private boolean canWalkOverFences() {
		return false;
	}

	private boolean canFloat() {
		return true;
	}

	private boolean isAmphibious() {
		return false;
	}

	private double getFloorLevel(int x, int y, int z) {
		return this.worldSource.getBlock(x, y, z) == null ? y : this.worldSource.getBlock(x, y, z).getLogic().getBounds().maxY + y;
	}


	protected boolean isNeighborValid(@Nullable BetterNode p_77627_, BetterNode p_77628_) {
		return p_77627_ != null && !p_77627_.closed && (p_77627_.costMalus >= 0.0F || p_77628_.costMalus < 0.0F);
	}


	private BetterNode tryFindFirstGroundNodeBelow(int p_326892_, int p_326901_, int p_326809_) {
		int lowHeight = 0;
		for (int i = p_326901_ - 1; i >= lowHeight; i--) {
			int maxFall = 3;
			if (p_326901_ - i > maxFall) {
				return this.getBlockedNode(p_326892_, i, p_326809_);
			}

			BlockPath pathtype = this.getBlockPathStatic(worldSource, p_326892_, i, p_326809_);
			if (mobPathfinder instanceof IPathGetter) {
				float f = ((IPathGetter) mobPathfinder).getPathfindingMalus(mobPathfinder, pathtype);
				if (pathtype != BlockPath.OPEN) {
					if (f >= 0.0F) {
						return this.getNodeAndUpdateCostToMax(p_326892_, i, p_326809_, pathtype, f);
					}

					return this.getBlockedNode(p_326892_, i, p_326809_);
				}

			}
		}

		return this.getBlockedNode(p_326892_, p_326901_, p_326809_);
	}

	private BetterNode getNodeAndUpdateCostToMax(int x, int y, int z, BlockPath p_326789_, float p_230624_) {
		BetterNode node = this.getBetterNode(x, y, z);
		node.type = p_326789_;
		node.costMalus = Math.max(node.costMalus, p_230624_);
		return node;
	}

	private BetterNode getBlockedNode(int p_230628_, int p_230629_, int p_230630_) {
		BetterNode node = this.getBetterNode(p_230628_, p_230629_, p_230630_);
		node.costMalus = -1.0F;

		node.type = BlockPath.BLOCKED;
		return node;
	}

	private BetterNode getClosedNode(int p_326935_, int p_326904_, int p_326845_, BlockPath path) {
		BetterNode node = this.getBetterNode(p_326935_, p_326904_, p_326845_);
		node.closed = true;
		node.type = path;
		node.costMalus = path.getMalus();
		return node;
	}

	protected final BetterNode getBetterNode(int x, int y, int z) {
		int l = BetterNode.createHash(x, y, z);
		BetterNode pathpoint = (BetterNode) this.closedSet.get(l);
		if (pathpoint == null) {
			pathpoint = new BetterNode(x, y, z);
			this.closedSet.put(l, pathpoint);
		}

		return pathpoint;
	}

	public BlockPath getBlockPathStatic(World p_330755_, int x, int y, int z) {
		BlockPath pathtype = getBlockPath(x, y, z);
		BlockPath pathtype1 = getBlockPath(x, y - 1, z);

		if (pathtype == BlockPath.OPEN && y >= 1) {
			if (pathtype1 == OPEN || pathtype1 == WATER || pathtype1 == LAVA || pathtype1 == WALKABLE) {
				return BlockPath.OPEN;
			}
			if (pathtype1 == DAMAGE_FIRE) {
				return DANGER_FIRE;
			}
			return checkNeighbourBlocks(p_330755_, this.mobPathfinder, x, y, z, BlockPath.WALKABLE);
		} else {
			return pathtype;
		}
	}

	public BlockPath checkNeighbourBlocks(World p_331893_, Entity entity, int x, int y, int z, BlockPath p_326944_) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (i != 0 || k != 0) {
						BlockPath pathtype = getBlockPath(x, y, z);
						if (pathtype == BlockPath.DAMAGE) {
							return BlockPath.DANGER;
						}

						if (pathtype == BlockPath.DAMAGE_FIRE || pathtype == LAVA) {
							return BlockPath.DANGER_FIRE;
						}
					}
				}
			}
		}

		return p_326944_;
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
						BlockPath blockPath = this.getBlockPathStatic(worldSource, x1, y1, z1);
						if (blockPath != null) {
							return blockPath;
						}
					}
				}
			}
		}

		return BlockPath.OPEN;
	}

	private BlockPath getBlockPath(int x1, int y1, int z1) {
		int k1 = this.worldSource.getBlockId(x1, y1, z1);
		int k2 = this.worldSource.getBlockId(MathHelper.floor(this.mobPathfinder.x), MathHelper.floor(this.mobPathfinder.y), MathHelper.floor(this.mobPathfinder.z));
		if (k1 > 0) {
			if (Blocks.blocksList[k1].getLogic() instanceof BlockLogicDoor) {
				int l1 = this.worldSource.getBlockMetadata(x1, y1, z1);
				if (!BlockLogicDoor.isOpen(l1)) {
					return BlockPath.DOOR_OPEN;
				}
			} else {
				Block block = Blocks.getBlock(k1);
				Material material = block.getMaterial();


				if (material == Material.water) {
					return WATER;
				}

				if (material == Material.lava) {
					return this.checkSameBlockPath(k2, LAVA);
				}
				if (material == Material.fire) {
					return this.checkSameBlockPath(k2, DANGER_FIRE);
				}
				if (material.blocksMotion()) {
					return this.checkSameBlockPath(k2, BlockPath.BLOCKED);
				}

				if (block.getLogic() instanceof IBlockPathGetter) {
					return this.checkSameBlockPath(k2, ((IBlockPathGetter) block.getLogic()).getBlockPath());
				}
			}
		}
		return BlockPath.OPEN;
	}

	private BlockPath checkSameBlockPath(int id, BlockPath blockPath) {
		if (id > 0) {
			Block block = Blocks.getBlock(id);
			Material material2 = block.getMaterial();
			if (material2 == Material.lava && blockPath == LAVA || material2 == Material.fire && blockPath == DANGER_FIRE) {
				return DAMAGE_FIRE;
			}
			if (material2.blocksMotion() && blockPath == BLOCKED) {
				return DAMAGE;
			}

			if (block.getLogic() instanceof IBlockPathGetter && ((IBlockPathGetter) block.getLogic()).getBlockPath() == DANGER && blockPath == DANGER) {
				return DAMAGE;
			}
		}
		return blockPath;
	}

	private BetterPath reconstructPath(BetterNode p_77435_) {
		List<BetterNode> list = Lists.newArrayList();
		BetterNode node = p_77435_;

		int i = 1;
		list.add(0, node);
		while (node.cameFrom != null) {
			node = node.cameFrom;
			list.add(i, node);
			i++;
		}

		Collections.reverse(list);
		return new BetterPath(list.toArray(new BetterNode[i]));
	}
}
