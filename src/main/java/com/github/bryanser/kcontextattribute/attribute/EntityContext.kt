package com.github.bryanser.kcontextattribute.attribute

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class EntityContext(
        val owner: LivingEntity
) {
    val contexts = hashMapOf<Attribute<*>, AttributeContext>()

    fun init(items: List<ItemStack>) {
        for (attr in ContextManager.attributes) {
            contexts[attr] = attr.createContext(owner)
        }
        for (item in items) {
            if (!item.hasItemMeta()) continue
            val im = item.itemMeta
            if (!im.hasLore()) continue
            val lore = im.lore
            for ((attr, ctx) in contexts) {
                try {
                    (attr as Attribute<AttributeContext>).loadAttribute(ctx, lore)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    inline fun <reified AC : AttributeContext, A : Attribute<AC>, R> with(attr: A, func: AC.() -> R): R {
        return contexts[attr]?.let { it as? AC }?.func()
                ?: throw IllegalArgumentException("找不到属性${attr.name}")
    }


    fun copy(): EntityContext {
        val ctx = EntityContext(owner)
        for ((a, ac) in contexts) {
            ctx.contexts[a] = ac.copy()
        }
        return ctx
    }
}

