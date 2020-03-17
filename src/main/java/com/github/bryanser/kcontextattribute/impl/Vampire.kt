package com.github.bryanser.kcontextattribute.impl

import com.github.bryanser.kcontextattribute.attribute.Attribute
import com.github.bryanser.kcontextattribute.attribute.AttributeContext
import com.github.bryanser.kcontextattribute.attribute.DamageAttribute
import com.github.bryanser.kcontextattribute.attribute.DamageEventContext
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class VampireContext(owner: LivingEntity) : AttributeContext(owner) {
    var chance: Double = 0.0
    var rate: Double = 0.00

    override fun copy(): AttributeContext = VampireContext(owner).also {
        it.chance = chance
        it.rate = rate
    }

    override fun toString(): String {
        return "§c汲取几率: ${String.format("%.2f%", chance * 100)}, 生命汲取: ${String.format("%.2f%", rate * 100)}"
    }
}

object Vampire : Attribute<VampireContext>(
        "Vampire",
        "吸血",
        11
), DamageAttribute {
    val chance = "汲取几率[：:] *(?<value>[+-][0-9.]+)%".toRegex().toPattern()
    val rate = "生命汲取[：:] *(?<value>[+-][0-9.]+)%".toRegex().toPattern()
    override fun createContext(p: LivingEntity): VampireContext = VampireContext(p)

    override fun loadAttribute(ctx: VampireContext, lore: List<String>, item: ItemStack) {
        if (!isWeapon(item)) {
            return
        }
        for (s in lore) {
            val str = ChatColor.stripColor(s)
            val dm = Crit.chance.matcher(str)
            if (dm.find()) {
                val value = dm.group("value")
                ctx.chance += value.toDouble() / 100.0
            } else {
                val m = Crit.rate.matcher(str)
                if (m.find()) {
                    val value = dm.group("value")
                    ctx.rate += value.toDouble() / 100.0
                }
            }
            if (ctx.rate < 0.01) {
                ctx.rate = 0.01
            }
        }
    }

    override fun init() {
    }

    override fun onDamage(ctx: DamageEventContext) {
        ctx.damager.with(this) {
            if (Math.random() < chance) {
                val v = rate * ctx.damage
                var h = owner.health + v
                val max = owner.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).value
                if (h > max) {
                    h = max
                } else if (h < 0) {
                    h = 0.0
                }
                owner.health = h
            }
        }
    }
}