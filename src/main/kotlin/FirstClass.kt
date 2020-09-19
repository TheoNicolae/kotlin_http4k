import com.fasterxml.jackson.databind.JsonNode
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.boolean

data class Email(val value: String)
data class Message(val subject: String, val from: Email, val to: Email)

object Main{
    private val json = Jackson
    @JvmStatic
    fun main(args: Array<String>) {
        val emailLens: BiDiBodyLens<Email> = Body.auto<Email>().toLens()
        val messageLens: BiDiBodyLens<Message> = Body.auto<Message>().toLens()
        val app = routes(
            "test" bind Method.GET to { Response(OK).body("you got Test")},
            "numberCheck" bind Method.POST to {request -> checkOddNumber(request)},
            "getParam" bind Method.POST to { request ->
                val value = emailLens.extract(request).value
                Response(OK).body("From body extracted param: $value ....")
            },
            "receiveAndResend" bind Method.POST to { req ->
                val subject = messageLens.extract(req);
                messageLens.inject(subject, Response(OK))
            }
        )

        app.asServer(Netty(8080)).start()
    }

    private fun checkOddNumber(request: Request): Response {
        return if (request.query("param1")!!.toInt() % 2 == 0) {
            Response(OK).body("the number is even")
        } else {
            Response(OK).body("the number is odd")
        }
    }

    private fun convertToJson(request: Request, messageLens: BiDiBodyLens<Message>, emailLens: BiDiBodyLens<Email>): JsonNode {
        return json.obj(
            "subject" to json.string(messageLens.extract(request).subject),
            "from" to json.string(emailLens.extract(request).value),
            "to" to json.string(emailLens.extract(request).value),
            "timestamp" to json.number(1234212),
            "thisIsAList" to json.array(listOf(boolean(true)))
        )
    }
}


