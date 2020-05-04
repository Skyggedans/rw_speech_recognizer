package com.rockwellits.rw_plugins.rw_speech_recognizer

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


class RwSpeechRecognizerPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, Application.ActivityLifecycleCallbacks {
    lateinit var activity: Activity
    lateinit var channel: MethodChannel
    private var commands: ArrayList<String>? = null

    private val asrBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (action == ACTION_SPEECH_EVENT) {
                val asrCommand = intent.getStringExtra(EXTRA_RESULT)

                channel.invokeMethod("onSpeechEvent", mapOf("command" to asrCommand))
            }
        }
    }

    companion object {
        private const val CHANNEL = "com.rockwellits.rw_plugins/rw_speech_recognizer"
        private const val ACTION_OVERRIDE_COMMANDS = "com.realwear.wearhf.intent.action.OVERRIDE_COMMANDS"
        private const val ACTION_SPEECH_EVENT = "com.realwear.wearhf.intent.action.SPEECH_EVENT"
        private const val ACTION_RESTORE_COMMANDS = "com.realwear.wearhf.intent.action.RESTORE_COMMANDS"
        private const val EXTRA_SOURCE_PACKAGE = "com.realwear.wearhf.intent.extra.SOURCE_PACKAGE"
        private const val EXTRA_COMMANDS = "com.realwear.wearhf.intent.extra.COMMANDS"
        private const val EXTRA_RESULT = "command"

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), CHANNEL)
            val plugin = RwSpeechRecognizerPlugin()

            plugin.activity = registrar.activity()
            plugin.channel = channel
            plugin.initialize()

            channel.setMethodCallHandler(plugin)
        }
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val channel = MethodChannel(flutterPluginBinding.getFlutterEngine().dartExecutor, CHANNEL)

        this.channel = channel
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "setCommands" -> {
                call.argument<ArrayList<String>>("commands")?.let { setCommands(it) }
                result.success(null)
            }
            "restoreCommands" -> {
                restoreCommands()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        activity.unregisterReceiver(asrBroadcastReceiver)
        restoreCommands()
    }

    override fun onActivityResumed(activity: Activity) {
        activity.registerReceiver(asrBroadcastReceiver, IntentFilter(ACTION_SPEECH_EVENT))
        applyCommands()
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activity = binding.activity
        this.initialize()
    }

    override fun onDetachedFromActivity() {
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    fun initialize() {
        assert(::activity.isInitialized)

        activity.application.registerActivityLifecycleCallbacks(this)
        activity.registerReceiver(asrBroadcastReceiver, IntentFilter(ACTION_SPEECH_EVENT))
    }

    private fun applyCommands() {
        if (commands != null) {
            val intent = Intent(ACTION_OVERRIDE_COMMANDS)

            intent.putExtra(EXTRA_SOURCE_PACKAGE, activity.packageName)
            intent.putExtra(EXTRA_COMMANDS, commands)
            activity.sendBroadcast(intent)
        }
    }

    private fun setCommands(commandsList: ArrayList<String>) {
        commands = commandsList
        applyCommands()
    }

    private fun restoreCommands() {
        val intent = Intent(ACTION_RESTORE_COMMANDS)

        intent.putExtra(EXTRA_SOURCE_PACKAGE, activity.packageName)
        activity.sendBroadcast(intent)
    }
}
