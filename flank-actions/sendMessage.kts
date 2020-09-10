import kotlin.system.exitProcess


//DEPS com.github.kittinunf.fuel:fuel:2.2.3

//INCLUDE slackService.kt
//INCLUDE common.kt

val result = sendMessage(args)

println("Message has been sent with result $result")

if (result != 0){
    exitProcess(result)
}
