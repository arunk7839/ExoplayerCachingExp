package com.c1ctech.exoplayercachingexp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util


class MainActivity : AppCompatActivity() {

    private lateinit var httpDataSourceFactory: HttpDataSource.Factory
    private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
    private lateinit var cacheDataSourceFactory: DataSource.Factory
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private val simpleCache: SimpleCache = MyApp.simpleCache

    private var mPlayer: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView
    private val videoURL = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get PlayerView by its id
        playerView = findViewById(R.id.playerView)

    }

    private fun initPlayer() {

        httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        defaultDataSourceFactory = DefaultDataSourceFactory(
            applicationContext, httpDataSourceFactory
        )

        //A DataSource that reads and writes a Cache.
        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        // Create a player instance and set mediaSourceFactory.
        simpleExoPlayer = SimpleExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory)).build()

        val videoUri = Uri.parse(videoURL)
        val mediaItem = MediaItem.fromUri(videoUri)
        val mediaSource =
            ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)

        // Bind the player to the view.
        playerView.player = simpleExoPlayer

        //setting exoplayer when it is ready.
        simpleExoPlayer.playWhenReady = true

        //Seeks to a position specified in milliseconds in the specified window.
        simpleExoPlayer.seekTo(0, 0)

        //set repeat mode.
        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_OFF

        // Set the media source to be played.
        simpleExoPlayer.setMediaSource(mediaSource, true)

        // Prepare the player.
        simpleExoPlayer.prepare()
    }


    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || mPlayer == null) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (mPlayer == null) {
            return
        }
        //release player when done
        mPlayer!!.release()
        mPlayer = null
    }
}