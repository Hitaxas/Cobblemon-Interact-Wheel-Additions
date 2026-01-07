package net.starliteheart.cobblemon_iwa.common.gui.reorder

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.ExitButton
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelGUI
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelOption
import com.cobblemon.mod.common.client.gui.interact.wheel.Orientation
import com.cobblemon.mod.common.client.render.drawScaledText
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.starliteheart.cobblemon_iwa.common.gui.reorder.widgets.OptionsWidget
import net.starliteheart.cobblemon_iwa.common.util.iwaResource
import net.starliteheart.cobblemon_iwa.common.util.saveOptionsToConfig
import java.security.InvalidParameterException

class ReorderOptions(
    private val options: Multimap<Orientation, InteractWheelOption>,
    private val interactKey: String,
    title: Component
) : Screen(title), Renderable {
    companion object {
        private const val BASE_WIDTH = 273
        private const val BASE_HEIGHT = 164
        private val backgroundResource = iwaResource("textures/gui/reorder/reorder_menu_base.png")
        private val headings = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        private const val MAX_PAGES = 4
    }

    override fun renderBlurredBackground(delta: Float) {}
    override fun renderMenuBackground(context: GuiGraphics) {}

    /**
     * Much of the code in this file and its supplementary widgets is largely based on code from Cobblemon's Summary UI
     * and its Party and PartySlot widgets, so that this UI would be more consistent with the base mod's UI.
     */

    private val optionsArray = arrayOfNulls<InteractWheelOption?>(8 * MAX_PAGES)
    lateinit var optionsGrid: GuiEventListener

    private fun translateMultimapToArray(options: Multimap<Orientation, InteractWheelOption>) {
        Orientation.entries.forEachIndexed { offset, orientation ->
            options[orientation].forEachIndexed { index, option ->
                if (optionsArray.size <= (offset + index * 8))
                    throw InvalidParameterException("Too many InteractWheelOptions in one orientation column")
                optionsArray[offset + index * 8] = option
            }
        }
    }

    private fun translateArrayToMultimap(array: Array<InteractWheelOption?>): Multimap<Orientation, InteractWheelOption> {
        val options: Multimap<Orientation, InteractWheelOption> = ArrayListMultimap.create()
        array.forEachIndexed { index, option ->
            if (option != null) {
                options.put(Orientation.entries[index % 8], option)
            }
        }
        return options
    }

    override fun init() {
        // Translate Multimap into a fixed array of InteractWheelOptions
        translateMultimapToArray(options)

        val (x, y) = getBasePosition()

        // Render Options Widget
        displayOptionsGrid()

        // Add Exit Button
        addRenderableWidget(
            ExitButton(pX = x + 244, pY = y + 148) {
                playSound(CobblemonSounds.GUI_CLICK)
                Minecraft.getInstance().setScreen(
                    InteractWheelGUI(saveOptions(), Component.translatable(interactKey))
                )
            }
        )
    }

    private fun displayOptionsGrid() {
        if (::optionsGrid.isInitialized) removeWidget(optionsGrid)

        val (x, y) = getBasePosition()

        optionsGrid = OptionsWidget(
            pX = x + 13,
            pY = y + 19,
            reorderMenu = this,
            options = optionsArray
        )

        val element = optionsGrid
        if (element is Renderable && element is NarratableEntry) {
            addRenderableWidget(element)
        }
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val (x, y) = getBasePosition()
        val matrices = context.pose()

        // Render Base Resource
        blitk(
            matrixStack = matrices,
            texture = backgroundResource,
            x = x,
            y = y,
            width = BASE_WIDTH,
            height = BASE_HEIGHT
        )

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = Component.translatable("cobblemon_iwa.ui.reorder.title").bold(),
            x = x + 7F,
            y = y + 2.5F,
            shadow = true
        )

        headings.forEachIndexed { index, heading ->
            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = Component.literal(heading).bold(),
                colour = if ((1..MAX_PAGES).any { page ->
                        ScreenRectangle(x + 17 + index * 32, y - 9 + page * 31, 27, 27)
                            .containsPoint(mouseX, mouseY)
                    }) 0x00FFFF00 else 0x00FFFFFF,
                x = x + 31.5F + index * 32F,
                y = y + 13.5F,
                centered = true,
                shadow = true
            )
        }

        for (page in 1..MAX_PAGES) {
            drawScaledText(
                context = context,
                font = CobblemonResources.DEFAULT_LARGE,
                text = Component.literal("$page").bold(),
                colour = if (headings.indices.any { index ->
                        ScreenRectangle(x + 17 + index * 32, y - 9 + page * 31, 27, 27)
                            .containsPoint(mouseX, mouseY)
                    }) 0x00FFFF00 else 0x00FFFFFF,
                x = x + 10.5F,
                y = y + 0.5F + page * 31F,
                centered = true,
                shadow = true
            )
        }

        // Render all added Widgets
        super.render(context, mouseX, mouseY, delta)
    }

    fun swapOptionSlot(sourceIndex: Int, targetIndex: Int) {
        if (sourceIndex >= this.optionsArray.size || targetIndex >= this.optionsArray.size) {
            return
        }

        val sourceOption = this.optionsArray.getOrNull(sourceIndex)

        if (sourceOption != null) {
            val targetOption = this.optionsArray.getOrNull(targetIndex)

            // Update change in UI
            this.optionsArray[targetIndex] = sourceOption
            this.optionsArray[sourceIndex] = targetOption

            // Shakedown columns
            val sourceColumnIndex = sourceIndex % 8
            val targetColumnIndex = targetIndex % 8
            shakedownColumn(sourceColumnIndex)
            if (sourceColumnIndex != targetColumnIndex) {
                shakedownColumn(targetColumnIndex)
            }

            // Refresh options grid
            displayOptionsGrid()
        }
    }

    private fun shakedownColumn(columnIndex: Int) {
        // Recurse down column
        source@ for (i in 0..<(MAX_PAGES - 1)) {
            val sourceIndex = i * 8 + columnIndex
            val sourceOption = this.optionsArray.getOrNull(sourceIndex)
            // If empty, we stop to find the first non-empty slot to swap with
            if (sourceOption == null) {
                target@ for (j in (i + 1)..<MAX_PAGES) {
                    val targetIndex = j * 8 + columnIndex
                    val targetOption = this.optionsArray.getOrNull(targetIndex)
                    if (targetOption != null) {
                        this.optionsArray[targetIndex] = sourceOption
                        this.optionsArray[sourceIndex] = targetOption
                        // Swap complete, search for the next empty slot
                        continue@source
                    }
                }
                // If all are empty, no more swaps are needed, so we can end early
                break@source
            }
        }
    }

    private fun saveOptions(): Multimap<Orientation, InteractWheelOption> {
        val newOptions = translateArrayToMultimap(optionsArray)
        saveOptionsToConfig(interactKey, newOptions)
        return newOptions
    }

    private fun getBasePosition(): Pair<Int, Int> {
        return Pair((width - BASE_WIDTH) / 2, (height - BASE_HEIGHT) / 2)
    }

    fun playSound(soundEvent: SoundEvent) {
        Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forUI(soundEvent, 1.0F))
    }

    override fun isPauseScreen() = false

    override fun onClose() {
        saveOptions()
        super.onClose()
    }
}