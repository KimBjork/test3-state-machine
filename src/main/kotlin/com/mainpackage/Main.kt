package com.mainpackage

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.runner.JUnitCore
import org.junit.runner.Result
import org.junit.runner.notification.RunListener
import java.io.OutputStream



class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val startTime = System.currentTimeMillis()

            val res = RunTests().getStringResult(startTime)
            println(res)
        }
    }

    fun handler(output: OutputStream): Unit {
        val startTime = System.currentTimeMillis()
        val mapper = jacksonObjectMapper()

        val res = RunTests().getJsonResult(startTime)
        mapper.writeValue(output, res)
    }
}