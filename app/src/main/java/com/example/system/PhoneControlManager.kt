package com.example.system

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.view.KeyEvent
import androidx.core.content.ContextCompat

data class PhoneActionResult(
    val executed: Boolean,
    val summary: String,
    val requiresPermission: String? = null,
    val closestAlternative: String? = null
)

class PhoneControlManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    fun executeAction(command: String): PhoneActionResult? {
        val lower = command.lowercase().trim()

        // 1. Flashlight
        if (lower.contains("flashlight") || lower.contains("torch")) {
            val turnOn = !lower.contains("off") && !lower.contains("stop")
            return toggleFlashlight(turnOn)
        }

        // 2. Volume control
        if (lower.contains("volume") || lower.contains("sound")) {
            return when {
                lower.contains("up") || lower.contains("increase") || lower.contains("raise") || lower.contains("higher") -> {
                    adjustVolume(AudioManager.ADJUST_RAISE)
                }
                lower.contains("down") || lower.contains("decrease") || lower.contains("lower") || lower.contains("reduce") -> {
                    adjustVolume(AudioManager.ADJUST_LOWER)
                }
                lower.contains("mute") || lower.contains("silent") -> {
                    adjustVolume(AudioManager.ADJUST_MUTE)
                }
                lower.contains("unmute") || lower.contains("max") -> {
                    adjustVolume(AudioManager.ADJUST_UNMUTE)
                }
                else -> adjustVolume(AudioManager.ADJUST_SAME)
            }
        }

        // 3. Music Control
        if (lower.contains("play music") || lower.contains("resume music") || lower.contains("play song")) {
            return dispatchMediaEvent(KeyEvent.KEYCODE_MEDIA_PLAY, "Playing your music! 🎵")
        }
        if (lower.contains("pause music") || lower.contains("stop music") || lower.contains("pause song")) {
            return dispatchMediaEvent(KeyEvent.KEYCODE_MEDIA_PAUSE, "Paused the music for you.")
        }
        if (lower.contains("next track") || lower.contains("next song") || lower.contains("skip song")) {
            return dispatchMediaEvent(KeyEvent.KEYCODE_MEDIA_NEXT, "Skipping to next track! 🎶")
        }

        // 4. Set Alarm
        if (lower.contains("alarm") || lower.contains("wake me up")) {
            return setAlarmFromText(lower)
        }

        // 5. Open Apps
        if (lower.startsWith("open ") || lower.startsWith("launch ") || lower.contains("open the ")) {
            val target = lower.removePrefix("open the ").removePrefix("open ").removePrefix("launch ").trim()
            return openAppOrFeature(target)
        }

        // 6. Call Contact / Phone
        if (lower.startsWith("call ") || lower.contains("phone call") || lower.contains("dial ")) {
            val target = lower.removePrefix("call ").removePrefix("dial ").trim()
            return initiateCall(target)
        }

        // 7. Send Message / SMS
        if (lower.startsWith("send a message") || lower.startsWith("send message") || lower.startsWith("text ")) {
            return prepareSms(lower)
        }

        // 8. Notifications
        if (lower.contains("notification") || lower.contains("show notifications")) {
            return openNotificationSettings()
        }

        // 9. Bluetooth / WiFi Settings
        if (lower.contains("bluetooth")) {
            return openBluetoothSettings()
        }
        if (lower.contains("wifi") || lower.contains("wi-fi")) {
            return openWifiSettings()
        }

