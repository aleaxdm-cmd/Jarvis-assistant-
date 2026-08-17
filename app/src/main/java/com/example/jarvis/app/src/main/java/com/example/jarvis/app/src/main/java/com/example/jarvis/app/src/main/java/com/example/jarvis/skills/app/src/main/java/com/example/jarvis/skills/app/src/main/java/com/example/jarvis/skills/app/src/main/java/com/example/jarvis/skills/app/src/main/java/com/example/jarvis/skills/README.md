# Jarvis — Android voice assistant scaffold

This is a working skeleton for a "Hey Jarvis" voice assistant that can open apps,
call contacts, send texts, and play videos on voice command.

## What's already wired up

- **MainActivity** — requests the runtime permissions Jarvis needs, then starts the service.
- **WakeWordService** — the always-on foreground service using Picovoice Porcupine's
  built-in "Jarvis" wake word. Pauses itself while capturing a spoken command, then resumes.
- **VoiceCommandListener** — wraps Android's built-in `SpeechRecognizer` to turn speech into text.
- **IntentRouter** — looks at the recognized sentence and picks the right "skill" to run.
- **Skills** (`skills/` folder) — one class per ability:
  - `OpenAppSkill` — "open whatsapp"
  - `CallSkill` — "call mom"
  - `MessageSkill` — "message john saying I'm on my way"
  - `PlayVideoSkill` — "play believer by imagine dragons" (opens YouTube search)

## What YOU still need to do

### 1. Add your Porcupine AccessKey
1. Sign up free at https://console.picovoice.ai and copy your AccessKey
2. In `WakeWordService.kt`, replace `"YOUR_ACCESS_KEY_HERE"` with that key
3. Build and run on a real device — say "Jarvis" and you should hear "Hi sir, how can I help?"

### 2. Test each skill one at a time
Once the wake word is wired in, test in this order: open app → call → message → play video.

### 3. Handle permission denials gracefully
Right now if the user denies a permission, nothing happens. You'll want a screen that
explains why Jarvis needs mic/call/SMS/contacts access and links to Settings.

### 4. (Optional) Smarter command understanding
The current skills use simple string matching. Once the basics work, you can swap
`IntentRouter.handle()` to send the recognized sentence to an LLM API instead, for
much more flexible phrasing.

### 5. Battery
Always-on listening drains battery. Test real-world battery impact once the wake word
engine is in.

## Permissions this app requests
`RECORD_AUDIO`, `CALL_PHONE`, `SEND_SMS`, `READ_CONTACTS`, `POST_NOTIFICATIONS`
