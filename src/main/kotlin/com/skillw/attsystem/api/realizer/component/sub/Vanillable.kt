package com.skillw.attsystem.api.realizer.component.sub

import com.skillw.attsystem.api.realizer.BaseRealizer

import taboolib.common5.cbool

/**
 * @className Vanillable
 *
 * @author Glom
 * @date 2023/1/5 16:51 Copyright 2022 user. All rights reserved.
 */
interface Vanillable {

    val defaultVanilla: Boolean
    fun isEnableVanilla(): Boolean = (this as? BaseRealizer)?.config?.get("vanilla")?.cbool ?: defaultVanilla

}