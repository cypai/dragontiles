package com.pipai.dragontiles.combat

import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import org.junit.Before
import kotlin.coroutines.resume

open class CombatBackendTest(val handler: Any) {

    val sEvent = EventSystem()

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
