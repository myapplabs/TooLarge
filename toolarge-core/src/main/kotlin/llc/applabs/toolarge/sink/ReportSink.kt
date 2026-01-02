package llc.applabs.toolarge.sink

import llc.applabs.toolarge.model.TooLargeReport

interface ReportSink {
    fun emit(report: TooLargeReport)
}