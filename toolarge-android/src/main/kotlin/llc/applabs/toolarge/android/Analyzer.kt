package llc.applabs.toolarge.android


import android.os.Bundle
import llc.applabs.toolarge.model.Origin
import llc.applabs.toolarge.model.TooLargeReport
import llc.applabs.toolarge.policy.Budget
import llc.applabs.toolarge.policy.PolicyConfig
import llc.applabs.toolarge.sink.ReportSink

internal class Analyzer(
    private val config: PolicyConfig,
    private val sink: ReportSink,
    private val isDebug: Boolean
) {
    fun analyze(origin: Origin, owner: String, bundle: Bundle, metadata: Map<String, String> = emptyMap()) {
        val total = ParcelSizer.sizeOfBundle(bundle)
        val offenders = BundleInspector(config.topN).inspect(bundle)
        val budget = config.budgets[origin]
        val stack = if (config.includeStacktrace) Throwable().stackTraceToString() else null

        val report = TooLargeReport(
            timestampMs = System.currentTimeMillis(),
            origin = origin,
            owner = owner,
            totalBytes = total,
            offenders = offenders,
            metadata = metadata,
            stacktrace = stack
        )

        if (budget == null) {
            sink.emit(report)
            return
        }

        when {
            total >= budget.errorBytes -> {
                sink.emit(report)
                if (budget.action == Budget.Action.THROW_IN_DEBUG && isDebug) throw ToolargeException(report)
            }
            total >= budget.warnBytes -> sink.emit(report)
        }
    }
}
