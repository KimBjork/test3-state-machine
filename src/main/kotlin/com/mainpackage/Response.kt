package com.mainpackage

import org.junit.runner.Result
import org.junit.runner.notification.Failure
import java.io.File

data class HandlerOutput(val startTime: Long,
                         val coldStart: Boolean,
                         val wasSuccess: Boolean,
                         val failures: MutableList<Failure>)

class Response {
    fun createString(startTime : Long, result: Result):String {
        return "{\"startTime\": ${startTime}, \"coldStart\": ${isColdStart()}, \"wasSuccess\": ${result.wasSuccessful()}," +
                " \"failures\": ${result.failures}}"
    }

    fun createJson(startTime: Long, result: Result) : HandlerOutput{
        return HandlerOutput(startTime, isColdStart(), result.wasSuccessful(), result.failures)
    }

    private fun isColdStart(): Boolean{
        val fileName = "/tmp/out.txt"
        val file = File(fileName)

        if(file.exists()){
            return false;
        }
        File(fileName).createNewFile()
        return true;
    }
}