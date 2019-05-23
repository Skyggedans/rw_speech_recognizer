package com.example.rw_speech_recognizer

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import android.app.Activity
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter


class RwSpeechRecognizerPlugin(val activity: Activity, val channel: MethodChannel) : MethodCallHandler {
    private val ACTION_OVERRIDE_COMMANDS = "com.realwear.wearhf.intent.action.OVERRIDE_COMMANDS"
    private val ACTION_SPEECH_EVENT = "com.realwear.wearhf.intent.action.SPEECH_EVENT"
    private val ACTION_RESTORE_COMMANDS = "com.realwear.wearhf.intent.action.RESTORE_COMMANDS"
    private val EXTRA_SOURCE_PACKAGE = "com.realwear.wearhf.intent.extra.SOURCE_PACKAGE"
    private val EXTRA_COMMANDS = "com.realwear.wearhf.intent.extra.COMMANDS"
    private val EXTRA_RESULT = "command"

    init {
        val asrBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action

                if (action == ACTION_SPEECH_EVENT) {
                    val asrCommand = intent.getStringExtra(EXTRA_RESULT)

                    channel.invokeMethod("onSpeechEvent", mapOf("command" to asrCommand))
                }
            }
        }

        activity.registerReceiver(asrBroadcastReceiver, IntentFilter(ACTION_SPEECH_EVENT))
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "com.rockwellits.plugins/rw_speech_recognizer")

            channel.setMethodCallHandler(RwSpeechRecognizerPlugin(registrar.activity(), channel))
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when {
            call.method == "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
            call.method == "setCommands" -> {
                call.argument<ArrayList<String>>("commands")?.let { setCommands(it) }
                result.success(null)
            }
            call.method == "restoreCommands" -> {
                restoreCommands()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    private fun setCommands(commands: ArrayList<String>) {
        val intent = Intent(ACTION_OVERRIDE_COMMANDS)

        intent.putExtra(EXTRA_SOURCE_PACKAGE, activity.packageName)
        intent.putExtra(EXTRA_COMMANDS, commands)
        activity.sendBroadcast(intent)
    }

    private fun restoreCommands() {
        val intent = Intent(ACTION_RESTORE_COMMANDS)

        intent.putExtra(EXTRA_SOURCE_PACKAGE, activity.packageName)
        activity.sendBroadcast(intent)
    }
}
