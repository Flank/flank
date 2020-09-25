
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers

fun sendMessage(args: Array<String>): Int{
    val token = args[Common.ARGS_TOKEN]
    val channel = args[Common.ARGS_CHANNEL]
    val message = args[Common.ARGS_MESSAGE]
    val cookie = args[Common.ARGS_COOKIE]

    return try {
        val (req, rep, res) = Fuel.get(Common.URL_SLACK, 
            listOf("token" to token,"channel" to channel,"text" to message))
            .header(Headers.COOKIE to cookie)
            .responseString()

        Common.EXIT_CODE_SUCCESS
    }
    catch(e: Exception) {
        Common.EXIT_CODE_FAILURE
    }
}
