package io.r_a_d.radio2.preferences

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import io.r_a_d.radio2.R
import io.r_a_d.radio2.preferenceStore
import io.r_a_d.radio2.streamerNotificationService.WorkerStore
import io.r_a_d.radio2.streamerNotificationService.startStreamerMonitor
import io.r_a_d.radio2.streamerNotificationService.stopStreamerMonitor

class StreamerNotifServiceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.streamer_notif_service_preferences, rootKey)


        val streamerPeriod = preferenceScreen.findPreference<Preference>("streamerMonitorPeriodPref")

        val streamerNotification = preferenceScreen.findPreference<Preference>("newStreamerNotification")
        streamerNotification?.setOnPreferenceChangeListener { _, newValue ->
            if ((newValue as Boolean)) {
                val builder1 = AlertDialog.Builder(context!!)
                builder1.setMessage(R.string.warningStreamerNotif)
                builder1.setCancelable(false)
                builder1.setPositiveButton(
                    "Yes"
                ) { dialog, _ ->
                    startStreamerMonitor(context!!, force = true) // force enabled because the preference value is not yet set when running this callback.
                    streamerPeriod?.summary = "Every ${(preferenceStore.getString("streamerMonitorPeriodPref", "") as String)} minutes"
                    streamerPeriod?.isEnabled = true
                    dialog.cancel()
                }

                builder1.setNegativeButton(
                    "No"
                ) { dialog, _ ->
                    // we force-reset the switch (that's why I use commit() )
                    val preferences = PreferenceManager.getDefaultSharedPreferences(context!!)
                    val editor = preferences.edit()
                    editor.putBoolean("newStreamerNotification", false)
                    editor.commit()
                    stopStreamerMonitor(context!!)
                    (streamerNotification as SwitchPreferenceCompat).isChecked = false
                    dialog.cancel()
                }

                val alert11 = builder1.create()
                alert11.show()
            }
            else {
                stopStreamerMonitor(context!!)
                streamerPeriod?.isEnabled = false
                WorkerStore.instance.isServiceStarted = false
            }
            true
        }

        streamerPeriod?.summary = "Every ${(preferenceStore.getString("streamerMonitorPeriodPref", "") as String)} minutes"
        streamerPeriod?.isEnabled = preferenceStore.getBoolean("newStreamerNotification", true)
        streamerPeriod?.setOnPreferenceChangeListener { _, newValue ->
            // quite nothing
            streamerPeriod.summary = "Every ${(newValue as String)} minutes"
            WorkerStore.instance.tickerPeriod = (Integer.parseInt(newValue)).toLong() * 60
            // this should be sufficient, the next alarm schedule should take the new tickerPeriod.
            true
        }


    }
}