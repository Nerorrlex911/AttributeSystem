package com.skillw.attsystem.internal.feature.compat.pouvoir.annotation

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.condition.BaseCondition
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.console
import taboolib.module.configuration.util.asMap
import taboolib.module.lang.sendLang
import java.util.regex.Pattern
import javax.script.ScriptContext.ENGINE_SCOPE

/**
 * Condition
 *
 * 条件注解 条件键 条件类型 条件名 （含正则）
 *
 * @constructor Condition Key String
 */
@AutoRegister
object Condition : ScriptAnnotation("Condition", fileAnnotation = true) {
    override fun handle(data: ScriptAnnotationData) {
        val script = data.script
        val function = data.function
        if (function != "null") return
        val vars = script.script.engine.getBindings(ENGINE_SCOPE)
        val key = vars["key"]?.toString() ?: error("Condition key in ${script.key} is null")
        val names = vars["names"].asMap().values.map { Pattern.compile(it.toString()) }
        object : BaseCondition(key) {
            override fun parameters(text: String): Map<String, Any>? {
                val matcher = names.map { it.matcher(text) }.firstOrNull { it.find() } ?: return null
                return Pouvoir.scriptManager.invoke<Any>(
                    script, "parameters", parameters = arrayOf(matcher, text)
                ) as? Map<String, Any>?
            }

            override fun condition(entity: LivingEntity?, parameters: Map<String, Any>): Boolean {
                return Pouvoir.scriptManager.invoke<Boolean>(
                    script, "condition", parameters = arrayOf(entity, parameters)
                ) ?: true
            }

        }.register()
        ASConfig.debug { console().sendLang("annotation-condition-register", key) }
        script.onDeleted("Condition-$key") {
            ASConfig.debug { console().sendLang("annotation-condition-unregister", key) }
            AttributeSystem.mechanicManager.remove(key)
        }
    }
}