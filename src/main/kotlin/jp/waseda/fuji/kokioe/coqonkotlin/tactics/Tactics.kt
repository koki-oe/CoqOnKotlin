package jp.waseda.fuji.kokioe.coqonkotlin.tactics

import jp.waseda.fuji.kokioe.coqonkotlin.core.Hypothesis
import jp.waseda.fuji.kokioe.coqonkotlin.core.definitions.Prop
import jp.waseda.fuji.kokioe.coqonkotlin.core.definitions.Theorem
import jp.waseda.fuji.kokioe.coqonkotlin.util.CoqObject

interface Tactics : CoqObject

data class Move(val hypothesis: Hypothesis) : Tactics {
    override fun toCoq() =
        if (hypothesis.generated) "move=> ${hypothesis.name}.\n"
        else "move: ${hypothesis.name}.\n"
}

class Apply(private val theorem: Theorem, private val props: Array<out Prop>) : Tactics {
    override fun toCoq() =
        "apply: (${theorem.name}${
            props.takeIf { it.isNotEmpty() }?.let { " ${it.joinToString(" ") { prop -> prop.notation }}" }
        }).\n"
}

class By : Tactics {
    override fun toCoq() = "by [].\n"
}