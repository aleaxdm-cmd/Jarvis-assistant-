package com.example.jarvis.skills

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Handles: "play believer by imagine dragons", "play funny cat videos"
 * Sends the query straight to YouTube's search intent.
 * Swap this out later for a local MediaStore lookup if you want offline video files instead.
 */
class PlayVideoSkill : Skill {

    override fun matches(command: String): Boolean {
        return command.startsWith("play ")
    }

    override fun execute(context: Context, command: String): String {
        val query = command.removePrefix("play ").trim()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(query))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return "Playing $query, sir."
    }
}
