package jp.waseda.fuji.kokioe.coqonkotlin.util.registry

class ImmutableRegistry<T>(private val registry: Map<String, T> = mapOf()) : Registry<T> {

    override operator fun get(id: String) = registry[id]

    override val size: Int
        get() = registry.size

    override val keys: Set<String>
        get() = registry.keys

    override fun contains(element: T) = registry.containsValue(element)
    override fun containsAll(elements: Collection<T>) = registry.values.containsAll(elements)
    override fun isEmpty() = registry.isEmpty()
    override fun iterator() = registry.values.iterator()

    companion object {
        fun <T> registryOf(init: Map<String, T>) = ImmutableRegistry(init.toMap())
        fun <T> registryOf() = ImmutableRegistry<T>()
    }
}