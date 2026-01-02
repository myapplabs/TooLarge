package llc.applabs.toolarge.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.extras == null) {
            intent.putExtra("intent_big", ByteArray(120_000))
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BigArgsFragment.newInstance(bytes = 180_000))
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Deliberately bloat activity saved state to demonstrate logging
        outState.putByteArray("activity_big", ByteArray(220_000))
    }
}
