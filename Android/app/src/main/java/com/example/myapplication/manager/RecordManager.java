package com.example.myapplication.manager;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

public class RecordManager extends Fragment {

    PullableSource.Default mic;
    Recorder recorder;
    String audioFileName;
    FragmentActivity fragmentActivity;
    AudioRecordConfig.Default audioRecordConfig;
    public RecordManager(FragmentActivity fragmentActivity){
        this.fragmentActivity = fragmentActivity;

        audioRecordConfig = new AudioRecordConfig.Default(
                MediaRecorder.AudioSource.MIC,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO,
                44100/2
        );


    }

    public void startRecord() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        audioFileName = fragmentActivity.getExternalFilesDir("/").getAbsolutePath()+"/" + timeStamp+".wav";
        Log.d("audioFileName", "startRecord: audioFileName "+ audioFileName);

        mic = new PullableSource.Default(audioRecordConfig);

        File file = new File(audioFileName);
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic),
                file
        );

        recorder.startRecording();
        Log.d("record", "startRecord: 녹음 시작");
    }

    public void stopRecord() throws IOException{
        recorder.stopRecording();
        Log.d("Record", "stopRecord: stop.");
    }

    public String getAudioFileName() {
        return audioFileName;
    }
}
