package io.r_a_d.radio2.ui.nowplaying

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.SavedStateViewModelFactory
import io.r_a_d.radio2.*


class NowPlayingFragment : Fragment() {

    /*
    companion object {
        fun newInstance() = NowPlayingFragment()
    }

     */

    private lateinit var nowPlayingViewModel: NowPlayingViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        nowPlayingViewModel = ViewModelProviders.of(this).get(NowPlayingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_nowplaying, container, false)

        // View bindings to the ViewModel
        val songTitleText: TextView = root.findViewById(R.id.text_song_title)
        val songArtistText: TextView = root.findViewById(R.id.text_song_artist)
        val seekBarVolume: SeekBar = root.findViewById(R.id.seek_bar_volume)
        val volumeText: TextView = root.findViewById(R.id.volume_text)
        val progressBar: ProgressBar = root.findViewById(R.id.progressBar)

        PlayerStore.instance.songTitle.observe(this, Observer {
            songTitleText.text = it
        })

        PlayerStore.instance.songArtist.observe(this, Observer {
            songArtistText.text = it
        })

        PlayerStore.instance.playbackState.observe(this, Observer {
            syncPlayPauseButtonImage(root)
        })

        PlayerStore.instance.volume.observe(this, Observer {
            volumeText.text = "$it%"
        })

        seekBarVolume.setOnSeekBarChangeListener(nowPlayingViewModel.seekBarChangeListener)
        seekBarVolume.progress = PlayerStore.instance.volume.value!!
        progressBar.max = 100
        progressBar.progress = 70

        syncPlayPauseButtonImage(root)

        // initialize the value for isPlaying when displaying the fragment
        PlayerStore.instance.isPlaying.value = PlayerStore.instance.playbackState.value == PlaybackStateCompat.STATE_PLAYING

        val button: ImageButton = root.findViewById(R.id.play_pause)
        button.setOnClickListener{
            PlayerStore.instance.isPlaying.value = PlayerStore.instance.playbackState.value == PlaybackStateCompat.STATE_STOPPED
        }

        return root
    }

    private fun syncPlayPauseButtonImage(v: View)
    {
        val img = v.findViewById<ImageButton>(R.id.play_pause)

        if (PlayerStore.instance.playbackState.value == PlaybackStateCompat.STATE_STOPPED) {
            img.setImageResource(R.drawable.exo_controls_play)
        } else {
            img.setImageResource(R.drawable.exo_controls_pause)
        }
    }



}
