package net.starliteheart.cobblemon_iwa.common.util

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.ExitButton
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelOption
import com.cobblemon.mod.common.client.gui.interact.wheel.Orientation
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.starliteheart.cobblemon_iwa.common.CobblemonIWA
import net.starliteheart.cobblemon_iwa.common.config.IWAConfig
import net.starliteheart.cobblemon_iwa.common.config.IWAConfig.OrderedOption
import net.starliteheart.cobblemon_iwa.common.gui.ReorderButton
import net.starliteheart.cobblemon_iwa.common.gui.reorder.ReorderOptions

fun iwaResource(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(CobblemonIWA.MOD_ID, path)

fun getConfig(): IWAConfig = AutoConfig.getConfigHolder(IWAConfig::class.java).get()
fun saveConfig() = AutoConfig.getConfigHolder(IWAConfig::class.java).save()

// Function used for reordering InteractWheelOptions based on client config
fun reorderOptions(
    options: Multimap<Orientation, InteractWheelOption>,
    ordering: List<OrderedOption>
): Multimap<Orientation, InteractWheelOption> {
    val ordered: Multimap<Orientation, InteractWheelOption> = ArrayListMultimap.create()
    val config = getConfig()

    // Search the given options and set the order of any that are listed in the ordering
    for ((resource, orientation) in ordering) {
        val location = ResourceLocation.parse(resource)
        val option = options.entries().find { (_, value) -> value.iconResource == location }
        option?.let { (key, value) ->
            ordered.put(orientation, value)
            options.remove(key, value)
        }
    }

    // Anything not used from options is added, either as is or using autofill
    if (!options.isEmpty) {
        when (config.autofillUnlisted) {
            false -> ordered.putAll(options)
            true -> for ((_, value) in options.entries()) {
                ordered.put(getNextFreeOrientation(ordered), value)
            }
        }
    }

    return ordered
}

// Copied from Cobblemon's PokemonInteractionGUICreationEvent
private fun getNextFreeOrientation(options: Multimap<Orientation, InteractWheelOption>): Orientation {
    var largest = Orientation.NORTH
    for (orientation in Orientation.entries) {
        if (!options.containsKey(orientation)) {
            return orientation
        } else {
            if (options.get(orientation).size < options.get(largest).size) {
                largest = orientation
            }
        }
    }
    return largest
}

private fun translateToOrderedOptions(options: Multimap<Orientation, InteractWheelOption>): List<OrderedOption> =
    options.entries().map { (orientation, option) ->
        OrderedOption(
            resource = option.iconResource.toString(),
            orientation = orientation
        )
    }

fun saveOptionsToConfig(interactKey: String, options: Multimap<Orientation, InteractWheelOption>) {
    val orderedOptions = translateToOrderedOptions(options)
    val config = getConfig()
    when (interactKey) {
        "cobblemon.ui.interact.pokemon" -> {
            config.pokemonOrderedOptions = orderedOptions
        }

        "cobblemon.ui.interact.player" -> {
            config.playerOrderedOptions = orderedOptions
        }

        else -> {
            CobblemonIWA.LOGGER.warn("Unknown interact key found when saving to config. Aborting config save.")
            return
        }
    }
    saveConfig()
}

fun createReorderButton(
    pos: Pair<Int, Int>,
    options: Multimap<Orientation, InteractWheelOption>,
    interactKey: String
) = ReorderButton(
    pX = pos.first + 28,
    pY = pos.second + 162,
    onPress = {
        Minecraft.getInstance().soundManager.play(
            SimpleSoundInstance.forUI(CobblemonSounds.GUI_CLICK, 1.0F)
        )
        Minecraft.getInstance().setScreen(
            ReorderOptions(options, interactKey, Component.translatable("cobblemon_iwa.ui.reorder"))
        )
    }
)

fun createExitButton(
    pos: Pair<Int, Int>
) = ExitButton(
    pX = pos.first + 116,
    pY = pos.second + 162,
    onPress = {
        Minecraft.getInstance().soundManager.play(
            SimpleSoundInstance.forUI(CobblemonSounds.GUI_CLICK, 1.0F)
        )
        Minecraft.getInstance().setScreen(null)
    }
)

fun renderBaseAddon(context: GuiGraphics, pos: Pair<Int, Int>) {
    blitk(
        matrixStack = context.pose(),
        texture = iwaResource("textures/gui/interact/interact_wheel_base_addon.png"),
        x = pos.first + 25,
        y = pos.second + 146,
        width = 120,
        height = 32
    )
}
