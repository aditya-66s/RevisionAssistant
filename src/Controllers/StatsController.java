package Controllers;

import Models.Session;
import Models.SessionRecord;
import Models.UserStats;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class StatsController extends Controller{
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Path csvPath = Paths.get(System.getProperty("user.dir"), "files", "stats.csv");

    // Read stats.csv into an ArrayList of UserStats objects
    private ArrayList<UserStats> convertCSVtoArrayList() throws IOException {
        // Ensure folders and files exist before reading
        ensureCSVExists(csvPath);
        ArrayList<UserStats> statsList = new ArrayList<>();
        // Read CSV line by line
        try(BufferedReader br = new  BufferedReader(new FileReader(csvPath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] stats = line.split(",");
                if(stats.length < 11) throw new IllegalArgumentException("Corrupted Row: " + line);
                String email = stats[0];
                int totalXP = Integer.parseInt(stats[1]);
                int totalSessions = Integer.parseInt(stats[2]);
                int totalCorrect = Integer.parseInt(stats[3]);
                int totalIncorrect = Integer.parseInt(stats[4]);
                long totalStudyTime = Long.parseLong((stats[5]));
                int currentStreak = Integer.parseInt(stats[6]);
                int longestStreak = Integer.parseInt(stats[7]);
                String lastStudyDate = stats[8];
                int consecutiveCorrect = Integer.parseInt(stats[9]);
                int totalRetries = Integer.parseInt(stats[10]);
                ArrayList<String> achievements = new ArrayList<>();
                // After this achievements are listed
                if(stats.length > 11) {
                    achievements = new ArrayList<>(
                            Arrays.asList(Arrays.copyOfRange(stats, 11, stats.length))
                    );
                }
                UserStats s = new UserStats(email, totalXP, totalSessions, totalCorrect,
                        totalIncorrect, totalStudyTime, currentStreak, longestStreak,
                        lastStudyDate, consecutiveCorrect, totalRetries, achievements);
                statsList.add(s);
            }
        }catch(Exception e) {
            quarantineCorruptedCSV(e, csvPath);
            return new ArrayList<>();
        }
        return statsList;
    }
    // Rewrite stats.csv with updated data
    private void rewriteCSV(ArrayList<UserStats> statsList) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath.toFile()))) {
            for(UserStats s : statsList) {
                bw.write(toCSVRow(s));
                bw.newLine();
            }
        }
    }
    // Convert user stats to a csv row for writing
    private String toCSVRow(UserStats s){
        StringBuilder line = new StringBuilder(s.getEmail() + "," + s.getTotalXP() + "," + s.getTotalSessions() + ","
                + s.getTotalCorrect() + "," + s.getTotalIncorrect() + ","
                + s.getTotalStudyTime() + "," + s.getCurrentStreak() + ","
                + s.getLongestStreak() + "," + s.getLastStudyDate() + "," + s.getConsecutiveCorrect()
                + "," + s.getTotalRetries());
        for(String achievement : s.getUnlockedAchievements()){
            line.append(",").append(achievement);
        }
        return line.toString();
    }

    // Finds the user stats for a given email, if not find return default
    public UserStats loadUserStats(String email) throws IOException {
        ArrayList<UserStats> statsList = convertCSVtoArrayList();
        for(UserStats s : statsList) {
            if(s.getEmail().equals(email)) return s;
        }
        // If not found
        return new UserStats(email);
    }
    // is user stats saved?
    public boolean isUserStatsSaved(String email) throws IOException {
        ArrayList<UserStats> statsList = convertCSVtoArrayList();
        for(UserStats s : statsList) {
            if(s.getEmail().equals(email)) return true;
        }
        return false;
    }
    // Save updated stats for a user
    public void saveUserStats(UserStats updated) throws IOException {
        ArrayList<UserStats> statsList = convertCSVtoArrayList();
        boolean found = false;
        for(UserStats s : statsList) {
            if(s.getEmail().equals(updated.getEmail())){
                // Replace with updated stats
                statsList.set(statsList.indexOf(s), updated);
                found = true;
                break;
            }
        }
        // If not found, add to list
        if(!found) statsList.add(updated);
        rewriteCSV(statsList);
    }
    // Create default stats for user on registration
    public void createDefaultUserStats(String email) throws IOException {
        saveUserStats(new UserStats(email));
    }
    // Update all stats after a session and returns the updated stats
    public UserStats updateStats(String email, Session session,
                                 int xpGained, boolean isRetry) throws IOException {
        UserStats stats = loadUserStats(email);
        // Add XP earned
        stats.addXP(xpGained);
        // Increment session count
        stats.addSession();
        // Add correct and incorrect counts from the session
        stats.addCorrect(session.getCorrectCount());
        stats.addIncorrect(session.getIncorrectCount());
        // Add elapsed time
        stats.addStudyTime(session.getElapsedTime());
        // If retried, increment the counter
        if(isRetry) stats.addRetry();
        // Update the consecutive streak
        if(stats.getConsecutiveCorrect() < session.getConsecutiveCorrect())
            stats.setConsecutiveCorrect(session.getConsecutiveCorrect());
        // Update streak
        updateDayStreak(stats);
        // Update achievements
        AchievementController achC = new AchievementController();
        ArrayList<String> updatedAchievements = achC.checkForAchievements(stats, session, isRetry);
        saveUserStats(stats);
        return stats;
    }
    // Update the day streak
    private void updateDayStreak(UserStats stats){
        String today = LocalDate.now().format(dtf);
        String last = stats.getLastStudyDate();
        if(last == null || last.isBlank()){
            stats.setCurrentStreak(1);
        }else if (!last.equals(today)) {
            // Consecutive days
            LocalDate lastDate = LocalDate.parse(last, dtf);
            if(lastDate.plusDays(1).equals(LocalDate.now())){
                stats.setCurrentStreak(stats.getCurrentStreak() + 1);
            }else{
                // New streak
                stats.setCurrentStreak(1);
            }
        } else {
            // no change
        }
        if(stats.getCurrentStreak() > stats.getLongestStreak()){
            stats.setLongestStreak(stats.getCurrentStreak());
        }
        // Update the day
        stats.setLastStudyDate(today);
    }

    // Function to return the path where individual Session data is stored for user analytics
    private Path sessionPath(String email){
        return Paths.get(System.getProperty("user.dir"), "files", "sessions", email + ".csv");
    }

    // Saving a session to the user's session history CSV
    public void saveSessionRecord(String email, SessionRecord sr) throws IOException {
        Path sessionPath = sessionPath(email);
        // Create directory and folder if not there already
        Files.createDirectories(sessionPath.getParent());
        if(!Files.exists(sessionPath)) Files.createFile(sessionPath);
        // Join the answer history as Ts and Fs to represent true and false
        StringBuilder answers = new StringBuilder();
        for(boolean b : sr.getAnswerHistory()){
            answers.append(b ? "T" : "F");
        }
        // Write it into the csv
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(sessionPath.toFile(), true))) {
            bw.write(sr.getDate() + "," +
                    sr.getAccuracy() + "," +
                    sr.getStudySeconds() + "," +
                    sr.getCorrect() + "," +
                    sr.getIncorrect() + "," +
                    sr.getDeckName() + "," +
                    answers
            );
            bw.newLine();
        }
    }

    // Load all session records for a user from their session csv
    public ArrayList<SessionRecord> loadSessionRecords(String email) throws IOException {
        Path sessionPath = sessionPath(email);
        ArrayList<SessionRecord> historicalSessions = new ArrayList<>();
        if(!Files.exists(sessionPath)) return historicalSessions; // no sessions yet
        // Read data line by line
        try(BufferedReader br = new BufferedReader(new FileReader(sessionPath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // Not enough data in a row
                if(data.length < 7) throw new IllegalArgumentException("Corrupted Row: " + line);
                ArrayList<Boolean> answerHistory = new ArrayList<>();
                // Reconvert Ts and Fs into trues and falses
                for(char c : data[6].toCharArray()){
                    answerHistory.add(c == 'T');
                }
                // Add new SessionRecord object to ArrayList
                historicalSessions.add(new SessionRecord(data[0], Double.parseDouble(data[1]),
                        Long.parseLong(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]),
                        data[5], answerHistory));
            }
        } catch (Exception e) {
            quarantineCorruptedCSV(e, sessionPath);
            return new ArrayList<>();
        }
        return historicalSessions;
    }

}
