package com.example.homeprotect_client.ui.home.stream

import android.graphics.ColorSpace
import android.graphics.Rect
import android.hardware.camera2.params.Face
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.homeprotect_client.R
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.EMAIL_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.IP_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.STREAM_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.TOKEN_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreConstants.UUID_KEY
import com.example.homeprotect_client.data.local.PreferenceDataStoreHelper
import com.example.homeprotect_client.remote.Constant
import com.example.homeprotect_client.remote.RestApiService
import com.example.homeprotect_client.remote.dto.Device
import com.pedro.encoder.input.video.Camera2ApiManager
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.encoder.utils.FaceDetectorUtil
import com.pedro.encoder.utils.FaceParsed
import com.pedro.rtmp.utils.ConnectCheckerRtmp
import com.pedro.rtplibrary.rtmp.RtmpCamera2
import com.pedro.rtplibrary.view.OpenGlView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class StreamFragment : Fragment() {

    private var streamJob: Job? = null
    var rtmpCamera2: RtmpCamera2? = null
    lateinit var surfaceView: OpenGlView
    var isStreaming = false


    val apiService = RestApiService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var viewOfLayout = inflater.inflate(R.layout.fragment_stream, container, false)
        surfaceView= viewOfLayout.findViewById(R.id.openGlView)
        var streamButton: Button =  viewOfLayout.findViewById(R.id.streamButton)
        surfaceView.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val connectChecker = initConnectChecker()
                rtmpCamera2 = RtmpCamera2(surfaceView,connectChecker)
                if(rtmpCamera2!!.prepareVideo()) {
                    rtmpCamera2!!.startPreview()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                Log.i("MY_LOG","UPDATED")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }

        })
        val apiService = RestApiService()
        val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)




           // Log.d("STREAMFRAGMENT",streamKey)
            /*if(streamKey==""||streamKey=="null") {
                apiService.updateStreamKey(ipAddress,token, Device(uuid,streamKey, Build.MODEL)) {
                    Log.d("STREAMFRAGMENT", it.toString())
                    if (it != null) {
                        streamKey = it.streamKey
                    }
                    lifecycleScope.launch {
                        preferenceDataStoreHelper.putPreference(STREAM_KEY, streamKey)
                    }
                }
            }

             */

        streamButton.setOnClickListener {
            lifecycleScope.launch {
                val streamKey = preferenceDataStoreHelper.getFirstPreference(STREAM_KEY, "")
                val email = preferenceDataStoreHelper.getFirstPreference(EMAIL_KEY,"")
                val token = preferenceDataStoreHelper.getFirstPreference(TOKEN_KEY,"")
                val uuid = preferenceDataStoreHelper.getFirstPreference(UUID_KEY,"")
                isStreaming = !isStreaming
                if (!isStreaming) {
                    rtmpCamera2?.stopStream()
                    streamJob?.cancel()
                    Toast.makeText(
                        activity?.applicationContext,
                        "STREAMING IS STOPPED",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        activity?.applicationContext,
                        "STREAMING IS STARTED",
                        Toast.LENGTH_LONG
                    ).show()
                    streamJob = GlobalScope.launch(Dispatchers.IO) {
                        startStream(streamKey, email)

                    }
                }
            }
        }

        return viewOfLayout
    }
    private fun startStream(streamKey: String, email: String) {

            val rtmpUrl = "rtmp://"+ Constant.DEFAULT_IP +":1935/live/$email?key=$streamKey"
        Log.d("STREAMFRAGMENT",rtmpUrl)
            if (rtmpCamera2!!.prepareVideo()) {
                if(rtmpCamera2!!.prepareAudio()) {
                    rtmpCamera2!!.startStream(rtmpUrl)
                    lifecycleScope.launch {
                        val preferenceDataStoreHelper = PreferenceDataStoreHelper(activity?.applicationContext)
                        val streamKey = preferenceDataStoreHelper.getFirstPreference(STREAM_KEY, "")
                        val email = preferenceDataStoreHelper.getFirstPreference(EMAIL_KEY,"")
                        val token = preferenceDataStoreHelper.getFirstPreference(TOKEN_KEY,"")
                        val uuid = preferenceDataStoreHelper.getFirstPreference(UUID_KEY,"")
                        rtmpCamera2!!.enableFaceDetection(object :
                            Camera2ApiManager.FaceDetectorCallback {
                            override fun onGetFaces(
                                faces: Array<out Face>?,
                                scaleSensor: Rect?,
                                sensorOrientation: Int
                            ) {


                                if(faces!=null) {
                                    for (face in faces) {

                                        val faceParsed: FaceParsed = FaceDetectorUtil.camera2Parse(
                                            face,
                                            scaleSensor,
                                            sensorOrientation,
                                            CameraHelper.getCameraOrientation(context),
                                            rtmpCamera2!!.cameraFacing)
                                        Log.d(
                                            "FACES",
                                            faceParsed.scale.x.toString() + " " + faceParsed.scale.y.toString()
                                        )
                                        Log.d(
                                            "FACES",
                                            faceParsed.position.x.toString() + " " + faceParsed.position.x.toString()
                                        )

                                        var warnings = ArrayList<String>()
                                        warnings.add("Face detected! "+ faceParsed.position.x.toString()+ " " + faceParsed.position.y.toString())
                                        val device: Device = Device(uuid = uuid,
                                            streamKey = streamKey,model = Build.MODEL, warnings = warnings)
                                        apiService.updateWarnings(Constant.DEFAULT_IP+":8080",token,device) {
                                            Log.d("UPDATE WARNING", "bbbb")
                                        }
                                    }
                                }
                            }

                        })
                    }
                }

            } else {
                Log.d("RTMP", "Encoders are not supported")
            }


        Log.d("STREAM BITMAP", surfaceView.holder.surface.isValid.toString())
        Log.d("STREAM BITMAP", surfaceView.holder.surface.toString())

    }

    private fun initConnectChecker(): ConnectCheckerRtmp {
        return object : ConnectCheckerRtmp {
            override fun onAuthErrorRtmp() {
                Log.d("RTMP", "Auth Error")
            }

            override fun onAuthSuccessRtmp() {
                Log.d("RTMP", "Auth success!")
            }

            override fun onConnectionFailedRtmp(reason: String) {
                if (rtmpCamera2?.reTry(5000, reason, null) == true) {
                    Log.d("RTMP", "Failed to connect. Retrying....")
                } else {
                    rtmpCamera2?.stopStream()
                }
            }

            override fun onConnectionStartedRtmp(rtmpUrl: String) {
                Log.d("RTMP", "Connection Started!")
            }

            override fun onConnectionSuccessRtmp() {
                Log.d("RTMP", "Connection Success!")
            }

            override fun onDisconnectRtmp() {
                Log.d("RTMP", "Disconnected")
            }

            override fun onNewBitrateRtmp(bitrate: Long) {
                //     bitrateAdapter.adaptBitrate(bitrate)

            }

        }
    }

}