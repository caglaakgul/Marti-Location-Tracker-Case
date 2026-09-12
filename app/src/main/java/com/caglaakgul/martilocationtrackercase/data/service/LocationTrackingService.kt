package com.caglaakgul.martilocationtrackercase.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.caglaakgul.martilocationtrackercase.R
import com.caglaakgul.martilocationtrackercase.data.tracking.TrackingStateStore
import com.caglaakgul.martilocationtrackercase.domain.repository.LocationRepository
import com.caglaakgul.martilocationtrackercase.domain.usecase.AddRoutePointUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var addRoutePointUseCase: AddRoutePointUseCase

    @Inject
    lateinit var trackingStateStore: TrackingStateStore

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var locationJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START_TRACKING) {
            startTracking()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopTracking()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startTracking() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        trackingStateStore.setTracking(true)

        if (locationJob?.isActive == true) return

        trackingStateStore.clearLiveRoute()
        locationJob = serviceScope.launch {
            locationRepository
                .observeLocationUpdates()
                .catch {
                    trackingStateStore.setTracking(false)
                    stopSelf()
                }
                .collect { location ->
                    trackingStateStore.setCurrentLocation(location)
                    trackingStateStore.addLiveRouteLocation(location)
                    addRoutePointUseCase(location)
                }
        }
    }

    private fun stopTracking() {
        locationJob?.cancel()
        locationJob = null
        trackingStateStore.setTracking(false)
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(getString(R.string.tracking_notification_title))
        .setContentText(getString(R.string.tracking_notification_text))
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.tracking_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW)

        val notificationManager = getSystemService(NotificationManager::class.java)

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val ACTION_START_TRACKING = "com.caglaakgul.martilocationtrackercase.START_TRACKING"
        private const val CHANNEL_ID = "location_tracking_channel"
        private const val NOTIFICATION_ID = 1001

        fun createStartIntent(context: Context): Intent {
            return Intent(context, LocationTrackingService::class.java).apply {
                action = ACTION_START_TRACKING
            }
        }
    }
}