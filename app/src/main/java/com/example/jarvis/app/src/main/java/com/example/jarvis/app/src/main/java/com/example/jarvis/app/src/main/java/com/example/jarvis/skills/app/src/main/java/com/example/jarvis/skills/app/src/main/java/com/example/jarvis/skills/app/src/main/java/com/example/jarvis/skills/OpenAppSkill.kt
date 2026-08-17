package com.example.jarvis.skills

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo

/**
 * Handles: "open whatsapp", "open youtube", "open camera" etc.
 */
class OpenAppSkill : Skill {

    override fun matches(command: String): Boolean {
        return command.startsWith("open ")
    }

    override fun execute(context: Context, command: String): String {
        val appName = command.removePrefix("open ").trim()
        val pm = context.packageManager
        val apps: List<ApplicationInfo> = pm.getInstalledApplications(0)

        // Find an installed app whose visible label roughly matches what was said
        val match = apps.firstOrNull {
            pm.getApplicationLabel(it).toString().equals(appName, ignoreCase = true)
        } ?: apps.firstOrNull {
            pm.getApplicationLabel(it).toString().contains(appName, ignoreCase = true)
        }

        if (match == null) {
            return "I couldn't find an app called $appName, sir."
        }

        val launchIntent = pm.getLaunchIntentForPackage(match.packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            "Opening $appName, sir."
        } else {
            "I found $appName but couldn't launch it, sir."
        }
    }
}
