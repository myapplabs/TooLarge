package llc.applabs.toolarge.sample

import android.app.Application
import llc.applabs.toolarge.android.LogcatSink
import llc.applabs.toolarge.android.TooLarge
import llc.applabs.toolarge.model.Origin
import llc.applabs.toolarge.policy.Budget
import llc.applabs.toolarge.policy.PolicyConfig

class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val policy = PolicyConfig(
            budgets = mapOf(
                Origin.ACTIVITY_SAVE_INSTANCE_STATE to Budget(200_000, 500_000),
                Origin.ACTIVITY_INTENT_EXTRAS to Budget(
                    warnBytes = 50_000,
                    errorBytes = 200_000,
                    action = Budget.Action.LOG
                ),
                Origin.FRAGMENT_ARGUMENTS to Budget(50_000, 200_000),
                Origin.FRAGMENT_SAVE_INSTANCE_STATE to Budget(100_000, 300_000),
                Origin.FRAGMENT_RESTORED_INSTANCE_STATE to Budget(
                    warnBytes = 100_000,
                    errorBytes = 300_000,
                    action = Budget.Action.LOG
                ),
            )
        )

        TooLarge.install(
            this,
            TooLarge.AndroidConfig(
                policy = policy,
                sink = LogcatSink(tag = "toolarge"),
                debug = BuildConfig.DEBUG
            )
        )
    }
}
