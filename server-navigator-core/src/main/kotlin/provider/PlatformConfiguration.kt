package de.nycode.servernavigator.core.provider

import java.io.File

interface PlatformConfiguration {

    fun getString(path: String): String?

    fun set(path: String, value: Any)

    fun getConfigRoot(): File

    fun info(text: String)

    fun warn(text: String)

    fun error(text: String)

}
