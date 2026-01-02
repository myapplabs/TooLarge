package llc.applabs.toolarge.policy

import llc.applabs.toolarge.model.Origin

data class PolicyConfig(
    val budgets: Map<Origin, Budget>,
    val topN: Int = 20,
    val includeStacktrace: Boolean = true
)