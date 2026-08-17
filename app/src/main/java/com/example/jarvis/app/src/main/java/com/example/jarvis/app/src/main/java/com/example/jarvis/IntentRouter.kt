package com.example.jarvis

import android.content.Context
import com.example.jarvis.skills.CallSkill
import com.example.jarvis.skills.MessageSkill
import com.example.jarvis.skills.OpenAppSkill
import com.example.jarvis.skills.PlayVideoSkill
import com.example.jarvis.skills.Skill

/**
 * Add new voice abilities by writing a new Skill class and registering it here.
 * This is the one place you touch when you want Jarvis to learn something new.
 */
class IntentRouter {

    private val skills: List<Skill> = listOf(
        CallSkill(),
        MessageSkill(),
        OpenAppSkill(),
        PlayVideoSkill()
        // Add more skills here as you build them, e.g. WeatherSkill(), AlarmSkill()...
    )

    fun handle(context: Context, rawCommand: String): String {
        val command = rawCommand.trim().lowercase()

        val skill = skills.firstOrNull { it.matches(command) }
        return skill?.execute(context, command)
            ?: "Sorry sir, I don't know how to do that yet."
    }
}
