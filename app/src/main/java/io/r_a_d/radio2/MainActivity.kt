package io.r_a_d.radio2

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.Menu
import com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED
import androidx.lifecycle.Observer
import androidx.navigation.NavController


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.labelVisibilityMode = LABEL_VISIBILITY_LABELED

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_nowplaying, R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if(!PlayerStore.instance.isServiceStarted.value!!)
        {
            // if the service is not started, start it in STOP mode.
            // It's not a dummy action : with STOP mode, the player does not buffer audio (and does not use data connection without the user's consent).
            // this is useful since the service must be started to register bluetooth devices buttons.
            // (in case someone opens the app then pushes the PLAY button from their bluetooth device)
            val i = Intent(this, RadioService::class.java)
            i.putExtra("action", Actions.STOP.name)
            Log.d(activityTag, "Starting the service in foreground")
            startService(i)
        }

        PlayerStore.instance.isPlaying.observe(this, Observer { newValue ->
            if (PlayerStore.instance.isPlaying.value!!)
                actionOnService(Actions.PLAY)
            else
                actionOnService(Actions.STOP)
        })

        PlayerStore.instance.volume.observe(this, Observer { newValue ->
            //if ( PlayerStore.instance.isBound.value!!)
            actionOnService(Actions.VOLUME, newValue)
        })
    }

    // ####################################
    // ####### SERVICE PLAY / PAUSE #######
    // ####################################


    private fun actionOnService(a: Actions, v: Int = 0)
    {
            val i = Intent(this, RadioService::class.java)
            i.putExtra("action", a.name)
            i.putExtra("value", v)
            Log.d("MainActivity", "Sending intent ${a.name}")
            startService(i)
    }

    private val activityTag = "=====MainActivity======"


    // ####################################
    // ###### SERVICE BINDER MANAGER ######
    // ####################################

    // NO BINDERS, only intents. That's the magic.
    // Avoid code duplication, keep a single entry point to modify the service, and manage the service independently
    // (no coupling between service and activity, as it should be ! Cause the notification makes changes too.)
}
