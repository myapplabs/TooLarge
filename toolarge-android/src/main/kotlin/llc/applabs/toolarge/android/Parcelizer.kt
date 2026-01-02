package llc.applabs.toolarge.android

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray

internal object ParcelSizer {

    fun sizeOfBundle(bundle: Bundle): Long {
        val p = Parcel.obtain()
        return try {
            p.writeBundle(bundle)
            p.dataSize().toLong()
        } finally {
            p.recycle()
        }
    }

    fun sizeOfValue(value: Any?): Long {
        val p = Parcel.obtain()
        return try {
            writeAnyToParcel(p, value)
            p.dataSize().toLong()
        } finally {
            p.recycle()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun writeAnyToParcel(p: Parcel, value: Any?) {
        when (value) {
            null -> p.writeInt(0)
            is Bundle -> p.writeBundle(value)
            is Parcelable -> p.writeParcelable(value, 0)
            is CharSequence -> p.writeString(value.toString())
            is String -> p.writeString(value)
            is Int -> p.writeInt(value)
            is Long -> p.writeLong(value)
            is Boolean -> p.writeInt(if (value) 1 else 0)
            is Float -> p.writeFloat(value)
            is Double -> p.writeDouble(value)
            is ByteArray -> { p.writeInt(value.size); p.writeByteArray(value) }
            is IntArray -> p.writeIntArray(value)
            is LongArray -> p.writeLongArray(value)
            is Array<*> -> p.writeArray(value)
            is List<*> -> p.writeList(value)
            is Map<*, *> -> p.writeMap(value)
            is SparseArray<*> -> p.writeSparseArray(value as SparseArray<Any?>)
            else -> {
                if (value is java.io.Serializable) p.writeSerializable(value)
                else p.writeString(value.toString())
            }
        }
    }
}
