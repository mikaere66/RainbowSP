package com.michaelrmossman.kotlin.rainbows.utils

import android.util.Log
import com.stericson.RootShell.exceptions.RootDeniedException
import com.stericson.RootShell.execution.Command
import com.stericson.RootTools.RootTools
import java.io.IOException
import java.util.concurrent.TimeoutException

object RootUtils {

    fun echoCommand(id: Int, value: Int, path: String) {
        val command = "echo $value > $path"
        rootCommand(id, command)
    }

    private fun rootCommand(id: Int, commandLine: String) {
        if (debug) { Log.d("COMMAND LINE $id", commandLine) }

        val command: Command = object: Command(id, commandLine) {
            override fun commandOutput(id: Int, line: String) {
                super.commandOutput(id, line)
                if (debug) Log.d("COMMAND OUTPUT $id", line)
            }

            override fun commandTerminated(id: Int, reason: String) {
                super.commandTerminated(id, reason)
                if (debug) Log.d("COMMAND REASON $id", reason)
            }

            override fun commandCompleted(id: Int, exitcode: Int) {
                super.commandCompleted(id, exitcode)
                if (debug) Log.d(
                    "COMMAND COMPLETED $id", exitcode.toString()
                )
            }
        }

        try {
            RootTools.getShell(true).add(command)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RootDeniedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
    }
}
