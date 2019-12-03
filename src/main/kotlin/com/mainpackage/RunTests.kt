package com.mainpackage

import org.junit.runner.JUnitCore
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import java.io.File

class TestListener : RunListener() {
    lateinit var result : Result

    override fun testRunFinished(result: Result){
        this.result = result
    }
}

data class HandlerOutput(val startTime: Long,
                         val coldStart: Boolean,
                         val wasSuccess: Boolean,
                         val failures: MutableList<Failure>)

class RunTests {
    fun getStringResult(startTime: Long) : String {
        val listener = run()
        return createString(startTime, listener.result)
    }
    fun getJsonResult(startTime: Long) : HandlerOutput {
        val listener = run()
        return createJson(startTime, listener.result)
    }

    private fun run() : TestListener{
        val junit = JUnitCore()
        val listener = TestListener()
        junit.addListener(listener)
        junit.run(TurnstileStateMachineTest::class.java,
                MatterStateMachineTest::class.java)
        return listener
    }

    private fun createString(startTime : Long, result: Result):String {
        return "{\"startTime\": ${startTime}, \"coldStart\": ${isColdStart()}, \"wasSuccess\": ${result.wasSuccessful()}," +
                " \"failures\": ${result.failures}}"
    }

    private fun createJson(startTime: Long, result: Result) : HandlerOutput{
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