import FirstClass.Main.appServer
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class FirstClassTest {
    private val backend = appServer()

    @Test fun `get endpoint test`() {
        backend(org.http4k.core.Request(Method.GET,"/test")).expectOK()
    }

}

private fun Response.expectOK(): Response {
    Assertions.assertTrue(OK == status)
    return this
}
