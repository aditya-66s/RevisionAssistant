package Controllers;

import Models.Settings;
import Models.UserStats;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SettingsController extends Controller {
    private final Path csvPath = Paths.get(System.getProperty("user.dir"), "files", "settings.csv");

    // Write default settings into settings.csv on user registration
    public void createDefaultSettings(String email) throws IOException {
        ensureCSVExists(csvPath);
        if (!doesSettingsExist(email)) {
            Settings defaultSettings = new Settings();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath.toString(), true))) {
                bw.write(toCSVRow(email, defaultSettings));
                bw.newLine();
            }
        }
    }

    // Load settings for a user
    public Settings loadSettings(String email) throws IOException {
        // Method from Controller superclass
        ensureCSVExists(csvPath);
        ArrayList<Settings> settings = convertCSVtoArrayList();
        // Linear search for user
        for(Settings s : settings) {
            if(s.getEmail().equals(email)) {
                return s;
            }
        }
        // If not found
        Settings defaultSettings = new Settings();
        defaultSettings.setEmail(email);
        return defaultSettings;
    }

    // Save settings.csv for a user if changed
    public boolean saveSettings(Settings updatedSettings) throws IOException {
        ArrayList<Settings> settings = convertCSVtoArrayList();
        boolean found = false;
        for(Settings s : settings) {
            if(s.getEmail().equals(updatedSettings.getEmail())) {
                copySettings(updatedSettings, s);
                found = true;
                break;
            }
        }
        // if not found add to array list
        if(!found) {
            Settings s = updatedSettings.copy();
            settings.add(s);
        }
        rewriteCSV(settings);
        return true;
    }

    // Checks if a user has settings already
    private boolean doesSettingsExist(String email) throws IOException {
        ArrayList<Settings> settingsList = convertCSVtoArrayList();
        for (Settings settings : settingsList) {
            if (settings.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
    // Reads settings.csv and converts each row into a user object
    private ArrayList<Settings> convertCSVtoArrayList() throws IOException {
        // Ensure folders and CSV exist before reading
        ensureCSVExists(csvPath);
        ArrayList<Settings> settingsList = new ArrayList<>();
        // Read CSV line by line
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath.toString()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(",");
                // Validate row lengths to avoid ArrayIndexOutOfBoundsException, throw an exception
                if (temp.length != settingsCSVDataItems) {
                    throw new IllegalArgumentException("Corrupted Row: " + line);
                }
                // Create a settings object
                Settings settings = new Settings(temp[0], temp[1], Double.parseDouble(temp[2]), Double.parseDouble(temp[3]),
                        Double.parseDouble(temp[4]), Boolean.parseBoolean(temp[5]), Boolean.parseBoolean(temp[6]));
                // Add object to ArrayList
                settingsList.add(settings);
            }
        }catch (Exception e){
            quarantineCorruptedCSV(e, csvPath);
            // Return an empty arraylist, no settings saved
            return new ArrayList<>();
        }
        return settingsList;
    }

    // Will be used to update CSV when user makers changes
    private void rewriteCSV(ArrayList<Settings> settingsList) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath.toString()))) {
            for (Settings settings : settingsList) {
                bw.write(toCSVRow(settings.getEmail(), settings));
                bw.newLine();
            }
        }
    }
    // Procedure to update the email within the file when the user changes their email
    public void updateEmail(String oldEmail, String newEmail) throws IOException {
        // load all users
        ArrayList<Settings> settingsList = convertCSVtoArrayList();
        for(Settings settings : settingsList){
            if(settings.getEmail().equals(oldEmail)){
                // change the email
                settings.setEmail(newEmail);
                break;
            }
        }
        // rewrite the file
        rewriteCSV(settingsList);
    }

    // Convert the settings object into a CSV Row
    private String toCSVRow(String email, Settings s) {
        return email + "," +
                s.getTheme() + "," +
                s.getDarkness() + "," +
                s.getTTSVolume() + "," +
                s.getSFXVolume() + "," +
                s.isMuted() + "," +
                s.isTTSEnabled();
    }
    // Update a user's setting object
    private void copySettings(Settings oldSettings, Settings newSettings) {
        newSettings.setTheme(oldSettings.getTheme());
        newSettings.setDarkness(oldSettings.getDarkness());
        newSettings.setTTSVolume(oldSettings.getTTSVolume());
        newSettings.setSFXVolume(oldSettings.getSFXVolume());
        newSettings.setMuted(oldSettings.isMuted());
        newSettings.setTTSEnabled(oldSettings.isTTSEnabled());
    }
    // Getters
    public double getSFXVolume() throws IOException {
        if(getCurrentSettings() == null){
            setCurrentSettings(loadSettings(getCurrentUser().getEmail()));
        }
        return getCurrentSettings().getSFXVolume();
    }
    public double getTTSVolume() throws IOException {
        return getCurrentSettings().getTTSVolume();
    }
}
