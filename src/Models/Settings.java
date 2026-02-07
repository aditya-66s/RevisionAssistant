package Models;

public class Settings {
    // to identify user
    private String email;
    // Stylesheet
    private String theme;
    // Accessibility
    private double fontSize;
    private double uiScale;
    private double brightness; // use a stack pane overlay rectangle and change opacity
    // Audio
    private double ttsVolume;
    private double sfxVolume;
    private boolean muted;
    private boolean ttsEnabled;

    // Default settings written into settings.csv when user signs up
    public Settings() {
        // Stylesheet default
        this.theme = "dark";

        // Accessibility
        this.fontSize = 16;
        this.uiScale = 1.0;
        this.brightness = 0.0;

        // Audio
        this.ttsVolume = 0.8;
        this.sfxVolume = 0.6;
        this.muted = false;
        this.ttsEnabled = true;
    }
    public Settings(String email, String theme, double fontSize, double uiScale, double brightness, double ttsVolume, double sfxVolume, boolean muted, boolean ttsEnabled) {
        this.email = email;
        // Stylesheet default
        this.theme = theme;

        // Accessibility
        this.fontSize = fontSize;
        this.uiScale = uiScale;
        this.brightness = brightness;

        // Audio
        this.ttsVolume = ttsVolume;
        this.sfxVolume = sfxVolume;
        this.muted = muted;
        this.ttsEnabled = ttsEnabled;
    }


    // Will set the value to the minimum or maximum value if outside the range
    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    //  Stylesheet
    public String getTheme() { return theme; }
    public void setTheme(String theme) {
        if (theme == null || theme.isBlank()) return;
        this.theme = theme;
    }

    // Accessibility
    public double getFontSize() { return fontSize; }
    public void setFontSize(double fontSize) {
        this.fontSize = clamp(fontSize, 12, 28);
    }

    public double getUIScale() { return uiScale; }
    public void setUIScale(double uiScale) {
        this.uiScale = clamp(uiScale, 0.8, 1.5);
    }

    public double getBrightness() { return brightness; }
    public void setBrightness(double brightness) {
        this.brightness = clamp(brightness, 0.0, 0.6);
    }

    // Audio
    public double getTTSVolume() { return ttsVolume; }
    public void setTTSVolume(double ttsVolume) {
        this.ttsVolume = clamp(ttsVolume, 0.0, 1.0);
    }

    public double getSFXVolume() { return sfxVolume; }
    public void setSFXVolume(double sfxVolume) {
        this.sfxVolume = clamp(sfxVolume, 0.0, 1.0);
    }

    public boolean isMuted() { return muted; }
    public void setMuted(boolean muted) { this.muted = muted; }

    public boolean isTTSEnabled() { return ttsEnabled; }
    public void setTtsEnabled(boolean ttsEnabled) { this.ttsEnabled = ttsEnabled; }
}
