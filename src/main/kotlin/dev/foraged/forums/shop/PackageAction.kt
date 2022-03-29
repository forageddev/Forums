/*
 * Copyright (c) 2022. Darragh Hay
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Darragh Hay
 */

package dev.foraged.forums.shop

class PackageAction(val target: String = "local", val type: PackageActionType, val requiresLogin: Boolean = true)