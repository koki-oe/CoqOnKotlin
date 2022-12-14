package jp.waseda.fuji.kokioe.coqonkotlin

import jp.waseda.fuji.kokioe.coqonkotlin.commands.CompiledFiles
import jp.waseda.fuji.kokioe.coqonkotlin.core.Section
import jp.waseda.fuji.kokioe.coqonkotlin.core.definitions.Theorem
import jp.waseda.fuji.kokioe.coqonkotlin.util.CoqWorkSpace
import jp.waseda.fuji.kokioe.coqonkotlin.util.registry.MutableRegistry.Companion.mutableRegistryOf
import jp.waseda.fuji.kokioe.coqonkotlin.util.registry.Registry

class CoqOnKotlin private constructor(
    private val compiledFiles: CompiledFiles,
    override val sections: Registry<Section>,
    override val theorems: Registry<Theorem>
) : CoqWorkSpace() {

    fun toCoq(): String {
        val output = StringBuilder()

        output.append(compiledFiles.toCoq())
        output.append("\n")
        output.append(sections.joinToString("\n") { it.toCoq() })

        return output.toString()
    }

    class Builder : CoqWorkSpace() {
        private val compiledFilesBuilder = CompiledFiles.Builder()
        override val sections = mutableRegistryOf<Section>()
        override val theorems = mutableRegistryOf<Theorem>()

        fun from(dirPath: String, lambda: CompiledFiles.Group.Builder.() -> Unit) =
            compiledFilesBuilder.group(dirPath).lambda()

        fun section(id: String, lambda: Section.Builder.() -> Unit) =
            Section.Builder(id, theorems).apply(lambda).build().also { sections[id] = it }

        fun build() = CoqOnKotlin(compiledFilesBuilder.build(), sections.toImmutable(), theorems.toImmutable())
    }

    companion object {
        fun coq(lambda: Builder.() -> Unit) = Builder().apply(lambda).build()
    }
}