package hydraheadhunter.datastacks.mixin.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.lang.String.valueOf;

@Mixin(DrawContext.class)
public class DrawContextMixin {
	@Unique
	private static int adjustmentClock =0;
	@Shadow @Final private MatrixStack matrices;
	private static final float scalefactor4 = 0.69F;
	private static final int   xAdjust4 = 10;
	private static final int   yAdjust4 = -3;
	private static final float scalefactor3 = 0.90F;
	private static final int   xAdjust3 = 3;
	private static final int   yAdjust3 = -1;
	
	
	
	@Inject( method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at=@At("HEAD"), cancellable = true)
	private void overrideDrawFourDigits(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countOverride, CallbackInfo info) {
		DrawContext context= (DrawContext)(Object) this;
		int count = stack.getCount();
		if (count > 99) {
			float localScale = count > 999? scalefactor4:scalefactor3;
			int localXAdjust = count > 999? xAdjust4:xAdjust3;
			int localYAdjust = count > 999? yAdjust4:yAdjust3;
			
			this.matrices.push();
			if (stack.getCount() != 1 || countOverride != null) {
				String string = countOverride == null ? valueOf(count) : countOverride;
				this.matrices.scale(localScale, localScale, 1.0F);
				this.matrices.translate(0.0F, 0.0F, 200.0F);
				int xPos= (int)((x + 19 - 2 - textRenderer.getWidth(string) ) / localScale)+ localXAdjust;
				int yPos= (int)((y +6   + 3)/localScale) -localYAdjust;
				adjustmentClock= (adjustmentClock+1)%100;
				
				context.drawText(textRenderer, string, xPos, yPos, 16777215, true);
			}
			this.matrices.pop();
			info.cancel();
		}
	}
}