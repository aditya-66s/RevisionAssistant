package Controllers;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import marytts.LocalMaryInterface;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.io.File;
import java.io.IOException;

public class TTSController {
    // Entry point for synthesis
    private LocalMaryInterface maryTTS;
    // To play synthesised audio
    private MediaPlayer audioPlayer;
    private SettingsController sc;
    // Default values
    private String voice = "dfki-prudence-hsmm";
    private double ttsVolume;
    // Constructor
    public TTSController(SettingsController sc) throws IOException {
        this.sc = sc;
        this.ttsVolume = sc.getTTSVolume();
        try{
            // Initialise MaryTTS
            maryTTS = new LocalMaryInterface();
            // Set default voice
            maryTTS.setVoice(voice);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    // Synthesises and plays the given text
    public void speak(String text){
        // Stop any audio playing first
        stop();
        try{
            // generate audio from text
            AudioInputStream audio = maryTTS.generateAudio(text);
            File tempfile = File.createTempFile("tts", ".wav");
            AudioSystem.write(audio, AudioFileFormat.Type.WAVE, tempfile);
            tempfile.deleteOnExit();

            Media media = new Media(tempfile.toURI().toString());
            audioPlayer = new MediaPlayer(media);
            audioPlayer.setVolume(ttsVolume);

            audioPlayer.play();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    // Stop the audio
    public void stop(){
        if(audioPlayer != null){
            audioPlayer.stop();
            audioPlayer = null;
        }
    }
}
