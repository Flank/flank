
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers

fun sendMessage(args: Array<String>): Int{
    val token = args[Common.ARGS_TOKEN]
    val channel = args[Common.ARGS_CHANNEL]
    val message = args[Common.ARGS_MESSAGE]
    val cookie = args.copyOfRange(Common.ARGS_COOKIE, args.lastIndex).joinToString(" ")

    return try {
        val (req, rep, res) = Fuel.get(Common.URL_SLACK, 
            listOf("token" to token,"channel" to channel,"text" to message))
            .header(Headers.COOKIE to cookie)
            .responseString()

        debug("Result: $res")
        Common.EXIT_CODE_SUCCESS
    }
    catch(e: Exception) {
        error("Error has occured: $e")
        Common.EXIT_CODE_FAILURE
    }
}

fun debug(message: String){
    println("::debug::$message")
}

fun error(message: String) {
    println("::error::$message")
}
