package com.cry.acresult

import android.content.Intent

class ResultEvent {
    var resultCode = 0
    var data: Intent? = null

    constructor()
    constructor(resultCode: Int, data: Intent?) {
        this.resultCode = resultCode
        this.data = data
    }

    override fun toString(): String {
        return "ResultEvent{" +
                ", resultCode=" + resultCode +
                ", data=" + data +
                '}'
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ResultEvent

        if (resultCode != that.resultCode) return false
        return if (data != null) data == that.data else that.data == null
    }

    override fun hashCode(): Int {
        var result = resultCode
        result = 31 * result + resultCode
        result = 31 * result + if (data != null) data.hashCode() else 0
        return result
    }
}