package llc.applabs.toolarge.android


import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import llc.applabs.toolarge.model.Origin
import llc.applabs.toolarge.policy.PolicyConfig
import llc.applabs.toolarge.sink.ReportSink

object TooLarge {

    data class AndroidConfig(
        val policy: PolicyConfig,
        val sink: ReportSink,
        val debug: Boolean
    )

    fun install(app: Application, cfg: AndroidConfig) {
        val analyzer = Analyzer(cfg.policy, cfg.sink, cfg.debug)

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // Register fragment hooks for FragmentActivity instances.
                if (activity is FragmentActivity) {
                    FragmentHooks.register(activity.supportFragmentManager, analyzer)
                }

                // Inspect Activity intent extras early.
                val extras = activity.intent?.extras
                if (extras != null) {
                    analyzer.analyze(
                        origin = Origin.ACTIVITY_INTENT_EXTRAS,
                        owner = activity::class.java.name,
                        bundle = extras,
                        metadata = mapOf(
                            "activity" to activity::class.java.name,
                            "hook" to "onActivityCreated",
                            "kind" to "intent.extras"
                        )
                    )
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                analyzer.analyze(
                    origin = Origin.ACTIVITY_SAVE_INSTANCE_STATE,
                    owner = activity::class.java.name,
                    bundle = outState,
                    metadata = mapOf("activity" to activity::class.java.name)
                )
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    fun selfTest(app: Application, cfg: AndroidConfig) {
        val test = Bundle().apply {
            putString("small", "hi")
            putByteArray("bigBytes", ByteArray(300_000))
            putBundle("nested", Bundle().apply {
                putByteArray("nestedBig", ByteArray(250_000))
            })
        }
        Analyzer(cfg.policy, cfg.sink, cfg.debug).analyze(
            origin = Origin.ACTIVITY_SAVE_INSTANCE_STATE,
            owner = "SELF_TEST",
            bundle = test,
            metadata = mapOf("selfTest" to "true")
        )
    }
}