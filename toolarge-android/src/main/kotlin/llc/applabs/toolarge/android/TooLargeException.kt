package llc.applabs.toolarge.android

import llc.applabs.toolarge.model.TooLargeReport


class ToolargeException(val report: TooLargeReport) :
    RuntimeException("Toolarge: ${report.origin} ${report.owner} total=${report.totalBytes}B")
