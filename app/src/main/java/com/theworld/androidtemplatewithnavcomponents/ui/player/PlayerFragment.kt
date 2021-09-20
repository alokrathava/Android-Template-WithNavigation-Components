package com.theworld.androidtemplatewithnavcomponents.ui.player

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import  com.theworld.androidtemplatewithnavcomponents.R
import com.theworld.androidtemplatewithnavcomponents.databinding.FragmentPlayerBinding
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.decoder.DecoderReuseEvaluation
import com.hrsports.cricketstreaming.utils.hideSystemUI
import com.hrsports.cricketstreaming.utils.showSystemUI


@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.fragment_player), AnalyticsListener {


    companion object {

        private const val TAG = "PlayerFragment"
    }

    private var playbackPosition: Long = 0


//    private var videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"


    private var videoUrl =
        "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
//    private var videoUrl = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"


    private var isLive = false

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!


    private lateinit var simpleExoPlayer: SimpleExoPlayer

    private var isFullScreen = false


    private lateinit var isLiveTextView: TextView
    private lateinit var exoDuration: TextView
    private lateinit var fullScreenButton: ImageView
    private lateinit var settingsImageButton: ImageView

    private var trackDialog: Dialog? = null


    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(requireContext(), "exoplayer-sample")
    }

    private lateinit var trackSelector: DefaultTrackSelector


    /*----------------------------------------- On ViewCreated -------------------------------*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPlayerBinding.bind(view)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        init()
        clickListeners()


    }

    /*----------------------------------------- Init -------------------------------*/

    private fun init() {


        trackSelector = DefaultTrackSelector(requireContext())
        trackSelector.setParameters(
            trackSelector
                .buildUponParameters()
                .setMaxVideoSizeSd()
                .setPreferredAudioLanguage("en")
        )

        initializePlayer()

    }


    /*----------------------------------- Init Player ---------------------------*/

    private fun initializePlayer() {

        simpleExoPlayer = SimpleExoPlayer
            .Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()


        preparePlayer(videoUrl, getVideoUrl(videoUrl))

        binding.playerView.player = simpleExoPlayer
        simpleExoPlayer.seekTo(playbackPosition)
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.addListener(onPlaybackStateChanged)


        simpleExoPlayer.addAnalyticsListener(this)


    }

    /*----------------------------------- Click Listeners ---------------------------*/

    private fun clickListeners() {

        isLiveTextView = binding.playerView.findViewById(R.id.tvLive)
        exoDuration = binding.playerView.findViewById(R.id.exo_duration)

        isLiveTextView.isVisible = isLive
        exoDuration.isVisible = !isLive


        fullScreenButton = binding.playerView.findViewById(R.id.fullScreenImage)
        settingsImageButton = binding.playerView.findViewById(R.id.image_settings)
        fullScreenButton.setOnClickListener {
            handleFullScreen()
        }
        settingsImageButton.setOnClickListener {

//            handleTrackSelection()
            initPopupQuality()

        }

    }


    /*----------------------------------- Prepare Player ---------------------------*/


    private fun preparePlayer(videoUrl: String, type: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri, type)
        simpleExoPlayer.setMediaSource(mediaSource)
        simpleExoPlayer.prepare()
    }

    /*----------------------------------- Build Media Source ---------------------------*/

    private fun buildMediaSource(uri: Uri, type: String): MediaSource {
        return when (type) {
            "dash" -> {

                DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))
            }
            "hls" -> {

                val mediaItem = MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .setUri(uri)
                    .build()

                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)
            }
            else -> {

                val mediaItem = MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setUri(uri)
                    .build()

                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem)
            }
        }
    }

    /*----------------------------------- Handle Listeners ---------------------------*/

    private fun releasePlayer() {

        binding.playerView.player!!.release()
        binding.playerView.player = null

    }


    val onPlaybackStateChanged = object : Player.Listener { // player listener

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {


                Player.STATE_READY -> {
//                    aspectRatioFrameLayout.setAspectRatio(16f / 9f)
                    binding.loadingSpinner.isVisible = false
                }
                Player.STATE_ENDED -> {
                    binding.loadingSpinner.isVisible = false
                }
                Player.STATE_BUFFERING -> {
                    binding.loadingSpinner.isVisible = true
                }
                Player.STATE_IDLE -> {
                    //your logic
                }
                else -> {
                    binding.playerView.hideController()
                }
            }
        }
    }


    private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false
        }
        var cause: Throwable? = e.sourceException
        while (cause != null) {
            if (cause is BehindLiveWindowException) {
                return true
            }
            cause = cause.cause

            Log.d(TAG, "isBehindLiveWindow: $cause")
        }
        return false
    }

    override fun onVideoInputFormatChanged(
        eventTime: AnalyticsListener.EventTime,
        format: Format,
        decoderReuseEvaluation: DecoderReuseEvaluation?
    ) {
        super.onVideoInputFormatChanged(eventTime, format, decoderReuseEvaluation)

        Log.d(TAG, "onVideoInputFormatChanged: Bit-Rates :::::: ${format.bitrate}")
        Log.d(
            TAG,
            "onVideoInputFormatChanged: Dimension :::::: ${format.width}X${format.height} "
        )

        Log.d(
            TAG,
            "onVideoInputFormatChanged: Current Window Index ::::::  ${simpleExoPlayer.currentWindowIndex}"
        )

    }


    /*----------------------------------- Handle Track Selection -------------------------------------*/


    private fun handleTrackSelection() {

        val popupMenu = PopupMenu(requireContext(), settingsImageButton)

        val menu = popupMenu.menu
        menu.add(Menu.NONE, 0, 0, "Video Quality")
        popupMenu.setOnMenuItemClickListener {

            false
        }


        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        val parameters = trackSelector.parameters

        mappedTrackInfo?.let {

            for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {

                val trackType = mappedTrackInfo.getRendererType(rendererIndex)
                val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
                val isRendererDisabled = parameters.getRendererDisabled(rendererIndex)
                val selectionOverride = parameters.getSelectionOverride(
                    rendererIndex,
                    trackGroupArray
                )


                Log.d(TAG, "isRendererDisabled: $isRendererDisabled")
                Log.d(TAG, "selectionOverride: " + Gson().toJson(selectionOverride))
                Log.d(TAG, "--- Render Index ::::  $rendererIndex")
                Log.d(TAG, "track group array::: ${Gson().toJson(trackGroupArray)} ")
                Log.d(TAG, "track group array Size: ${trackGroupArray.length}")
                Log.d(TAG, "track TYpe Outer :::: ${trackType}")


                for (groupIndex in 0 until trackGroupArray.length) {
                    for (trackIndex in 0 until trackGroupArray[groupIndex].length) {


                        val trackName = DefaultTrackNameProvider(resources).getTrackName(
                            trackGroupArray[groupIndex].getFormat(trackIndex)
                        )
                        val isTrackSupported = mappedTrackInfo.getTrackSupport(
                            rendererIndex,
                            groupIndex,
                            trackIndex
                        ) == RendererCapabilities.FORMAT_HANDLED


                        Log.d(
                            TAG,
                            "track item $trackIndex: trackName: $trackName, Track Type: $trackType, isTrackSupported: $isTrackSupported"
                        )

                        menu.add(1, trackIndex + 1, trackIndex + 1, trackName)

                    }
                }


            }


            menu.setGroupCheckable(1, true, true)
            popupMenu.show()

        }


        /* val mappedTrackInfo = trackSelector.currentMappedTrackInfo
         if (mappedTrackInfo != null) {
             val rendererIndex = 2
             val rendererType = mappedTrackInfo.getRendererType(rendererIndex)
             val allowAdaptiveSelections = (rendererType == C.TRACK_TYPE_VIDEO
                     || (rendererType == C.TRACK_TYPE_AUDIO
                     && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                     == MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS))
             val (first, second) = TrackSelectionView.getDialog(
                 requireContext(),
                 "Track Selector",
                 trackSelector,
                 rendererIndex
             )
             second.setShowDisableOption(true)
             second.setAllowAdaptiveSelections(allowAdaptiveSelections)
             first.show()
         }*/


        /*
        *  trackSelector.setParameters(
                    trackSelector
                            .buildUponParameters()
                            .setMaxVideoSizeSd()
                            .setMaxVideoBitrate(quality)
                            .setMaxVideoSize(videoW, videoH)
            );
        * */

    }


    /*------------------------------------ Handle Quality Popup -----------------------------------------*/


    private fun initPopupQuality() {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        var videoRenderer: Int? = null

        if (mappedTrackInfo == null) return else settingsImageButton.visibility = View.VISIBLE

        for (i in 0 until mappedTrackInfo.rendererCount) {
            if (isVideoRenderer(mappedTrackInfo, i)) {
                videoRenderer = i
            }
        }

        if (videoRenderer == null) {
            settingsImageButton.visibility = View.GONE
            return
        }


        val trackSelectionDialogBuilder = TrackSelectionDialogBuilder(
            requireContext(),
            "Select Quality",
            trackSelector,
            videoRenderer
        )

        trackSelectionDialogBuilder.setTheme(R.style.TrackSelectionDialog)

        trackSelectionDialogBuilder.setTrackNameProvider {
            // Override function getTrackName
            "${it.width} x ${it.height}"
        }
        trackDialog = trackSelectionDialogBuilder.build()
        trackDialog?.show()
    }

    private fun isVideoRenderer(
        mappedTrackInfo: MappingTrackSelector.MappedTrackInfo,
        rendererIndex: Int
    ): Boolean {
        val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
        if (trackGroupArray.length == 0) {
            return false
        }
        val trackType = mappedTrackInfo.getRendererType(rendererIndex)
        return C.TRACK_TYPE_VIDEO == trackType
    }


    /*----------------------------------- Handle Full Screen Click -------------------------------------*/

    private fun handleFullScreen() {

        if (isFullScreen) {

            fullScreenButton.setImageResource(R.drawable.ic_fullscreen_enter)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
//            } else {
//                @Suppress("DEPRECATION")
//                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//            }

            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE


//            requireActivity().showSystemUI()


            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED


            val params = binding.playerView.layoutParams
            params.width = LinearLayout.LayoutParams.MATCH_PARENT
            params.height = 630
            binding.playerView.layoutParams = params


        } else {
            fullScreenButton.setImageResource(R.drawable.ic_fullscreen_exit)


            requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

//            requireActivity().hideSystemUI()


            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE


            val params = binding.playerView.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.playerView.layoutParams = params

        }

        isFullScreen = !isFullScreen
    }


    /*private fun hideSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }*/


    /*----------------------------------------- Back Press Callback -------------------------------*/

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            if (isFullScreen) {
                handleFullScreen()
                return
            }

            findNavController().navigateUp()
        }
    }


    /*----------------------------------- Lifecycle Handle -------------------------------------*/


    override fun onDestroyView() {
        super.onDestroyView()

//        releasePlayer()

        simpleExoPlayer.playWhenReady = !simpleExoPlayer.playWhenReady
        simpleExoPlayer.stop(true)
        simpleExoPlayer.seekTo(0)
    }


    /*------------------------------------- Get Video Url ----------------------------------------*/

    private fun getVideoUrl(url: String): String {

        val extension = url.substring(url.lastIndexOf("."))

        Log.d(TAG, "getVideoUrl: $extension")

//        (extension == ".mp4" || extension == ".mkv" || extension == ".avi") {
//
//        } else

        return when (extension) {
            ".m3u8" -> "hls"
            ".mpd" -> "dash"
            else -> "default"
        }
    }

}