package llc.applabs.toolarge.android


import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import llc.applabs.toolarge.model.Offender

internal class BundleInspector(private val topN: Int) {

    fun inspect(bundle: Bundle): List<Offender> {
        val out = mutableListOf<Offender>()
        inspectBundle(bundle, pathPrefix = "", out = out)
        return out.sortedByDescending { it.bytes }.take(topN)
    }

    private fun inspectBundle(bundle: Bundle, pathPrefix: String, out: MutableList<Offender>) {
        for (key in bundle.keySet()) {
            val value = bundle.get(key)
            val path = if (pathPrefix.isEmpty()) key else "$pathPrefix/$key"

            if (key == "android:viewHierarchyState" && value is SparseArray<*>) {
                inspectViewHierarchyState(path, value, out)
                continue
            }

            val bytes = sizeOfEntry(key, value)
            out += Offender(
                path = path,
                type = value?.javaClass?.name ?: "null",
                bytes = bytes,
                summary = summaryOf(value)
            )

            if (value is Bundle) {
                inspectBundle(value, path, out)
            }
        }
    }

    private fun sizeOfEntry(key: String, value: Any?): Long {
        val tmp = Bundle()
        when (value) {
            null -> tmp.putString(key, null)
            is Bundle -> tmp.putBundle(key, value)
            is Parcelable -> tmp.putParcelable(key, value)
            is String -> tmp.putString(key, value)
            is CharSequence -> tmp.putCharSequence(key, value)
            is Int -> tmp.putInt(key, value)
            is Long -> tmp.putLong(key, value)
            is Boolean -> tmp.putBoolean(key, value)
            is Float -> tmp.putFloat(key, value)
            is Double -> tmp.putDouble(key, value)
            is ByteArray -> tmp.putByteArray(key, value)
            is IntArray -> tmp.putIntArray(key, value)
            is LongArray -> tmp.putLongArray(key, value)
            is java.io.Serializable -> tmp.putSerializable(key, value)
            else -> tmp.putString(key, value.toString())
        }
        return ParcelSizer.sizeOfBundle(tmp)
    }

    private fun inspectViewHierarchyState(path: String, sparse: SparseArray<*>, out: MutableList<Offender>) {
        val size = sparse.size()
        for (i in 0 until size) {
            val k = sparse.keyAt(i)
            val v = sparse.valueAt(i)
            val bytes = ParcelSizer.sizeOfValue(v)
            out += Offender(
                path = "$path[$k]",
                type = v?.javaClass?.name ?: "null",
                bytes = bytes,
                summary = "viewStateEntry"
            )
        }

        out += Offender(
            path = path,
            type = "SparseArray<Parcelable>",
            bytes = ParcelSizer.sizeOfValue(sparse),
            summary = "entries=$size"
        )
    }

    private fun summaryOf(value: Any?): String? = when (value) {
        is String -> "length=${value.length}"
        is CharSequence -> "length=${value.length}"
        is ByteArray -> "bytes=${value.size}"
        is IntArray -> "count=${value.size}"
        is LongArray -> "count=${value.size}"
        is List<*> -> "count=${value.size}"
        is Map<*, *> -> "count=${value.size}"
        is Bundle -> "keys=${value.keySet().size}"
        is SparseArray<*> -> "entries=${value.size()}"
        else -> null
    }
}
