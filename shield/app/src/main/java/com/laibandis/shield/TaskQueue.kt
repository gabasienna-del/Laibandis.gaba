package com.laibandis.shield

import android.content.Context
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

data class QueuedTask(val base: String, val path: String, val body: String)

object TaskQueue {

    private val queue = ConcurrentLinkedQueue<QueuedTask>()
    private var started = false

    private fun file(ctx: Context) = File(ctx.filesDir, "queue.txt")

    fun load(ctx: Context) {
        val f = file(ctx)
        if (f.exists()) {
            f.readLines().forEach {
                val p = it.split("|", limit = 3)
                if (p.size == 3) queue.add(QueuedTask(p[0], p[1], p[2]))
            }
        }
    }

    fun save(ctx: Context) {
        file(ctx).writeText(queue.joinToString("\n") {
            "${it.base}|${it.path}|${it.body}"
        })
    }

    fun enqueue(ctx: Context, t: QueuedTask) {
        queue.add(t)
        save(ctx)
    }

    fun start(ctx: Context) {
        if (started) return
        started = true
        load(ctx)
        thread {
            while (true) {
                val t = queue.poll()
                if (t != null) {
                    try {
                        val token = TokenStore.load(ctx)
                        val url = Router.resolve(t.base) + t.path
                        Http.forward(url, t.body, token)
                    } catch (e: Exception) {
                        // не получилось — вернём в очередь
                        queue.add(t)
                        save(ctx)
                    }
                }
                Thread.sleep(3000)
            }
        }
    }
}
