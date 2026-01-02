package llc.applabs.toolarge.sample

import android.os.Bundle
import androidx.fragment.app.Fragment

class BigArgsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fragment initialization
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        
        // Deliberately bloat fragment saved state to demonstrate logging
        outState.putByteArray("fragment_big", ByteArray(350_000))
    }

    companion object {
        private const val ARG_BYTES = "arg_bytes"

        fun newInstance(bytes: Int): BigArgsFragment {
            return BigArgsFragment().apply {
                arguments = Bundle().apply {
                    putByteArray(ARG_BYTES, ByteArray(bytes))
                }
            }
        }
    }
}