        return null
    }

    fun openAppOrFeature(target: String): PhoneActionResult {
        val clean = target.replace(".", "").trim()
        when {
            clean.contains("whatsapp") -> {
                return launchPackageOrStore("com.whatsapp", "WhatsApp")
            }
            clean.contains("youtube") -> {
                return launchPackageOrWeb("com.google.android.youtube", "YouTube", "https://www.youtube.com")
            }
            clean.contains("camera") -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return tryStartActivity(intent, "Opening the camera for you! 📸")
            }
            clean.contains("gallery") || clean.contains("photo") -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    type = "image/*"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return tryStartActivity(intent, "Opening your photos! ✨")
            }
            clean.contains("setting") -> {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return tryStartActivity(intent, "Opening phone settings for you.")
            }
            clean.contains("browser") || clean.contains("chrome") || clean.contains("internet") -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return tryStartActivity(intent, "Opening browser for you!")
            }
            clean.contains("map") || clean.contains("google maps") -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return tryStartActivity(intent, "Opening Maps!")
            }
            else -> {
                // Try searching for installed application matching name
                val pm = context.packageManager
                val intent = pm.getLaunchIntentForPackage(clean)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return PhoneActionResult(true, "Opening $target!")
                }
                // Try generic web search / store search
                val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, target)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                return tryStartActivity(
                    searchIntent,
                    "Searching for $target",
                    closestAlternative = "I couldn't find an installed app named '$target', so I opened search for you!"
                )
            }
        }
    }

    private fun launchPackageOrStore(packageName: String, appName: String): PhoneActionResult {
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            PhoneActionResult(true, "Opening $appName for you! ✨")
        } else {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(marketIntent)
                PhoneActionResult(true, "$appName isn't installed yet, taking you to Google Play Store to get it!", closestAlternative = "Opening Play Store")
            } catch (e: Exception) {
                PhoneActionResult(false, "$appName isn't installed on your device.")
            }
        }
    }

    private fun launchPackageOrWeb(packageName: String, appName: String, fallbackUrl: String): PhoneActionResult {
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            PhoneActionResult(true, "Opening $appName for you! 🌟")
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            tryStartActivity(webIntent, "Opening $appName in your browser!", closestAlternative = "Opened browser fallback")
        }
    }

    fun initiateCall(target: String): PhoneActionResult {
        val hasCallPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

        // Extract number if present
        val cleanNumber = target.filter { it.isDigit() || it == '+' }
        val phoneNumber = if (cleanNumber.length >= 3) {
            cleanNumber
        } else {
            findContactNumber(target) ?: target
        }

        return if (hasCallPermission && phoneNumber.any { it.isDigit() }) {
            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            tryStartActivity(callIntent, "Calling $target right now! 📞")
        } else {
            // User-friendly safe alternative: ACTION_DIAL with prefilled number
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            tryStartActivity(
                dialIntent,
                "Ready to call $target! Press dial to connect safely.",
                closestAlternative = "Opened dialer with contact prefilled so you can confirm before calling."
            )
        }
    }

    private fun findContactNumber(contactName: String): String? {
        val hasContactsPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasContactsPermission) return null

        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
                arrayOf("%$contactName%"),
                null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    if (numberIdx >= 0) return it.getString(numberIdx)
                }
            }
        } catch (_: Exception) {}
        return null
    }

    fun prepareSms(input: String): PhoneActionResult {
        var recipient = ""
        var msgBody = ""

        if (input.contains("to ")) {
            val parts = input.split("to ", limit = 2)
            val afterTo = parts.getOrNull(1)?.trim() ?: ""
            if (afterTo.contains(" saying ")) {
                val splitSaying = afterTo.split(" saying ", limit = 2)
                recipient = splitSaying[0].trim()
                msgBody = splitSaying[1].trim()
            } else if (afterTo.contains(" that ")) {
                val splitThat = afterTo.split(" that ", limit = 2)
                recipient = splitThat[0].trim()
                msgBody = splitThat[1].trim()
            } else {
                recipient = afterTo
            }
        }

        val phoneNum = findContactNumber(recipient) ?: recipient.filter { it.isDigit() || it == '+' }
        val uri = if (phoneNum.isNotEmpty()) Uri.parse("smsto:$phoneNum") else Uri.parse("smsto:")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            if (msgBody.isNotEmpty()) putExtra("sms_body", msgBody)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return tryStartActivity(
            intent,
            "Opening messages to text ${recipient.ifEmpty { "your contact" }}! 💬",
            closestAlternative = "Opened SMS composer with draft ready for your confirmation."
        )
    }

    fun setAlarmFromText(text: String): PhoneActionResult {
        var hour = 7
        var minute = 0
        var isPm = false

        if (text.contains("pm") || text.contains("evening") || text.contains("night")) isPm = true
        if (text.contains("am") || text.contains("morning")) isPm = false

        val regex = Regex("""(\d{1,2})(?::(\d{2}))?""")
        val match = regex.find(text)
        if (match != null) {
            val h = match.groupValues[1].toIntOrNull() ?: 7
            val m = match.groupValues.getOrNull(2)?.takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
            hour = h
            minute = m
            if (isPm && hour < 12) hour += 12
            if (!isPm && hour == 12 && text.contains("am")) hour = 0
        }

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Aisha Wakeup Alarm ❤️")
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val displayTime = String.format("%02d:%02d", hour, minute)
        return tryStartActivity(intent, "Setting alarm for $displayTime! Sleep well, darling! ⏰")
    }

    private fun adjustVolume(direction: Int): PhoneActionResult {
        if (audioManager == null) return PhoneActionResult(false, "Audio manager unavailable.")
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
        val text = when (direction) {
            AudioManager.ADJUST_RAISE -> "Turned the volume up! 🔊"
            AudioManager.ADJUST_LOWER -> "Turned the volume down. 🔉"
            AudioManager.ADJUST_MUTE -> "Muted the audio. 🔇"
            AudioManager.ADJUST_UNMUTE -> "Unmuted the audio. 🔊"
            else -> "Volume adjusted."
        }
        return PhoneActionResult(true, text)
    }

    private fun dispatchMediaEvent(keyCode: Int, message: String): PhoneActionResult {
        if (audioManager == null) return PhoneActionResult(false, "Audio manager unavailable.")
        val down = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        val up = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        audioManager.dispatchMediaKeyEvent(down)
        audioManager.dispatchMediaKeyEvent(up)
        return PhoneActionResult(true, message)
    }

    private fun toggleFlashlight(turnOn: Boolean): PhoneActionResult {
        if (cameraManager == null) {
            return PhoneActionResult(false, "Camera hardware not available on this device.")
        }
        return try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: cameraManager.cameraIdList.firstOrNull()

            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, turnOn)
                val msg = if (turnOn) "Flashlight turned on! 💡 Shining bright for you!" else "Flashlight turned off! 🌙"
                PhoneActionResult(true, msg)
            } else {
                PhoneActionResult(false, "No flashlight found on this device.")
            }
        } catch (e: Exception) {
            PhoneActionResult(false, "Couldn't control flashlight: ${e.message}")
        }
    }

    private fun openBluetoothSettings(): PhoneActionResult {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return tryStartActivity(intent, "Opening Bluetooth settings for you! 📶")
    }

    private fun openWifiSettings(): PhoneActionResult {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return tryStartActivity(intent, "Opening Wi-Fi settings for you! 🌐")
    }

    private fun openNotificationSettings(): PhoneActionResult {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return tryStartActivity(
            intent,
            "Opening notification settings! 🔔",
            closestAlternative = "Taking you directly to system notification settings."
        )
    }

    private fun tryStartActivity(
        intent: Intent,
        successMessage: String,
        closestAlternative: String? = null
    ): PhoneActionResult {
        return try {
            context.startActivity(intent)
            PhoneActionResult(true, successMessage, closestAlternative = closestAlternative)
        } catch (e: Exception) {
            PhoneActionResult(false, "Unable to perform action: ${e.message}")
        }
    }
}
