package jp.waseda.fuji.kokioe.coqonkotlin.commands

import jp.waseda.fuji.kokioe.coqonkotlin.util.CoqObject

class CompiledFiles(private val groups: Map<String, Group>) : CoqObject {
    companion object {
        const val EMPTY_PATH = ""
    }

    override fun toCoq(): String {
        val output = StringBuilder()

        groups.forEach { (dirPath, group) ->
            if (dirPath != EMPTY_PATH)
                output.append("From $dirPath\n\t")

            if (group.imports.isNotEmpty())
                output.append("Require Import ${group.imports.joinToString(" ")}.\n")
        }

        return output.toString()
    }

    class Builder {
        private val defaultGroup = Group.Builder()
        private val groups = mutableMapOf(EMPTY_PATH to defaultGroup)

        fun group(dirPath: String) = groups[dirPath] ?: Group.Builder().also { groups[dirPath] = it }

        internal fun build() = CompiledFiles(groups.map { it.key to it.value.build() }.toMap())
    }

    class Group(val imports: Array<String>) {
        class Builder {
            private val imports = mutableListOf<String>()

            fun import(vararg categories: String) = imports.addAll(categories)

            internal fun build() = Group(imports.toTypedArray())
        }
    }
}