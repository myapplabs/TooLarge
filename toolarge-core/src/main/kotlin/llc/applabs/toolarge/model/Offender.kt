package llc.applabs.toolarge.model

import kotlinx.serialization.Serializable

@Serializable
data class Offender(
    val path: String,           // e.g. "android:viewHierarchyState[42]"
    val type: String,           // e.g. "SparseArray<Parcelable>"
    val bytes: Long,
    val summary: String? = null // e.g. "entries=120"
)