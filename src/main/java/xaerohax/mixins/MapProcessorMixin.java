package xaerohax.mixins;

import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.map.MapProcessor;

@Mixin(value = MapProcessor.class, remap = false)
public abstract class MapProcessorMixin {

	@Redirect(
		method = "updateCaveStart",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/dimension/DimensionType;logicalHeight()I",
			remap = true
		),
		remap = false
	)
	private int modifyHeight(DimensionType instance) {
		return instance.height() + 1;
	}
}
