package llc.applabs.toolarge.sink

import llc.applabs.toolarge.sink.ReportSink
import llc.applabs.toolarge.model.TooLargeReport

class CompositeSink(private val sinks: List<ReportSink>) : ReportSink {
    override fun emit(report: TooLargeReport) = sinks.forEach { it.emit(report) }
}