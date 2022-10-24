package xaerohax.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.settings.ModSettings;

@Mixin(value = ModSettings.class, remap = false)
public class ModSettingsMixin {
	@Final
	@Mutable
	@Shadow
	public static final float[] zooms = new float[]{0.25F, 0.5F, 0.75F, 1.0F, 1.25F, 1.5F, 1.75F, 2.0F, 2.25F, 2.5F, 2.75F, 3.0F, 3.25F, 3.5F, 3.75F, 4.0F, 4.25F, 4.5F, 4.75F, 5.0F};
}
