package net.rebux.jumpandrun.item.impl

import de.tr7zw.changeme.nbtapi.NBT
import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourManager
import net.rebux.jumpandrun.template
import net.rebux.jumpandrun.utils.TickFormatter
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object MenuItem : Item("menu") {

  // TODO: get rid of plugin
  private val plugin = Instance.plugin
  private val parkours: List<Parkour>
    get() = ParkourManager.parkours.values.sortedBy(Parkour::difficulty)

  override fun onInteract(player: Player) {
    openInventory(player, 0)
  }

  fun openInventory(player: Player, page: Int) {
    val inventory = Bukkit.createInventory(
      null,
      plugin.config.getInt("parkoursPerPage") + 9,
      template("menu.title", mapOf(
        "completed" to countParkoursPlayed(player),
        "records" to countParkourRecords(player),
        "quantity" to ParkourManager.parkours.size
      ))
    )

    openMenu(inventory, player, page)
  }

  private fun openMenu(inventory: Inventory, player: Player, page: Int) {
    val parkoursPerPage = inventory.size - 9

    for (slot in 0 until parkoursPerPage) {
      val index = slot + page * parkoursPerPage

      if (index == parkours.size) {
        break
      }

      inventory.setItem(slot, parkours[index].buildItem(player))
    }

    if (parkours.size > parkoursPerPage * (page + 1)) {
      inventory.setItem(
        inventory.size - 1,
        buildPaginationItem(PaginationType.Next, page)
      )
    }

    if (page > 0) {
      inventory.setItem(
        inventory.size - 9,
        buildPaginationItem(PaginationType.Previous, page)
      )
    }

    player.openInventory(inventory)
  }

  private fun Parkour.buildItem(player: Player): ItemStack {
    val playerTime = this.times[player.uniqueId]
    val bestTime = this.times.values.minOrNull()
    val playersWithBestTime = this.times.entries
      .filter { it.value == bestTime }
      .map { Bukkit.getOfflinePlayer(it.key) }

    val displayName = "${ChatColor.DARK_AQUA}${this.name} %s".format(
      if (playerTime != null) {
        if (playerTime == bestTime) {
          "${ChatColor.GOLD}✫"
        } else {
          "${ChatColor.GREEN}✔"
        }
      } else {
        "${ChatColor.RED}✘"
      }
    )

    val lore = buildList {
      val parkour = this@buildItem

      add(template("menu.difficulty", mapOf("difficulty" to parkour.difficulty)))
      add(template("menu.builder", mapOf("builder" to parkour.builder)))
      add("")

      add(template("menu.personalBest.title"))

      if (playerTime != null) {
        add(template("menu.personalBest.time", mapOf("time" to TickFormatter.format(playerTime))))
      } else {
        add(template("menu.noTime"))
      }

      add("")

      add(template("menu.globalBest.title"))

      if (bestTime != null) {
        add(template("menu.globalBest.time", mapOf("time" to TickFormatter.format(bestTime))))
        add(template("menu.globalBest.subtitle"))
        playersWithBestTime.forEach { player ->
          add(template("menu.globalBest.player", mapOf("player" to player.name!!)))
        }
      } else {
        add(template("menu.noTime"))
      }
    }

    val itemStack = Builder()
      .material(this.material)
      .displayName(displayName)
      .lore(lore)
      .build()

    if (playerTime != null) {
      val itemMeta = itemStack.itemMeta

      itemMeta!!.addItemFlags(ItemFlag.HIDE_ENCHANTS)
      itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10)

      itemStack.itemMeta = itemMeta
    }

    NBT.modify(itemStack) { nbt ->
      nbt.setInteger(Plugin.PARKOUR_TAG, this.id)
    }

    return itemStack
  }

  private enum class PaginationType(
    val skullName: String,
    val displayName: String,
    val step: Int
  ) {
    Next("MHF_ArrowRight", template("items.previousPage"), 1),
    Previous("MHF_ArrowLeft", template("items.nextPage"), -1),
  }

  private fun buildPaginationItem(type: PaginationType, page: Int): ItemStack {
    val itemStack = SkullBuilder()
      .displayName(type.displayName)
      .username(type.skullName)
      .build()

    NBT.modify(itemStack) { nbt ->
      nbt.setInteger(Plugin.PAGE_TAG, page)
      nbt.setInteger(Plugin.PAGE_STEP_TAG, type.step)
    }

    return itemStack
  }

  private fun countParkoursPlayed(player: Player): Int {
    return parkours.count { parkour ->
      parkour.times.contains(player.uniqueId)
    }
  }

  private fun countParkourRecords(player: Player): Int {
    return parkours.count { parkour ->
      val recordTime = parkour.times.values.minOrNull()
      val playerTime = parkour.times[player.uniqueId]
        ?: Int.MAX_VALUE

      return@count recordTime == playerTime
    }
  }
}
