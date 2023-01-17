package jp.waseda.fuji.kokioe.coqonkotlin.json

import kotlinx.serialization.json.*

data class JsonModule(
    val preamble: Preamble,
    val struct: Struct
) {
    companion object {
        fun from(jsonObject: JsonObject) = JsonModule(
            preamble = Preamble.from(jsonObject),
            struct = Struct.from(jsonObject)
        )
    }

    data class Preamble(
        override val what: String,
        override val name: String,
        val needMagic: Boolean,
        val needDummy: Boolean,
        val usedModules: List<String>
    ) : HasType, HasName {
        companion object {
            fun from(jsonObject: JsonObject) = Preamble(
                what = jsonObject.what,
                name = jsonObject.name,
                needMagic = jsonObject.requireNotNull("need_magic").autoCast(),
                needDummy = jsonObject.requireNotNull("need_dummy").autoCast(),
                usedModules = jsonObject.requireArray("used_modules").autoCast()
            )
        }
    }

    data class Struct(
        val declarations: List<JsonDeclaration>
    ) {
        companion object {
            fun from(jsonObject: JsonObject) = Struct(
                declarations = jsonObject.requireArray("declarations").map { it.jsonObject.asDeclaration }
            )
        }
    }
}

interface HasType {
    val what: String
}

interface HasName {
    val name: String
}

interface JsonType : HasType

val JsonObject.asType: JsonType
    get() = when (what) {
        TVar.TAG -> TVar.from(this)
        TVarIdx.TAG -> TVarIdx.from(this)
        TGlob.TAG -> TGlob.from(this)
        TArrow.TAG -> TArrow.from(this)
        TDummy.TAG -> TDummy
        TUnknown.TAG -> TUnknown
        TAxiom.TAG -> TAxiom
        else -> throw IllegalArgumentException("$this is an illegal type")
    }

fun JsonObject.asType(key: String) = requireNotNull(key).jsonObject.asType

data class TVar(
    override val name: String
) : JsonType, HasName {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "type:var"

        fun from(jsonObject: JsonObject) = TVar(
            name = jsonObject.name
        )
    }
}

data class TVarIdx(
    val name: Int
) : JsonType {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "type:varidx"

        fun from(jsonObject: JsonObject) = TVarIdx(
            name = jsonObject.requireNotNull("name").autoCast()
        )
    }
}

data class TGlob(
    override val name: String,
    val args: List<JsonType>
) : JsonType, HasName {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "type:glob"

        fun from(jsonObject: JsonObject) = TGlob(
            name = jsonObject.name,
            args = jsonObject.requireArray("args").map { it.jsonObject.asType }
        )
    }
}

data class TArrow(
    val left: JsonType,
    val right: JsonType
) : JsonType {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "type:arrow"

        fun from(jsonObject: JsonObject) = TArrow(
            left = jsonObject.asType("left"),
            right = jsonObject.asType("right")
        )
    }
}

object TDummy : JsonType {
    const val TAG = "type:dummy"
    override val what = TAG
}

object TUnknown : JsonType {
    const val TAG = "type:unknown"
    override val what = TAG
}

object TAxiom : JsonType {
    const val TAG = "type:axiom"
    override val what = TAG
}

interface JsonExpression : HasType

val JsonObject.asExpression: JsonExpression
    get() = when (what) {
        MLRel.TAG -> MLRel.from(this)
        MLApply.TAG -> MLApply.from(this)
        MLLambda.TAG -> MLLambda.from(this)
        MLLetIn.TAG -> MLLetIn.from(this)
        MLGlobal.TAG -> MLGlobal.from(this)
        MLConstructor.TAG -> MLConstructor.from(this)
        MLTuple.TAG -> MLTuple.from(this)
        MLCase.TAG -> MLCase.from(this)
        MLFix.TAG -> MLFix.from(this)
        MLException.TAG -> MLException.from(this)
        MLDummy.TAG -> MLDummy
        MLMagic.TAG -> MLMagic.from(this)
        MLAxiom.TAG -> MLAxiom
        MLUInt.TAG -> MLUInt.from(this)
        MLFloat.TAG -> MLFloat.from(this)
        MLPArray.TAG -> MLPArray.from(this)
        else -> throw IllegalArgumentException("$this is an illegal type")
    }

fun JsonObject.asExpression(key: String) = requireNotNull(key).jsonObject.asExpression

data class MLRel(
    override val name: String
) : JsonExpression, HasName {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:rel"

        fun from(jsonObject: JsonObject) = MLRel(
            name = jsonObject.name
        )
    }
}

data class MLApply(
    val func: JsonFunction,
    val args: List<JsonExpression>
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:apply"

        fun from(jsonObject: JsonObject) = MLApply(
            func = jsonObject.asFunction("func"),
            args = jsonObject.requireArray("args").map { it.jsonObject.asExpression }
        )
    }
}

