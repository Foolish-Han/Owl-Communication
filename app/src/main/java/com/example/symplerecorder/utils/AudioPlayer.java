package com.example.symplerecorder.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlayer {
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioTrack audioTrack;

    public void init(){
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize, AudioTrack.MODE_STREAM);
        audioTrack.play();
    }
    public void play(byte[] audioData) {
        audioTrack.write(audioData, 0, audioData.length);
    }

    public void stop() {
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
    }
}
