package com.github.bryanser.kcontextattribute.attribute

interface PassiveAttribute {
    val interval: Int
    fun onPassive(ctx: EntityContext)
}