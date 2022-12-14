package jp.waseda.fuji.kokioe.coqonkotlin.core

import jp.waseda.fuji.kokioe.coqonkotlin.core.definitions.Prop
import jp.waseda.fuji.kokioe.coqonkotlin.core.definitions.Theorem
import jp.waseda.fuji.kokioe.coqonkotlin.util.CoqObject
import jp.waseda.fuji.kokioe.coqonkotlin.util.registry.MutableRegistry
import jp.waseda.fuji.kokioe.coqonkotlin.util.registry.MutableRegistry.Companion.mutableRegistryOf
import jp.waseda.fuji.kokioe.coqonkotlin.util.registry.Registry

class Section(
    private val id: String,
    private val props: Registry<Prop>,
    private val hypotheses: Registry<Hypothesis>,
    private val theorems: Registry<Theorem>
) : CoqObject {
    companion object {
        const val PROP_PREFIX = "P"
        const val HYPOTHESIS_PREFIX = "Hyp"
        const val THEOREM_PREFIX = "Thm"
    }

    override fun toCoq(): String {
        val output = StringBuilder()

        output.append("Section $id.\n")

        if (props.isNotEmpty()) {
            output.append("Variables ${props.keys.joinToString(" ")} : Prop.\n")
        }

        if (hypotheses.isNotEmpty() && hypotheses.any { !it.generated }) {
            output.append("\n")
            hypotheses.forEach { output.append(it.toCoq()) }
        }

        if (theorems.isNotEmpty()) {
            output.append("\n")
            theorems.forEach { output.append(it.toCoq()) }
        }

        output.append("\nEnd $id.\n")
        return output.toString()
    }

    class Builder(private val id: String, private val commonTheorems: MutableRegistry<Theorem>) {
        private val props = mutableRegistryOf<Prop>()
        private val hypotheses = mutableRegistryOf<Hypothesis>()
        private val theorems = mutableRegistryOf<Theorem>()

        fun prop(name: String) = Prop(name).also { props[name] = it }
        fun prop() = prop("$PROP_PREFIX${props.size + 1}")

        fun hypothesis(name: String, prop: Prop, generated: Boolean = false) =
            Hypothesis(name, prop, generated).also { hypotheses[name] = it }

        fun hypothesis(prop: Prop, generated: Boolean = false) =
            hypothesis("$HYPOTHESIS_PREFIX${hypotheses.size + 1}", prop, generated)

        fun theorem(name: String, prop: Prop, lambda: Theorem.Builder.() -> Unit) =
            Theorem.Builder(name, prop, this).apply(lambda).build().also {
                if (theorems.keys.contains(name)) throw Exception("$name already exists.") else {
                    theorems[name] = it
                    commonTheorems[name] = it
                }
            }

        fun theorem(prop: Prop, lambda: Theorem.Builder.() -> Unit) =
            theorem("$THEOREM_PREFIX${theorems.size + 1}", prop, lambda)

        fun build() = Section(id, props.toImmutable(), hypotheses.toImmutable(), theorems.toImmutable())
    }
}
