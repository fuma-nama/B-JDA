package bjda.wrapper

import net.dv8tion.jda.api.requests.RestAction
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun<T> RestAction<T>.fetch() = suspendCoroutine { cont ->
    this.queue({
        cont.resume(it)
    }, {
        cont.resumeWithException(it)
    })
}