data class MLLambda(
    val argNames: List<String>,
    val body: JsonExpression
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:lambda"

        fun from(jsonObject: JsonObject) = MLLambda(
            argNames = jsonObject.requireArray("argnames").autoCast(),
            body = jsonObject.asExpression("body")
        )
    }
}

data class MLLetIn(
    override val name: String,
    val nameVal: JsonExpression,
    val body: JsonExpression
) : JsonExpression, HasName {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:let"

        fun from(jsonObject: JsonObject) = MLLetIn(
            name = jsonObject.name,
            nameVal = jsonObject.asExpression("nameval"),
            body = jsonObject.asExpression("body")
        )
    }
}

data class MLGlobal(
    override val name: String
) : JsonExpression, HasName {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:global"

        fun from(jsonObject: JsonObject) = MLGlobal(
            name = jsonObject.name
        )
    }
}

data class MLConstructor(
    override val name: String,
    val args: List<JsonExpression>
) : JsonExpression, HasName {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:constructor"

        fun from(jsonObject: JsonObject) = MLConstructor(
            name = jsonObject.name,
            args = jsonObject.requireArray("args").map { it.jsonObject.asExpression }
        )
    }
}

data class MLTuple(
    val items: List<JsonExpression>
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:tuple"

        fun from(jsonObject: JsonObject) = MLTuple(
            items = jsonObject.requireArray("items").map { it.jsonObject.asExpression }
        )
    }
}

data class MLCase(
    val expr: JsonExpression,
    val cases: List<JsonOnePat>
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:case"

        fun from(jsonObject: JsonObject) = MLCase(
            expr = jsonObject.asExpression("expr"),
            cases = jsonObject.requireArray("cases").map { JsonOnePat.from(it.jsonObject) }
        )
    }
}

data class MLFix(
    val functions: List<Item>,
    val index: Int
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:fix"

        fun from(jsonObject: JsonObject) = MLFix(
            functions = jsonObject.requireArray("funcs").map { Item.from(it.jsonObject) },
            index = jsonObject.requireNotNull("for").autoCast()
        )
    }

   data class Item(
       override val name: String,
       val body: JsonFunction
   ) : HasType, HasName {
       override val what: String
           get() = TAG

       companion object {
           const val TAG = "fix:item"

           fun from(jsonObject: JsonObject) = Item(
               name = jsonObject.name,
               body = jsonObject.asFunction("body")
           )
       }
   }
}

data class MLException(
    val msg: String
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:exception"

        fun from(jsonObject: JsonObject) = MLException(
            msg = jsonObject.requireNotNull("msg").autoCast()
        )
    }
}

object MLDummy : JsonExpression {
    const val TAG = "expr:dummy"
    override val what = TAG
}

data class MLMagic(
    val value: JsonExpression
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:coerce"

        fun from(jsonObject: JsonObject) = MLMagic(
            value = jsonObject.asExpression("value")
        )
    }
}

object MLAxiom : JsonExpression {
    const val TAG = "expr:axiom"
    override val what = TAG
}

data class MLUInt(
    val value: Int
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:int"

        fun from(jsonObject: JsonObject) = MLUInt(
            value = jsonObject.requireNotNull("int").autoCast()
        )
    }
}

data class MLFloat(
    val value: Int
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:float"

        fun from(jsonObject: JsonObject) = MLFloat(
            value = jsonObject.requireNotNull("float").autoCast()
        )
    }
}

data class MLPArray(
    val elements: List<JsonExpression>,
    val default: JsonExpression
) : JsonExpression {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:array"

        fun from(jsonObject: JsonObject) = MLPArray(
            elements = jsonObject.requireArray("elems").map { it.jsonObject.asExpression },
            default = jsonObject.asExpression("default")
        )
    }
}

data class JsonOnePat(
    val pat: JsonPat,
    val body: JsonExpression
) : HasType {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "case"

        fun from(jsonObject: JsonObject) = JsonOnePat(
            pat = jsonObject.asPat("pat"),
            body = jsonObject.asExpression("body")
        )
    }
}

interface JsonPat : HasType

val JsonObject.asPat: JsonPat
    get() = when (what) {
        PConstructor.TAG -> PConstructor.from(this)
        PTuple.TAG -> PTuple.from(this)
        PWild.TAG -> PWild
        PRel.TAG -> PRel.from(this)
        else -> throw IllegalArgumentException("$this is an illegal pat")
    }

fun JsonObject.asPat(key: String) = requireNotNull(key).jsonObject.asPat

