package com.example.homeprotect_client.ui.home.stream


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.remote.Constant
import com.example.homeprotect_client.remote.RestApiService
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import kotlinx.coroutines.launch


class PlayerFragment : Fragment() {
    val apiService = RestApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var viewOfLayout = inflater.inflate(com.example.homeprotect_client.R.layout.fragment_player, container, false)
        val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)
        lifecycleScope.launch {

            val args = arguments
            Log.d("Bundle", args.toString())

            var streamKey=""
            if (args != null) {
                streamKey = args.get("key").toString()
                Log.d("PLAYER", args.get("key").toString())
            }
            val email = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.EMAIL_KEY,"")
            Log.d("email", email)
            val url = "rtmp://${Constant.DEFAULT_IP}:1935/live/$email?key=$streamKey"
            Log.d("url", url)
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
            val videoTrackSelectionFactory: TrackSelection.Factory =
                AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            val player: SimpleExoPlayer =
                ExoPlayerFactory.newSimpleInstance(activity?.baseContext, trackSelector)

            val playerView: PlayerView =
                viewOfLayout.findViewById(com.example.homeprotect_client.R.id.player)

            playerView.player = player

            val rtmpDataSourceFactory = RtmpDataSourceFactory()


            val videoSource: MediaSource = ExtractorMediaSource.Factory(rtmpDataSourceFactory)
                .createMediaSource(Uri.parse(url))

            player.prepare(videoSource)

            player.playWhenReady = true
        }
        return viewOfLayout
    }


}