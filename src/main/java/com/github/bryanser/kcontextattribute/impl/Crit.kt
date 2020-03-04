package com.github.bryanser.kcontextattribute.impl

import com.github.bryanser.kcontextattribute.attribute.Attribute
import com.github.bryanser.kcontextattribute.attribute.AttributeContext
import com.github.bryanser.kcontextattribute.attribute.DamageAttribute
import com.github.bryanser.kcontextattribute.attribute.DamageEventContext
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity

class CritContext(owner: LivingEntity) : AttributeContext(owner) {
    var chance: Double = 0.0
    var rate: Double = 2.0

    override fun copy(): AttributeContext = CritContext(owner).also {
        it.chance = chance
        it.rate = rate
    }

    override fun toString(): String {
        return "§6暴击率: ${String.format("%.2f%", chance * 100)}, 暴击伤害倍率: ${String.format("%.2f%", rate * 100)}"
    }

}

object Crit : Attribute<CritContext>(
        "Crit",
        "暴击",
        10
), DamageAttribute {
    val chance = "暴击率[：:] *(?<value>[+-][0-9.]+)%".toRegex().toPattern()
    val rate = "暴击伤害[：:] *(?<value>[+-][0-9.]+)%".toRegex().toPattern()

    override fun createContext(p: LivingEntity): CritContext = CritContext(p)

    override fun loadAttribute(ctx: CritContext, lore: List<String>) {
        for (s in lore) {
            val str = ChatColor.stripColor(s)
            val dm = chance.matcher(str)
            if (dm.find()) {
                val value = dm.group("value")
                ctx.chance += value.toDouble() / 100.0
            } else {
                val m = rate.matcher(str)
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
        ctx.damager.with(this){
            if(Math.random() < chance){
                ctx.damage *= rate
            }
        }
    }

}