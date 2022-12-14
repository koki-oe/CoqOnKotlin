package jp.waseda.fuji.kokioe.coqonkotlin.util.registry

import jp.waseda.fuji.kokioe.coqonkotlin.util.registry.ImmutableRegistry.Companion.registryOf

class MutableRegistry<T>(
    private val registry: MutableMap<String, T> = mutableMapOf()
) : Registry<T> by ImmutableRegistry(registry) {

    operator fun set(id: String, value: T) {
        registry[id] = value
    }

    fun toImmutable() = registryOf(registry.toMap())

    companion object {
        fun <T> mutableRegistryOf(init: Map<String, T>) = MutableRegistry(init.toMutableMap())
        fun <T> mutableRegistryOf() = MutableRegistry<T>()
    }
}