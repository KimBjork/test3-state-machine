package com.mainpackage

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.runner.JUnitCore
import org.junit.runner.Result
import org.junit.runner.notification.RunListener
import java.io.OutputStream

class TestListener : RunListener() {
    lateinit var result : Result

    override fun testRunFinished(result: Result){
        this.result = result
    }
}

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val startTime = System.currentTimeMillis()

            val junit = JUnitCore()
            val listener = TestListener()
            junit.addListener(listener)
            junit.run(StateMachineTest::class.java)

            val res = Response().createString(startTime, listener.result)
            println(res)
        }
    }

    fun handler(output: OutputStream): Unit {
        val startTime = System.currentTimeMillis()
        val mapper = jacksonObjectMapper()

        val junit = JUnitCore()
        val listener = TestListener()
        junit.addListener(listener)
        junit.run(StateMachineTest::class.java)

        val res = Response().createJson(startTime, listener.result)

        mapper.writeValue(output, res)
    }
}