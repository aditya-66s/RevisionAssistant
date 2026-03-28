package Controllers;

import Models.Settings;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import marytts.LocalMaryInterface;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;


public class TTSController extends Controller{
    // Entry point for synthesis
    private LocalMaryInterface maryTTS;
    // To play synthesised audio
    private MediaPlayer audioPlayer;
    private Settings userSettings;
    // Default values
    private String voice = "dfki-prudence-hsmm";
    private double ttsVolume;
    private double rate;
    // Constructor
    public TTSController(double ttsVolume){
        this.ttsVolume = ttsVolume;
        this.rate = 1.0;
        // Initialise the MaryTTS on a separate thread -> prevent slow start
        new Thread(() -> {
            try{
                // Initialise MaryTTS
                maryTTS = new LocalMaryInterface();
                // Set default voice
                maryTTS.setVoice(voice);
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }).start();
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
            audioPlayer.setRate(rate);
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
    // Getters and Setters
    public void setRate(double rate){
        this.rate = Math.min(2.0, Math.max(1.0, Math.round(rate * 10.0) / 10.0));
    }
    public double getRate(){
        return rate;
    }

    public void setTTSVolume(double ttsVolume) {
        this.ttsVolume = ttsVolume;
    }
}
