package de.nycode.servernavigator.platform.spigot.item

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

@Suppress("DEPRECATION")
fun buildItem(builder: ItemBuilder.() -> Unit): ItemStack {
    val itemBuilder = ItemBuilder()
    itemBuilder.apply(builder)

    require(itemBuilder.material != null) { "Material cannot be null" }

    val itemStack = ItemStack(itemBuilder.material!!)
    itemStack.apply {
        amount = itemBuilder.amount
        val meta = itemMeta
        itemMeta = meta?.apply {
            setDisplayName(itemBuilder.displayName)
            lore = itemBuilder.lore
            if (itemBuilder.material == Material.PLAYER_HEAD) {
                this as SkullMeta
                require(itemBuilder.headData.uuid != null || itemBuilder.headData.name != null) { "UUID or Name must be provided" }
                owningPlayer =
                    itemBuilder.headData.name?.let { Bukkit.getOfflinePlayer(it) } ?: Bukkit.getOfflinePlayer(
                        itemBuilder.headData.uuid!!
                    )
            }
        }
    }
    return itemStack
}

data class ItemBuilder(
    var displayName: String? = null,
    var amount: Int = 1,
    var lore: MutableList<String> = mutableListOf(),
    var material: Material? = null,
    internal var meta: ItemMeta? = null,
    var headData: HeadBuilder = HeadBuilder()
)

data class HeadBuilder(
    var uuid: UUID? = null,
    var name: String? = null,
)

fun ItemBuilder.head(builder: HeadBuilder.() -> Unit) {
    material = Material.PLAYER_HEAD
    headData.apply(builder)
}
