package jp.waseda.fuji.kokioe.coqonkotlin.util.registry

interface Registry<T> : Collection<T> {
    operator fun get(id: String): T?
    val keys: Set<String>
}