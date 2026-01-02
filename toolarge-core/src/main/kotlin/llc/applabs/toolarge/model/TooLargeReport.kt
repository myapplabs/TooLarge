package llc.applabs.toolarge.model

import kotlinx.serialization.Serializable
import llc.applabs.toolarge.model.Offender
import llc.applabs.toolarge.model.Origin

@Serializable
data class TooLargeReport(
    val timestampMs: Long,
    val origin: Origin,
    val owner: String,
    val totalBytes: Long,
    val offenders: List<Offender>,
    val metadata: Map<String, String> = emptyMap(),
    val stacktrace: String? = null
)