package baguchan.better_ai.mixin;

import baguchan.better_ai.api.IHead;
import net.minecraft.client.render.entity.LivingRenderer;
import net.minecraft.core.entity.EntityLiving;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LivingRenderer.class, remap = false)
public class LivingRendererMixin {
	@Redirect(method = "doRenderLiving", at = @At(value = "FIELD", target = "Lnet/minecraft/core/entity/EntityLiving;xRot:F", opcode = Opcodes.GETFIELD))
	private float getXRot(EntityLiving instance) {
		if (instance instanceof IHead) {
			return ((IHead) instance).getXHeadRot();
		}
		return instance.xRot;
	}

	@Redirect(method = "doRenderLiving", at = @At(value = "FIELD", target = "Lnet/minecraft/core/entity/EntityLiving;xRotO:F", opcode = Opcodes.GETFIELD))
	private float getXRotO(EntityLiving instance) {
		if (instance instanceof IHead) {
			return ((IHead) instance).getXHeadRotO();
		}
		return instance.xRotO;
	}

	@Redirect(method = "doRenderLiving", at = @At(value = "FIELD", target = "Lnet/minecraft/core/entity/EntityLiving;yRot:F", opcode = Opcodes.GETFIELD))
	private float getYRot(EntityLiving instance) {
		if (instance instanceof IHead) {
			return ((IHead) instance).getYHeadRot();
		}
		return instance.yRot;
	}

	@Redirect(method = "doRenderLiving", at = @At(value = "FIELD", target = "Lnet/minecraft/core/entity/EntityLiving;yRotO:F", opcode = Opcodes.GETFIELD))
	private float getYRotO(EntityLiving instance) {
		if (instance instanceof IHead) {
			return ((IHead) instance).getYHeadRotO();
		}
		return instance.yRotO;
	}
}
