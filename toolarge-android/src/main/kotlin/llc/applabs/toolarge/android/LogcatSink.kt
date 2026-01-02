package llc.applabs.toolarge.android

import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import llc.applabs.toolarge.model.TooLargeReport
import llc.applabs.toolarge.sink.ReportSink

class LogcatSink(
    private val tag: String = "toolarge",
    private val json: Boolean = false
) : ReportSink {

    private val serializer = Json { prettyPrint = false; ignoreUnknownKeys = true }

    override fun emit(report: TooLargeReport) {
        if (json) {
            Log.w(tag, serializer.encodeToString(report))
            return
        }

        Log.w(tag, "TOOLARGE origin=${report.origin} owner=${report.owner} total=${report.totalBytes} bytes")
        report.offenders.forEachIndexed { idx, o ->
            Log.w(tag, "  #$idx ${o.bytes}B  ${o.path}  (${o.type}) ${o.summary.orEmpty()}")
        }
        report.stacktrace?.let { Log.w(tag, it) }
    }
}
