package Models;

public class Settings {
    // to identify user
    private String email;
    // Stylesheet
    private String theme;
    // Accessibility
    private double fontSize;
    private double uiScale;
    private double darkness; // use a stack pane overlay rectangle and change opacity
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
        this.darkness = 0.0;
        // Audio
        this.ttsVolume = 0.8;
        this.sfxVolume = 0.6;
        this.muted = false;
        this.ttsEnabled = true;
    }
    public Settings(String email, String theme, double darkness, double ttsVolume, double sfxVolume, boolean muted, boolean ttsEnabled) {
        this.email = email;
        // Stylesheet default
        this.theme = theme;
        // Accessibility
        this.darkness = darkness;
        // Audio
        this.ttsVolume = ttsVolume;
        this.sfxVolume = sfxVolume;
        this.muted = muted;
        this.ttsEnabled = ttsEnabled;
    }
    // Making a copy of settings for drafts
    public Settings copy(){
        Settings s = new Settings();
        s.setEmail(this.email);
        s.setTheme(this.theme);
        s.setDarkness(this.darkness);
        s.setTTSVolume(this.ttsVolume);
        s.setSFXVolume(this.sfxVolume);
        s.setMuted(this.muted);
        s.setTTSEnabled(this.ttsEnabled);
        return s;
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
        if (theme == null || theme.isBlank()) {this.theme = "dark";}
        this.theme = theme;
    }

    // Accessibility
    public double getDarkness() { return darkness; }
    public void setDarkness(double darkness) {
        this.darkness = clamp(darkness, 0.0, 0.8);
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
    public void setTTSEnabled(boolean ttsEnabled) { this.ttsEnabled = ttsEnabled; }
}
