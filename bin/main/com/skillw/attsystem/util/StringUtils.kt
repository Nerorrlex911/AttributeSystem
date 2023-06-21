package com.skillw.attsystem.util

import org.bukkit.Material
import taboolib.library.xseries.XMaterial
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext

object StringUtils {
    @JvmStatic
    fun String.material(): Material? {
        val xMaterial = XMaterial.matchXMaterial(this)
        return if (xMaterial.isPresent) {
            xMaterial.get().parseMaterial()
        } else {
            Material.matchMaterial(this)
        }
    }
}
