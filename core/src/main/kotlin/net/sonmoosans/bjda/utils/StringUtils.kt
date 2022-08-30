package net.sonmoosans.bjda.utils

import java.util.*
import kotlin.collections.ArrayList

const val normal = 0
const val inQuote = 1
const val inDoubleQuote = 2

fun translateCommandline(toProcess: String?): Array<String> {
    if (toProcess == null || toProcess.isEmpty()) {
        // no command? no string
        return emptyArray()
    }

    // parse with a simple finite state machine

    var state = normal
    val tok = StringTokenizer(toProcess, "\"\' ", true)
    val list = ArrayList<String>()
    var current = StringBuilder()
    var lastTokenHasBeenQuoted = false

    while (tok.hasMoreTokens()) {
        val nextTok: String = tok.nextToken()
        when (state) {
            inQuote -> if ("\'" == nextTok) {
                lastTokenHasBeenQuoted = true
                state = normal
            } else {
                current.append(nextTok)
            }
            inDoubleQuote -> if ("\"" == nextTok) {
                lastTokenHasBeenQuoted = true
                state = normal
            } else {
                current.append(nextTok)
            }
            else -> {
                if ("\'" == nextTok) {
                    state = inQuote
                } else if ("\"" == nextTok) {
                    state = inDoubleQuote
                } else if (" " == nextTok) {
                    if (lastTokenHasBeenQuoted || current.isNotEmpty()) {
                        list.add(current.toString())
                        current = StringBuilder()
                    }
                } else {
                    current.append(nextTok)
                }
                lastTokenHasBeenQuoted = false
            }
        }
    }
    if (lastTokenHasBeenQuoted || current.isNotEmpty()) {
        list.add(current.toString())
    }
    require(!(state == inQuote || state == inDoubleQuote)) {
        ("Unbalanced quotes in "
                + toProcess)
    }
    val args = arrayOfNulls<String>(list.size)
    return list.toArray(args)
}
