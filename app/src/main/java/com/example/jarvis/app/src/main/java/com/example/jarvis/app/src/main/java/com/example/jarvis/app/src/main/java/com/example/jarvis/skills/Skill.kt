package com.example.jarvis.skills

import android.content.Context

/**
 * Every voice "skill" (call, open app, message, play video, ...) implements this.
 * matches() decides if this skill should handle the recognized speech.
 * execute() actually does the action and returns a spoken reply.
 */
interface Skill {
    fun matches(command: String): Boolean
    fun execute(context: Context, command: String): String
}
