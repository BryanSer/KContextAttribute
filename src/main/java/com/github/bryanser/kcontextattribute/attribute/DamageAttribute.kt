package com.github.bryanser.kcontextattribute.attribute

interface DamageAttribute {
    fun onDamage(ctx: DamageContext)
}

interface PassiveAttribute {
    val interval: Int
    fun onPassive(ctx: Context)
}