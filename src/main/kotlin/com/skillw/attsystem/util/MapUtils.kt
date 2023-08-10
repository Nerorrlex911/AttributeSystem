package com.skillw.attsystem.util

import com.skillw.pouvoir.util.calculateInline
import com.skillw.pouvoir.util.replacement
import org.bukkit.entity.LivingEntity
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.ItemTagList
import taboolib.module.nms.ItemTagType

object MapUtils {

    @JvmStatic
    fun MutableMap<String, Any>.removeDeep(path: String) {
        val splits = path.split(".")
        if (splits.isEmpty()) {
            this.remove(path)
            return
        }
        var compound = this
        var temp: MutableMap<String, Any>
        for (node in splits) {
            if (node.equals(splits.last(), ignoreCase = true)) {
                compound.remove(node)
            }
            compound[node].also { temp = ((it as MutableMap<String, Any>?) ?: return) }
            compound = temp
        }
    }

    @JvmStatic
    internal fun ItemTag.toMutableMap(strList: List<String> = emptyList()): MutableMap<String, Any> {
        val map = HashMap<String, Any>()
        for (it in this) {
            val key = it.key
            if (strList.contains(key)) continue
            val value = it.value.obj()
            map[key] = value
        }
        return map
    }

    @JvmStatic
    internal fun ItemTagList.toList(): List<Any> {
        return map { it.obj() }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    @JvmStatic
    internal fun ItemTagData.obj(): Any {
        val value = when (this.type) {
            ItemTagType.BYTE -> this.asByte()
            ItemTagType.SHORT -> this.asShort()
            ItemTagType.INT -> this.asInt()
            ItemTagType.LONG -> this.asLong()
            ItemTagType.FLOAT -> this.asFloat()
            ItemTagType.DOUBLE -> this.asDouble()
            ItemTagType.STRING -> this.asString()
            ItemTagType.BYTE_ARRAY -> this.asByteArray()
            ItemTagType.INT_ARRAY -> this.asIntArray()
            ItemTagType.COMPOUND -> this.asCompound()
            ItemTagType.LIST -> this.asList()
            else -> this.asString()
        }
        return when (value) {
            is ItemTag -> {
                value.toMutableMap()
            }

            is ItemTagList -> {
                val list = ArrayList<Any>()
                value.forEach {
                    list.add(it.obj())
                }
                list
            }

            else -> value
        }
    }

    internal fun <T : Any> T.replaceThenCalc(replacement: Map<String, String>, entity: LivingEntity?): Any {
        return when (this) {
            is Map<*, *> -> {
                val map = HashMap<String, Any>()
                forEach { (key, value) ->
                    key ?: return@forEach
                    value ?: return@forEach
                    map[key.toString()] = value.replaceThenCalc(replacement, entity)
                }
                map
            }

            is List<*> -> {
                val list = ArrayList<Any>()
                mapNotNull { it }.forEach {
                    list.add(it.replaceThenCalc(replacement, entity))
                }
                list
            }

            is String -> replacement(replacement).calculateInline(entity)

            else -> this
        }
    }


    internal fun <T : Any> T.clone(): Any {
        return when (this) {
            is Map<*, *> -> {
                val map = HashMap<String, Any>()
                forEach { (key, value) ->
                    key ?: return@forEach
                    value ?: return@forEach
                    map[key.toString()] = value.clone()
                }
                map
            }

            is List<*> -> {
                val list = ArrayList<Any>()
                mapNotNull { it }.forEach {
                    list.add(it.clone())
                }
                list
            }

            else -> this
        }
    }
}