package com.cry.mediaprojectiondemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.widget.Toast
import com.andforce.socket.SocketClient
import com.cry.screenop.coroutine.RecordViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class CastService: Service() {
    private var mpm: MediaProjectionManager? = null
    private var viewModel: RecordViewModel? = null

    private var socketClient: SocketClient = SocketClient("http://192.168.2.183:3001")
    companion object {
        const val NOTIFICATION_ID = 1
        // 启动方法
        fun startService(context: Context, data: Intent, code: Int) {
            val startIntent = Intent(context.applicationContext, CastService::class.java)
            startIntent.putExtra("data", data)
            startIntent.putExtra("code", code)
            context.applicationContext.startForegroundService(startIntent)
        }
    }

    private var job: Job? = null

    private val mainScope = MainScope()

    override fun onCreate() {
        super.onCreate()

        startForeground(NOTIFICATION_ID, createNotification())

        viewModel = RecordViewModel(mainScope)

        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }

        job =  mainScope.launch(handler) {
            viewModel?.capturedImage?.collect {
                it?.let { bitmap->
                    withContext(Dispatchers.IO) {
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
                        val byteArray = byteArrayOutputStream.toByteArray()

                        socketClient.send(byteArray)
                        runCatching {
                            byteArrayOutputStream.close()
                        }
                        if (bitmap.isRecycled.not()) {
                            bitmap.recycle()
                        }
                    }
                }
            }
        }

        mpm = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?

        socketClient.startConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        socketClient.release()
        job?.cancel()
    }
    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //return super.onStartCommand(intent, flags, startId)
        if (intent == null) {
            return START_NOT_STICKY
        }

        // 获取intent中的数据
        val data = intent.getParcelableExtra<Intent>("data")
        val code = intent.getIntExtra("code", 0)
        if (data == null || code == 0) {
            Toast.makeText(this, "data or code is null", Toast.LENGTH_SHORT).show()
            return START_NOT_STICKY
        }

        mpm?.getMediaProjection(code, data)?.let { mp ->
            viewModel?.startCaptureImages(this, mp, 0.35f)
        }

        return START_STICKY
    }
    private fun createNotification(): Notification {
        val builder: Notification.Builder = Notification.Builder(this,
            createNotificationChannel("my_service", "My Background Service"))
        builder.setContentTitle("Recording Screen")
            .setContentText("Recording in progress")
            .setSmallIcon(R.drawable.ic_launcher_background)
        return builder.build()
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}