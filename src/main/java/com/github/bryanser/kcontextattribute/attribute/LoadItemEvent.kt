package com.github.bryanser.kcontextattribute.attribute

import org.bukkit.entity.LivingEntity
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityEvent
import org.bukkit.inventory.ItemStack

class LoadItemEvent(
        private val ent: LivingEntity,
        val context: EntityContext
) : EntityEvent(ent) {
    var items = mutableListOf<ItemStack>()

    override fun getHandlers(): HandlerList = handler

    override fun getEntity(): LivingEntity = ent

    fun addItem(vararg item: ItemStack?) {
        item.forEach { if (it != null) items.add(it) }
    }

    companion object {
        val handler = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList = handler
    }

}