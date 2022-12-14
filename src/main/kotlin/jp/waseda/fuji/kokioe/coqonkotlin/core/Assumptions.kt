package jp.waseda.fuji.kokioe.coqonkotlin.core

import jp.waseda.fuji.kokioe.coqonkotlin.core.definitions.Prop
import jp.waseda.fuji.kokioe.coqonkotlin.util.CoqObject

class Hypothesis internal constructor(
    val name: String, private val prop: Prop, val generated: Boolean
) : CoqObject {
    override fun toCoq() = if (generated) "" else "Hypothesis $name : ${prop.toCoq()}.\n"
}