data class PConstructor(
    override val name: String,
    val argNames: List<String>
) : JsonPat, HasName {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "pat:constructor"

        fun from(jsonObject: JsonObject) = PConstructor(
            name = jsonObject.name,
            argNames = jsonObject.requireArray("argnames").autoCast()
        )
    }
}

data class PTuple(
    val items: List<JsonPat>
) : JsonPat {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "pat:tuple"

        fun from(jsonObject: JsonObject) = PTuple(
            items = jsonObject.requireArray("items").map { it.jsonObject.asPat }
        )
    }
}

object PWild : JsonPat {
    const val TAG = "pat:wild"
    override val what = TAG
}

data class PRel(
    override val name: String
) : JsonPat, HasName {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "pat:rel"

        fun from(jsonObject: JsonObject) = PRel(
            name = jsonObject.name
        )
    }
}

data class JsonFunction(
    val argNames: List<String>,
    val body: JsonExpression
) : HasType {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "expr:lambda"

        fun from(jsonObject: JsonObject) = JsonFunction(
            argNames = jsonObject.requireArray("argnames").autoCast(),
            body = jsonObject.asExpression("body")
        )
    }
}

val JsonObject.asFunction: JsonFunction
    get() = JsonFunction.from(this)

fun JsonObject.asFunction(key: String) = requireNotNull(key).jsonObject.asFunction

interface JsonDeclaration : HasType, HasName

val JsonObject.asDeclaration: JsonDeclaration
    get() = when (what) {
        DInd.TAG -> DInd.from(this)
        DType.TAG -> DType.from(this)
        DFix.TAG -> DFix.from(this)
        DTerm.TAG -> DTerm.from(this)
        else -> throw IllegalArgumentException("$this is an illegal declaration")
    }

data class DInd(
    override val name: String,
    val argNames: List<String>,
    val constructors: List<Constructor>
) : JsonDeclaration {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "decl:ind"

        fun from(jsonObject: JsonObject) = DInd(
            name = jsonObject.name,
            argNames = jsonObject.requireArray("argnames").autoCast(),
            constructors = jsonObject.requireArray("constructors").map { Constructor.from(it.jsonObject) }
        )
    }

    data class Constructor(
        override val name: String,
        val argTypes: List<JsonType>
    ) : HasName {
        companion object {
            fun from(jsonObject: JsonObject) = Constructor(
                name = jsonObject.name,
                argTypes = jsonObject.requireArray("argtypes").map { it.jsonObject.asType }
            )
        }
    }
}

data class DType(
    override val name: String,
    val argNames: List<String>,
    val value: JsonType
) : JsonDeclaration {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "decl:type"

        fun from(jsonObject: JsonObject) = DType(
            name = jsonObject.name,
            argNames = jsonObject.requireArray("argnames").autoCast(),
            value = jsonObject.asType("value")
        )
    }
}

data class DFix(
    override val name: String,
    val fixList: List<Item>
) : JsonDeclaration {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "decl:fixgroup"

        fun from(jsonObject: JsonObject) = DFix(
            name = jsonObject.name,
            fixList = jsonObject.requireArray("fixlist").map { Item.from(it.jsonObject) }
        )
    }

    data class Item(
        override val name: String,
        val type: JsonType,
        val value: JsonFunction
    ) : JsonDeclaration {
        override val what: String
            get() = TAG

        companion object {
            const val TAG = "fixgroup:item"

            fun from(jsonObject: JsonObject) = Item(
                name = jsonObject.name,
                type = jsonObject.asType("type"),
                value = jsonObject.asFunction("value")
            )
        }
    }
}

data class DTerm(
    override val name: String,
    val type: JsonType,
    val value: JsonFunction
) : JsonDeclaration {
    override val what: String
        get() = TAG

    companion object {
        const val TAG = "decl:term"

        fun from(jsonObject: JsonObject) = DTerm(
            name = jsonObject.name,
            type = jsonObject.asType("type"),
            value = jsonObject.asFunction("value")
        )
    }
}

inline fun <reified T> JsonArray.autoCast(): List<T> = map { it.autoCast() }

inline fun <reified T> JsonElement.autoCast(): T =
    when (T::class) {
        String::class -> jsonPrimitive.content
        Int::class -> jsonPrimitive.int
        Float::class -> jsonPrimitive.float
        Boolean::class -> jsonPrimitive.boolean
        else -> throw IllegalArgumentException("$this can not be cast into ${T::class} automatically.")
    } as T

fun JsonObject.requireNotNull(key: String) = requireNotNull(get(key))

fun JsonObject.requireArray(key: String) = requireNotNull(key).jsonArray

val JsonObject.what: String
    get() = requireNotNull("what").autoCast()

val JsonObject.name: String
    get() = requireNotNull("name").autoCast()
