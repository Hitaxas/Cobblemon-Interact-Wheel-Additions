package net.starliteheart.cobblemon_iwa.common.gui

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.CobblemonRenderable
import com.cobblemon.mod.common.client.gui.summary.Summary
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import net.starliteheart.cobblemon_iwa.common.util.iwaResource

class ReorderButton(
    pX: Int, pY: Int,
    onPress: OnPress
) : Button(pX, pY, WIDTH.toInt(), HEIGHT.toInt(), Component.literal("Reorder"), onPress, DEFAULT_NARRATION),
    CobblemonRenderable {

    companion object {
        private const val WIDTH = 26F
        private const val HEIGHT = 13F
        private const val SCALE = 0.5F
        private val buttonResource = iwaResource("textures/gui/interact/reorder_button.png")
        private val iconResource = cobblemonResource("textures/gui/summary/summary_party_swap_icon.png")
    }

    override fun renderWidget(context: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        blitk(
            matrixStack = context.pose(),
            texture = buttonResource,
            x = x,
            y = y,
            width = WIDTH,
            height = HEIGHT,
            vOffset = if (isHovered(pMouseX.toDouble(), pMouseY.toDouble())) HEIGHT else 0,
            textureHeight = HEIGHT * 2
        )

        blitk(
            matrixStack = context.pose(),
            texture = iconResource,
            x = (x + 10) / SCALE,
            y = (y + 2) / SCALE,
            width = 12,
            height = 17,
            scale = SCALE
        )
        Summary

    }

    override fun playDownSound(soundManager: SoundManager) {}

    fun isHovered(mouseX: Double, mouseY: Double) =
        mouseX.toFloat() in (x.toFloat()..(x.toFloat() + WIDTH)) && mouseY.toFloat() in (y.toFloat()..(y.toFloat() + HEIGHT))
}