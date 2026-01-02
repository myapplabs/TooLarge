package llc.applabs.toolarge.model

import kotlinx.serialization.Serializable

@Serializable
enum class Origin {
    ACTIVITY_SAVE_INSTANCE_STATE,
    ACTIVITY_INTENT_EXTRAS,
    FRAGMENT_SAVE_INSTANCE_STATE,
    FRAGMENT_RESTORED_INSTANCE_STATE,
    FRAGMENT_ARGUMENTS,
    INTENT_EXTRAS,
    NAV_BACK_STACK,
    SAVED_STATE_REGISTRY
}