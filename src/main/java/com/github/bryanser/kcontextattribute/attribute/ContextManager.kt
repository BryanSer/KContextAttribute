package com.github.bryanser.kcontextattribute.attribute

import com.github.bryanser.brapi.Main
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import java.util.*

object ContextManager : Listener {
    const val MAX_CACHE_TIME = 1000L
    val attributes: MutableList<Attribute<*>> = mutableListOf()
    val cache = hashMapOf<UUID, Pair<EntityContext, Long>>()
    val projectileAttribute = hashMapOf<UUID, EntityContext>()

    fun register(attr: Attribute<*>) {
        attributes.add(attr)
        attributes.sortBy { it.priority }
    }

    fun init() {
        var i = 1L
        for (a in attributes) {
            a.init()
            if (a is PassiveAttribute) {
                Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), {
                    for (p in Bukkit.getOnlinePlayers()) {
                        val ctx = getContext(p)
                        try {
                            a.onPassive(ctx)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                }, i++, a.interval.toLong())
            }
        }
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin())
    }

    fun getContext(entity: LivingEntity, update: Boolean = false): EntityContext {
        if (!update) {
            val cache = cache[entity.uniqueId]
            if (cache != null) {
                val (c, time) = cache
                if (System.currentTimeMillis() - time < MAX_CACHE_TIME) {
                    return c.copy()
                }
            }
        }
        val ctx = EntityContext(entity)
        val evt = LoadItemEvent(entity, ctx)
        Bukkit.getPluginManager().callEvent(evt)
        ctx.init(evt.items)
        this.cache[entity.uniqueId] = ctx.copy() to System.currentTimeMillis()
        return ctx
    }

    @EventHandler
    fun onDamage(evt: EntityDamageByEntityEvent) {
        val e = evt.entity as? LivingEntity ?: return
        val d = evt.damager
        val damctx: EntityContext = if (d is Projectile) {
            projectileAttribute[d.uniqueId] ?: return
        } else {
            getContext(d as? LivingEntity ?: return)
        }
        val ctx = DamageEventContext(damctx, getContext(e), evt)
        for (attr in attributes) {
            if (attr is DamageAttribute) {
                try {
                    attr.onDamage(ctx)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        evt.damage = ctx.damage
    }

    @EventHandler
    fun onSwap(evt: PlayerItemHeldEvent) {
        cache.remove(evt.player.uniqueId)
    }

    @EventHandler
    fun onSwap(evt: PlayerSwapHandItemsEvent) {
        cache.remove(evt.player.uniqueId)
    }

    @EventHandler
    fun onSwap(evt: InventoryClickEvent) {
        cache.remove(evt.whoClicked.uniqueId)
    }

    @EventHandler
    fun onEntityDeath(evt: EntityDeathEvent) {
        cache.remove(evt.entity.uniqueId)
    }

    @EventHandler
    fun onLoadItem(evt: LoadItemEvent) {
        val e = evt.entity.equipment
        evt.addItem(*e.armorContents, e.itemInMainHand, e.itemInOffHand)
    }

    @EventHandler
    fun onLunch(evt: ProjectileLaunchEvent) {
        val e = getShooter(evt.entity) ?: return
        projectileAttribute[evt.entity.uniqueId] = getContext(e, true)
    }

    @EventHandler
    fun onHit(evt: ProjectileHitEvent) {
        val uid = evt.entity.uniqueId
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), {
            projectileAttribute.remove(uid)
        }, 2)
    }

    private fun getShooter(e: Projectile): LivingEntity? {
        val s = e.shooter
        if (s == null || s !is LivingEntity) {
            return null
        }
        return s
    }
}