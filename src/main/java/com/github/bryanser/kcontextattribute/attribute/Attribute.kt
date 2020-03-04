package com.github.bryanser.kcontextattribute.attribute

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

abstract class Attribute<AC : AttributeContext>(
        val name: String,
        val displayName: String,
        val priority: Int
) {

    abstract fun createContext(p: LivingEntity): AC

    abstract fun loadAttribute(ctx: AC, lore: List<String>, item:ItemStack)

    abstract fun init()
}

