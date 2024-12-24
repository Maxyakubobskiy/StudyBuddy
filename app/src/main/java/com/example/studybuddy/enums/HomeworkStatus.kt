package com.example.studybuddy.enums

enum class HomeworkStatus(val displayName: String) {
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    companion object {
        fun getDisplayNames() = entries.map { it.displayName }
    }
}