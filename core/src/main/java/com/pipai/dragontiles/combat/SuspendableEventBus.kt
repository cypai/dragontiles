package com.pipai.dragontiles.combat

import net.mostlyoriginal.api.event.common.EventSystem
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions

class SuspendableEventBus(private val eventSystem: EventSystem) {

    private val subscriptions: MutableMap<KClass<*>, MutableList<Pair<Any, KFunction<*>>>> = mutableMapOf()

    fun register(o: Any) {
        o::class.declaredFunctions
                .filter { it.annotations.any { a -> a is CombatSubscribe } }
                .forEach { addSubscription(o, it) }
    }

    private fun addSubscription(o: Any, f: KFunction<*>) {
        val firstParam = f.parameters[1].type.classifier as KClass<*>
        if (firstParam in subscriptions) {
            subscriptions[firstParam]?.add(Pair(o, f))
        } else {
            subscriptions[firstParam] = mutableListOf(Pair(o, f))
        }
    }

    suspend fun suspendDispatch(ev: CombatEvent, api: CombatApi) {
        subscriptions[ev::class]?.forEach { it.second.callSuspend(it.first, ev, api) }
        eventSystem.dispatch(ev)
    }

    fun dispatch(ev: CombatEvent) {
        eventSystem.dispatch(ev)
    }
}

@Target(AnnotationTarget.FUNCTION)
annotation class CombatSubscribe
