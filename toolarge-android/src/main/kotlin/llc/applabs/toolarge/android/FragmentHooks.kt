package llc.applabs.toolarge.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import llc.applabs.toolarge.model.Origin

internal object FragmentHooks {

    fun register(
        fragmentManager: FragmentManager,
        analyzer: Analyzer
    ) {
        fragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {

            override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                // FRAGMENT_ARGUMENTS: inspect early so you can catch huge args at creation time.
                val args = f.arguments
                if (args != null) {
                    analyzer.analyze(
                        origin = Origin.FRAGMENT_ARGUMENTS,
                        owner = f::class.java.name,
                        bundle = args,
                        metadata = mapOf(
                            "fragment" to f::class.java.name,
                            "hook" to "onFragmentPreCreated"
                        )
                    )
                }
            }

            override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                // FRAGMENT_RESTORED_INSTANCE_STATE: this is what Android is attempting to restore.
                if (savedInstanceState != null) {
                    analyzer.analyze(
                        origin = Origin.FRAGMENT_RESTORED_INSTANCE_STATE,
                        owner = f::class.java.name,
                        bundle = savedInstanceState,
                        metadata = mapOf(
                            "fragment" to f::class.java.name,
                            "hook" to "onFragmentCreated",
                            "kind" to "restoredInstanceState"
                        )
                    )
                }
            }

            override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
                analyzer.analyze(
                    origin = Origin.FRAGMENT_SAVE_INSTANCE_STATE,
                    owner = f::class.java.name,
                    bundle = outState,
                    metadata = mapOf(
                        "fragment" to f::class.java.name,
                        "hook" to "onFragmentSaveInstanceState"
                    )
                )
            }
        }, true)
    }
}