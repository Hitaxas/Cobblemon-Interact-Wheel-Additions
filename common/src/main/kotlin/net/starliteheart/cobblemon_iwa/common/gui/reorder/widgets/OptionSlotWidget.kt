package net.starliteheart.cobblemon_iwa.common.gui.reorder.widgets

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelOption
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.starliteheart.cobblemon_iwa.common.gui.reorder.ReorderOptions
import net.starliteheart.cobblemon_iwa.common.util.iwaResource
import org.joml.Vector3f

class OptionSlotWidget(
    pX: Number,
    pY: Number,
    private val optionsWidget: OptionsWidget,
    private val reorderMenu: ReorderOptions,
    val option: InteractWheelOption?,
    private val index: Int
) : SoundlessWidget(pX.toInt(), pY.toInt(), SIZE, SIZE, Component.literal("OptionSlot")) {
    companion object {
        const val SIZE = 27
        private const val ICON_SIZE = 32
        private const val ICON_SCALE = 0.5F

        private val slotResource = iwaResource("textures/gui/reorder/reorder_menu_option_tile.png")
        private val slotEmptyResource = iwaResource("textures/gui/reorder/reorder_menu_option_tile_empty.png")
        private val slotShiftUpResource = iwaResource("textures/gui/reorder/reorder_menu_option_tile_shift_up.png")
    }

    private fun getSlotTexture(option: InteractWheelOption?): ResourceLocation =
        if (option != null)
            slotResource
        else if (index.floorDiv(8) > 0 && optionsWidget.options[index - 8] == null)
            slotShiftUpResource
        else
            slotEmptyResource

    private fun getSlotVOffset(): Int = if (isHovered) height else 0

    override fun renderWidget(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height
        val matrices = context.pose()
        val isDraggedSlot = optionsWidget.swapSource == index
        val slotOption = if (isDraggedSlot) null else option

        // Base slot texture
        blitk(
            matrixStack = matrices,
            texture = getSlotTexture(slotOption),
            x = x,
            y = y,
            width = width,
            height = height,
            vOffset = getSlotVOffset(),
            textureHeight = height * 2,
        )

        if (!isDraggedSlot && option != null) {
            val colour = option.colour() ?: Vector3f(1F, 1F, 1F)
            blitk(
                matrixStack = matrices,
                texture = option.iconResource,
                x = (x + 5.5F) / ICON_SCALE,
                y = (y + 5.5F) / ICON_SCALE,
                width = ICON_SIZE,
                height = ICON_SIZE,
                alpha = if (isHovered) 1F else 0.2F,
                red = colour.x,
                green = colour.y,
                blue = colour.z,
                scale = ICON_SCALE
            )

            if (isHovered) {
                option.tooltipText?.let {
                    context.renderTooltip(Minecraft.getInstance().font, Component.translatable(it), mouseX, mouseY)
                }
            }
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (isValidClick(pMouseX, pMouseY, pButton)) {
            toggleDrag(true)
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton)
    }

    override fun mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        toggleDrag(false)
        return optionsWidget.mouseReleased(pMouseX, pMouseY, pButton)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, f: Double, g: Double): Boolean {
        if (optionsWidget.isWithinScreen(mouseX, mouseY) && index < 0) {
            repositionSlot(mouseX, mouseY)
        } else {
            if (optionsWidget.swapSource != null) optionsWidget.playSound(CobblemonSounds.PC_DROP)
            toggleDrag(false)
            optionsWidget.swapSource = null
            optionsWidget.draggedSlot = null
        }
        return super.mouseDragged(mouseX, mouseY, button, f, g)
    }

    private fun toggleDrag(boolean: Boolean) {
        val focusedElement = if (boolean) optionsWidget.draggedSlot else null
        reorderMenu.focused = focusedElement
        reorderMenu.isDragging = boolean
    }

    private fun repositionSlot(mouseX: Double, mouseY: Double) {
        x = (mouseX - (SIZE / 2)).toInt()
        y = (mouseY - (SIZE / 2)).toInt()
    }

    private fun isValidClick(mouseX: Double, mouseY: Double, button: Int): Boolean = button == 0
            && mouseX.toInt() in x..(x + width)
            && mouseY.toInt() in y..(y + height)
}