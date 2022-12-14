package jp.waseda.fuji.kokioe.coqonkotlin.core.definitions

import jp.waseda.fuji.kokioe.coqonkotlin.core.Hypothesis
import jp.waseda.fuji.kokioe.coqonkotlin.core.Section
import jp.waseda.fuji.kokioe.coqonkotlin.tactics.Apply
import jp.waseda.fuji.kokioe.coqonkotlin.tactics.By
import jp.waseda.fuji.kokioe.coqonkotlin.tactics.Move
import jp.waseda.fuji.kokioe.coqonkotlin.tactics.Tactics
import jp.waseda.fuji.kokioe.coqonkotlin.util.CoqObject

open class Prop(val notation: String) : CoqObject {

    infix fun then(another: Prop) = Implication(this, another)

    override fun toCoq() = notation
}

class Implication(val antecedent: Prop, val consequent: Prop) :
    Prop("(${antecedent.notation} -> ${consequent.notation})")

class Theorem(val name: String, private val prop: Prop, private val tactics: List<Tactics>) : CoqObject {

    override fun toCoq(): String {
        val output = StringBuilder()

        output.append("Theorem $name : ${prop.toCoq()}.\n")
        output.append("Proof.\n")

        tactics.forEach { output.append(it.toCoq()) }

        output.append("Qed.\n")

        return output.toString()
    }

    class Builder(private val name: String, private val prop: Prop, private val section: Section.Builder) {
        private val tactics = mutableListOf<Tactics>()

        private var goal = prop

        fun move(hypothesis: Hypothesis) {
            tactics += Move(hypothesis)
        }

        fun move(names: String) {
            names.split(" ").forEach { name ->
                if (goal is Implication) {
                    val prev = goal as Implication
                    section.hypothesis(name, prev.antecedent, true).also { tactics += Move(it) }
                    goal = prev.consequent
                } else throw IllegalArgumentException("No assumption in ${prop.notation}")
            }
        }

        fun apply(theorem: Theorem, vararg prop: Prop) {
            tactics += Apply(theorem, prop)
        }

        fun by() {
            tactics += By()
        }

        fun build() = Theorem(name, prop, tactics)
    }
}
