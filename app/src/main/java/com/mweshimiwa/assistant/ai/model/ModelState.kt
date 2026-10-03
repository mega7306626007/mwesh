package com.mweshimiwa.assistant.ai.model

enum class ModelState {
    UNINITIALIZED,
    LOADING,
    READY,
    GENERATING,
    CANCELLING,
    ERROR,
    UNAVAILABLE,
    UPDATING
}
