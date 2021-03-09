package de.nycode.servernavigator.provider

object Providers {

    val ALL_PROVIDERS =
        listOf(ServerInformationFileProvider(), ServerInformationPterodactylProvider()).associateBy { it.configurationKey }

}
