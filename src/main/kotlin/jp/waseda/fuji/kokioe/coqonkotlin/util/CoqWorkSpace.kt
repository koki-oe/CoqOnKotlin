package jp.waseda.fuji.kokioe.coqonkotlin.util

import jp.waseda.fuji.kokioe.coqonkotlin.core.Section
import jp.waseda.fuji.kokioe.coqonkotlin.core.definitions.Theorem
import jp.waseda.fuji.kokioe.coqonkotlin.util.registry.Registry

abstract class CoqWorkSpace {
    protected abstract val sections: Registry<Section>
    protected abstract val theorems: Registry<Theorem>

    fun getSection(id: String): Section = sections[id] ?: throwNoSuchReference(id)

    fun getTheorem(name: String): Theorem = theorems[name] ?: throwNoSuchReference(name)

    private fun throwNoSuchReference(id: String): Nothing =
        throw NoSuchFieldException("The reference $id was not found in the current environment.")
}