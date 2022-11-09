package com.androidaudiorecorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.solicity.user.BuildConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AndroidAudioRecorder {

    protected static final String EXTRA_FILE_PATH = "filePath";
    protected static final String EXTRA_COLOR = "color";
    protected static final String EXTRA_SOURCE = "source";
    protected static final String EXTRA_CHANNEL = "channel";
    protected static final String EXTRA_SAMPLE_RATE = "sampleRate";
    protected static final String EXTRA_AUTO_START = "autoStart";
    protected static final String EXTRA_KEEP_DISPLAY_ON = "keepDisplayOn";

    private Activity activity;
    private Fragment fragment;
    public static final int MEDIA_TYPE_AUDIO = 1;

    private String filePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
  //  private String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() + "/recorded_audio.wav";
   // private String filePath =getOutputMediaFileUri(MEDIA_TYPE_AUDIO);
    private static final String IMAGE_DIRECTORY_NAME = "Temp";

//    public Uri getOutputMediaFileUri(int type) {
////        return Uri.fromFile(getOutputMediaFile(type));
//
//        return FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
//    }
//
//
//    private File getOutputMediaFile(int type) {
//
//        // External sdcard location
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
//                IMAGE_DIRECTORY_NAME);
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_AUDIO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "recorded_audio" + timeStamp + ".wav");
//        } else {
//            return null;
//        }
//
//
//        return mediaFile;
//    }


    private AudioSource source = AudioSource.MIC;
    private AudioChannel channel = AudioChannel.STEREO;
    private AudioSampleRate sampleRate = AudioSampleRate.HZ_44100;
    private int color = Color.parseColor("#546E7A");
    private int requestCode = 0;
    private boolean autoStart = false;
    private boolean keepDisplayOn = false;

    private AndroidAudioRecorder(Activity activity) {
        this.activity = activity;
    }

    private AndroidAudioRecorder(Fragment fragment) {
        this.fragment = fragment;
    }

    public static AndroidAudioRecorder with(Activity activity) {
        return new AndroidAudioRecorder(activity);
    }

    public static AndroidAudioRecorder with(Fragment fragment) {
        return new AndroidAudioRecorder(fragment);
    }

    public AndroidAudioRecorder setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public AndroidAudioRecorder setColor(int color) {
        this.color = color;
        return this;
    }

    public AndroidAudioRecorder setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public AndroidAudioRecorder setSource(AudioSource source) {
        this.source = source;
        return this;
    }

    public AndroidAudioRecorder setChannel(AudioChannel channel) {
        this.channel = channel;
        return this;
    }

    public AndroidAudioRecorder setSampleRate(AudioSampleRate sampleRate) {
        this.sampleRate = sampleRate;
        return this;
    }

    public AndroidAudioRecorder setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
        return this;
    }

    public AndroidAudioRecorder setKeepDisplayOn(boolean keepDisplayOn) {
        this.keepDisplayOn = keepDisplayOn;
        return this;
    }

    public void record() {
        Intent intent = new Intent(activity,AudioRecorderActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        intent.putExtra(EXTRA_COLOR, color);
        intent.putExtra(EXTRA_SOURCE, source);
        intent.putExtra(EXTRA_CHANNEL, channel);
        intent.putExtra(EXTRA_SAMPLE_RATE, sampleRate);
        intent.putExtra(EXTRA_AUTO_START, autoStart);
        intent.putExtra(EXTRA_KEEP_DISPLAY_ON, keepDisplayOn);
        activity.startActivityForResult(intent, requestCode);
    }

    public void recordFromFragment() {
        Intent intent = new Intent(fragment.getActivity(),AudioRecorderActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        intent.putExtra(EXTRA_COLOR, color);
        intent.putExtra(EXTRA_SOURCE, source);
        intent.putExtra(EXTRA_CHANNEL, channel);
        intent.putExtra(EXTRA_SAMPLE_RATE, sampleRate);
        intent.putExtra(EXTRA_AUTO_START, autoStart);
        intent.putExtra(EXTRA_KEEP_DISPLAY_ON, keepDisplayOn);
        fragment.startActivityForResult(intent, requestCode);
    }




}
