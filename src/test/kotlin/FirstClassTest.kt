import FirstClass.Main.appServer
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.lens.BiDiBodyLens
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class FirstClassTest {
    private val backend = appServer()

    @Test
    fun `get endpoint test`() {
        backend(Request(Method.GET, "/test")).expectOK()
    }

    @Test
    fun `post endpoint, odd number test`() {
        val resultEven = backend(Request(Method.POST, "/numberCheck").query("param1", "4")).expectOK()
        Assertions.assertEquals("the number is even", resultEven.bodyString())
        val resultOdd = backend(Request(Method.POST, "/numberCheck").query("param1", "5")).expectOK()
        Assertions.assertEquals("the number is odd", resultOdd.bodyString())
    }

    @Test
    fun `receiveAndResend endpoint test`() {
        val emailLens: BiDiBodyLens<Email> = Body.auto<Email>().toLens()
        val messageLens: BiDiBodyLens<Message> = Body.auto<Message>().toLens()
        val result = backend(
            Request(Method.POST, "/receiveAndResend").with(
                messageLens of Message(
                    "test",
                    Email("from test"),
                    Email("to test")
                )
            )
        )
        Assertions.assertEquals(
            "{\"subject\":\"test\",\"from\":{\"value\":\"from test\"},\"to\":{\"value\":\"to test\"}}",
            result.bodyString()
        )
    }


}

private fun Response.expectOK(): Response {
    Assertions.assertTrue(OK == status)
    return this
}
