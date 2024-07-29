package korlibs.io.lang

import korlibs.platform.*
import kotlin.collections.set

private var customEnvironments: LinkedHashMap<String, String>? = null

interface Environment {
    operator fun get(key: String): String?
    operator fun set(key: String, value: String)
    fun getAll(): Map<String, String>
    companion object : Environment {
        val DIR_SEPARATOR: Char get() = if (Platform.isWindows) '\\' else '/'
        val PATH_SEPARATOR: Char get() = if (Platform.isWindows) ';' else ':'

        // Uses querystring on JS/Browser, and proper env vars in the rest
        override operator fun get(key: String): String? = customEnvironments?.get(key.uppercase()) ?: Platform.envsUC[key.uppercase()]
        override operator fun set(key: String, value: String) {
            if (customEnvironments != null) {
                customEnvironments = LinkedHashMap()
            }
            customEnvironments?.set(key.uppercase(), value)
        }

        override fun getAll(): Map<String, String> = (customEnvironments ?: mapOf()) + Platform.envs
    }
}

@Deprecated("", ReplaceWith("tempPath"))
val Environment.TEMP get() = tempPath
val Environment.tempPath get() = this["TMPDIR"] ?: this["TEMP"] ?: this["TMP"] ?: "/tmp"
// @TODO: System.getProperty("user.home")
val Environment.userHome get() = when {
    this["HOMEDRIVE"] != null && this["HOMEPATH"] != null -> "${this["HOMEDRIVE"]}${this["HOMEPATH"]}"
    else -> this["HOMEPATH"] ?: this["HOME"] ?: this.tempPath
}

open class EnvironmentCustom(customEnvironments: Map<String, String> = LinkedHashMap()) : Environment {
    var customEnvironments = when (customEnvironments) {
        is MutableMap<*, *> -> customEnvironments as MutableMap<String, String>
        else -> customEnvironments.toMutableMap()
    }
    private val customEnvironmentsNormalized = customEnvironments.map { it.key.uppercase() to it.value }.toLinkedMap()
    fun String.normalized() = this.uppercase().trim()
    override operator fun get(key: String): String? = customEnvironmentsNormalized[key.normalized()]
    operator override fun set(key: String, value: String) {
        customEnvironments[key] = value
        customEnvironmentsNormalized[key.normalized()] = value
    }
    override fun getAll(): Map<String, String> = customEnvironments
}

fun Environment(envs: Map<String, String> = mapOf()) = EnvironmentCustom(envs)
fun Environment(vararg envs: Pair<String, String>) = EnvironmentCustom(envs.toMap())

fun Environment.expand(str: String): String {
    return str.replace(Regex("(~|%(\\w+)%)")) {
        val key = it.value.trim('%')
        when (key) {
            "~" -> this.userHome
            else -> this[key]
        } ?: ""
    }
}

private fun <K, V> Iterable<Pair<K, V>>.toLinkedMap(): LinkedHashMap<K, V> = LinkedHashMap<K, V>().also { for ((key, value) in this) it[key] = value }
