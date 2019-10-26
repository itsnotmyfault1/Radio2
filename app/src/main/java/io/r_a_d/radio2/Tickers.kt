package io.r_a_d.radio2

import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import java.util.*


class Tick  : TimerTask() {
    override fun run() {
        PlayerStore.instance.currentTime.postValue(PlayerStore.instance.currentTime.value!! + 500)
    }
}

class ApiFetchTick  : TimerTask() {
    private val apiFetchTickTag = "======apiTick======"
    override fun run() {
        if (PlayerStore.instance.playbackState.value == PlaybackStateCompat.STATE_STOPPED)
        {
            val apiUrl = "https://r-a-d.io/api"
            val mainApiData = ApiDataFetcher(apiUrl)
            mainApiData.fetch()
            Log.d(apiFetchTickTag, "mainApiData fetch")
        }
    }
}