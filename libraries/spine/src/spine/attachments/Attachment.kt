package com.esotericsoftware.spine.attachments

abstract class Attachment(var name: String) {
    override fun toString(): String = name
    abstract fun copy(): Attachment
}
