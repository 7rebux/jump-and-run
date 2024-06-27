package net.rebux.jumpandrun.item.impl

import de.tr7zw.changeme.nbtapi.NBT
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.config.MenuConfig
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourManager
import net.rebux.jumpandrun.utils.MessageBuilder
import net.rebux.jumpandrun.utils.TickFormatter
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object MenuItem : Item("menu") {

  private val parkours: List<Parkour>
    get() = ParkourManager.parkours.values.sortedBy(Parkour::difficulty)

  override fun onInteract(player: Player) {
    this.openInventory(player, 0)
  }

  fun openInventory(player: Player, page: Int) {
    val title = MessageBuilder()
      .template(MenuConfig.tile)
      .values(
        mapOf(
          "completed" to countParkoursPlayed(player),
          "records" to countParkourRecords(player),
          "quantity" to ParkourManager.parkours.size
        )
      )
      .prefix(false)
      .buildSingle()
    val size = MenuConfig.parkoursPerPage + 9
    val inventory = Bukkit.createInventory(null, size, title)

    this.openMenu(inventory, player, page)
  }

  private fun openMenu(inventory: Inventory, player: Player, page: Int) {
    for (slot in 0 until MenuConfig.parkoursPerPage) {
      val index = slot + page * MenuConfig.parkoursPerPage

      if (index == parkours.size) {
        break
      }

      inventory.setItem(slot, parkours[index].buildItem(player))
    }

    if (parkours.size > MenuConfig.parkoursPerPage * (page + 1)) {
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

      addAll(
        MessageBuilder()
          .template(MenuConfig.Entry.difficulty)
          .values(
            mapOf(
              "difficulty" to parkour.difficulty
            )
          )
          .prefix(false)
          .build()
      )

      addAll(
        MessageBuilder()
          .template(MenuConfig.Entry.builder)
          .values(
            mapOf(
              "builder" to parkour.builder
            )
          )
          .prefix(false)
          .build()
      )

      add("")

      addAll(
        MessageBuilder()
          .template(MenuConfig.Entry.PersonalBest.title)
          .prefix(false)
          .build()
      )

      if (playerTime != null) {
        val (time, unit) = TickFormatter.format(playerTime)

        addAll(
          MessageBuilder()
            .template(MenuConfig.Entry.PersonalBest.time)
            .values(
              mapOf(
                "time" to time,
                "unit" to unit
              )
            )
            .prefix(false)
            .build()
        )
      } else {
        addAll(
          MessageBuilder()
            .template(MenuConfig.Entry.noTime)
            .prefix(false)
            .build()
        )
      }

      add("")

      addAll(
        MessageBuilder()
          .template(MenuConfig.Entry.GlobalBest.title)
          .prefix(false)
          .build()
      )

      if (bestTime != null) {
        val (time, unit) = TickFormatter.format(bestTime)

        addAll(
          MessageBuilder()
            .template(MenuConfig.Entry.GlobalBest.time)
            .values(
              mapOf(
                "time" to time,
                "unit" to unit
              )
            )
            .prefix(false)
            .build()
        )

        addAll(
          MessageBuilder()
            .template(MenuConfig.Entry.GlobalBest.subtitle)
            .prefix(false)
            .build()
        )

        playersWithBestTime.forEach { player ->
          addAll(
            MessageBuilder()
              .template(MenuConfig.Entry.GlobalBest.player)
              .values(
                mapOf(
                  "player" to player.name!!
                )
              )
              .prefix(false)
              .build()
          )
        }
      } else {
        addAll(
          MessageBuilder()
            .template(MenuConfig.Entry.noTime)
            .prefix(false)
            .build()
        )
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
    Next("MHF_ArrowRight", MenuConfig.nextPage, 1),
    Previous("MHF_ArrowLeft", MenuConfig.previousPage, -1),
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
