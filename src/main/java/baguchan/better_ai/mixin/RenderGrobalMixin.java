package baguchan.better_ai.mixin;

import baguchan.better_ai.api.path.IPath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.Node;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(value = RenderGlobal.class, remap = false)
public class RenderGrobalMixin {

	@Shadow
	private Minecraft mc;
	@Shadow
	private World worldObj;

	@Inject(method = "drawDebugEntityOutlines", at = @At("HEAD"))
	public void drawDebugEntityOutlines(ICamera camera, float partialTicks, CallbackInfo ci) {

		GL11.glDisable(3553);
		GL11.glLineWidth(2.0F);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float entityRadius = 0.0F;
		if (this.mc.thePlayer.getGamemode() == Gamemode.creative) {
			entityRadius = 100.0F;
		}

		List<Entity> entitiesNearby = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.mc.thePlayer, this.mc.thePlayer.bb.expand((double) entityRadius, (double) entityRadius, (double) entityRadius));
		Iterator var13 = entitiesNearby.iterator();
		double x = camera.getX(partialTicks);
		double y = camera.getY(partialTicks);
		double z = camera.getZ(partialTicks);

		while (var13.hasNext()) {
			Entity e = (Entity) var13.next();
			if (e instanceof IPath) {
				IPath path = (IPath) e;
				if (path.getCurrentPath() != null) {
					for (Node node : path.getCurrentPath()) {
						if (node != null) {
							AABB aabb = new AABB(node.x - x, node.y - y, node.z - z, node.x + 1 - x, node.y + 0.1F - y, node.z + 1 - z);
							this.drawOutlinedBoundingBox(aabb);
						}
					}
				}
			}
		}

		GL11.glEnable(3553);
	}

	@Shadow
	private void drawOutlinedBoundingBox(AABB expand) {
	}
}
