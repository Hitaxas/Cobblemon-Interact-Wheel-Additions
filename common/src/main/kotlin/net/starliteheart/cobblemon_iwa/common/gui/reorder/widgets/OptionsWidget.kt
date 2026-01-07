package net.starliteheart.cobblemon_iwa.common.gui.reorder.widgets

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelOption
import com.cobblemon.mod.common.client.gui.summary.widgets.PartySlotWidget
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.starliteheart.cobblemon_iwa.common.gui.reorder.ReorderOptions
import net.starliteheart.cobblemon_iwa.common.util.iwaResource

class OptionsWidget(
    pX: Int,
    pY: Int,
    val reorderMenu: ReorderOptions,
    val options: Array<InteractWheelOption?>
) : SoundlessWidget(pX, pY, WIDTH, HEIGHT, Component.literal("OptionsOverlay")) {
    companion object {
        private const val WIDTH = 259
        private const val HEIGHT = 126
        private val backgroundResource = iwaResource("textures/gui/reorder/reorder_menu_options.png")
    }

    var swapSource: Int? = null
    var draggedSlot: OptionSlotWidget? = null

    private val optionsWidgets = arrayListOf<OptionSlotWidget>()

    init {
        options.forEachIndexed { index, option ->
            val x = x + 4 + (index % 8) * 32
            val y = y + 3 + index.floorDiv(8) * 31

            OptionSlotWidget(
                pX = x,
                pY = y,
                optionsWidget = this,
                reorderMenu = reorderMenu,
                option = option,
                index = index
            ).also { widget ->
                this.addWidget(widget)
                optionsWidgets.add(widget)
            }
        }
    }

    override fun renderWidget(context: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        val matrices = context.pose()
        blitk(
            matrixStack = matrices,
            texture = backgroundResource,
            x = x,
            y = y,
            width = width,
            height = height
        )

        optionsWidgets.forEach { it.render(context, pMouseX, pMouseY, pPartialTicks) }

        if (draggedSlot != null) {
            matrices.pushPose()
            matrices.translate(0.0, 0.0, 500.0)
            draggedSlot!!.render(context, pMouseX, pMouseY, pPartialTicks)
            matrices.popPose()
        }
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        val index = getIndexFromPos(pMouseX, pMouseY)
        if (index > -1) {
            val option = optionsWidgets[index].option
            if (option != null) {
                swapSource = index
                draggedSlot = OptionSlotWidget(
                    pX = pMouseX - (PartySlotWidget.WIDTH / 2),
                    pY = pMouseY - (PartySlotWidget.HEIGHT / 2),
                    optionsWidget = this,
                    reorderMenu = reorderMenu,
                    option = option,
                    index = -1
                )
                playSound(CobblemonSounds.PC_GRAB)
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton)
    }

    override fun mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        if (swapSource != null) {
            val index = getIndexFromPos(pMouseX, pMouseY)
            if (index > -1 && index != swapSource) {
                reorderMenu.swapOptionSlot(swapSource!!, index)
            }
            swapSource = null
            draggedSlot = null
            playSound(CobblemonSounds.PC_DROP)
        }
        return true
    }

    private fun getIndexFromPos(mouseX: Double, mouseY: Double): Int {
        for (index in 0..32) {
            val posX = x + 4 + (index % 8) * 32
            val posY = y + 3 + index.floorDiv(8) * 31
            if (mouseX.toInt() in posX..(posX + OptionSlotWidget.SIZE)
                && mouseY.toInt() in posY..(posY + OptionSlotWidget.SIZE)
            ) {
                return index
            }
        }
        return -1
    }

    fun playSound(soundEvent: SoundEvent) {
        Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forUI(soundEvent, 1.0F))
    }

    fun isWithinScreen(mouseX: Double, mouseY: Double): Boolean = mouseX.toInt() in x..(x + WIDTH)
            && mouseY.toInt() in y..(y + HEIGHT)
}