package com.pipai.dragontiles.combat

import com.pipai.dragontiles.utils.getLogger
import net.mostlyoriginal.api.event.common.EventSystem
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions

class SuspendableEventBus(private val eventSystem: EventSystem) {
    private val logger = getLogger()

    private val subscriptions: MutableMap<KClass<*>, PriorityQueue<Subscription>> = mutableMapOf()
    private lateinit var api: CombatApi
    private val dispatchQueue: MutableList<CombatEvent> = mutableListOf()

    fun init(api: CombatApi) {
        this.api = api
    }

    fun register(o: Any) {
        o::class.declaredFunctions
                .filter { it.annotations.any { a -> a is CombatSubscribe } }
                .forEach {
                    val annotation = it.annotations.find { a -> a is CombatSubscribe } as CombatSubscribe
                    addSubscription(annotation.priority, o, it)
                }
    }

    private fun addSubscription(priority: Int, o: Any, f: KFunction<*>) {
        val firstParam = f.parameters[1].type.classifier as KClass<*>
        if (firstParam !in subscriptions) {
            subscriptions[firstParam] = PriorityQueue(10) { a, b -> a.priority.compareTo(b.priority) }
        }
        subscriptions[firstParam]?.add(Subscription(priority, o, f))
    }

    suspend fun dispatch(ev: CombatEvent) {
        logger.info("Dispatch $ev")
        eventSystem.dispatch(ev)
        subscriptions[ev::class]?.forEach { it.func.callSuspend(it.obj, ev, api) }
    }

    fun syncDispatch(ev: CombatEvent) {
        eventSystem.dispatch(ev)
    }

    private data class Subscription(val priority: Int, val obj: Any, val func: KFunction<*>)
}

@Target(AnnotationTarget.FUNCTION)
annotation class CombatSubscribe(val priority: Int = 0)
