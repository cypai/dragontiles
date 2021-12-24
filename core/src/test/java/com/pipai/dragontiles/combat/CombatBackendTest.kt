package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.GameData
import com.pipai.dragontiles.data.GameDataInitializer
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import org.junit.Before
import kotlin.coroutines.resume

open class CombatBackendTest(val handler: Any) {

    val gameData = GameData()

    val sEvent = EventSystem()

    init {
        GameDataInitializer().init(gameData)
    }

    @Before
    fun init() {
        sEvent.registerEvents(handler)
    }

}

class QueryHandler {
    @Subscribe
    fun handleQuery(ev: QueryTilesEvent) {
        ev.continuation.resume(listOf())
    }

    @Subscribe
    fun handleSwap(ev: QuerySwapEvent) {
        // Do nothing
    }
}
