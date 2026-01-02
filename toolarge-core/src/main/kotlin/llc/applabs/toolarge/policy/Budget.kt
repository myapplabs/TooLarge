package llc.applabs.toolarge.policy

data class Budget(
    val warnBytes: Long,
    val errorBytes: Long,
    val action: Action = Action.LOG
) {
    enum class Action { LOG, THROW_IN_DEBUG }
}