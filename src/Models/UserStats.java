package Models;

import java.util.ArrayList;

public class UserStats {
    private String email;
    private int totalXP;
    private int totalSessions;
    private int totalCorrect;
    private int totalIncorrect;
    private long totalStudyTime;
    private int currentStreak;
    private int longestStreak;
    public String lastStudyDate;
    private int consecutiveCorrect;
    private int totalRetries;
    private ArrayList<String> unlockedAchievements;

    public UserStats(String email) {
        this.email = email;
        this.totalXP = 0;
        this.totalSessions = 0;
        this.totalCorrect = 0;
        this.totalIncorrect = 0;
        this.totalStudyTime = 0;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.lastStudyDate = "";
        this.consecutiveCorrect = 0;
        this.totalRetries = 0;
        this.unlockedAchievements = new ArrayList<>();
    }
    public UserStats(String email, int totalXP, int totalSessions, int totalCorrect,
                     int totalIncorrect, long totalStudyTime, int currentStreak,
                     int longestStreak, String lastStudyDate, int consecutiveCorrect,
                     int totalRetries, ArrayList<String> unlockedAchievements) {
        this.email = email;
        this.totalXP = totalXP;
        this.totalSessions = totalSessions;
        this.totalCorrect = totalCorrect;
        this.totalIncorrect = totalIncorrect;
        this.totalStudyTime = totalStudyTime;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastStudyDate = lastStudyDate;
        this.consecutiveCorrect = consecutiveCorrect;
        this.totalRetries = totalRetries;
        this.unlockedAchievements = unlockedAchievements;
    }
    // Helper Methods
    public void addXP(int xp) {
        this.totalXP += xp;
    }
    public void addSession(){
        this.totalSessions++;
    }
    public void addCorrect(int correct) {
        this.totalCorrect+=correct;
    }
    public void addIncorrect(int incorrect) {
        this.totalIncorrect+=incorrect;
    }
    public void addStudyTime(long st) {
        this.totalStudyTime += st;
    }
    public void addRetry(){
        this.totalRetries++;
    }
    public boolean isAchievementUnlocked(String achievement){
        return this.unlockedAchievements.contains(achievement);
    }
    public void addAchievement(String achievement){
        if(!isAchievementUnlocked(achievement)) this.unlockedAchievements.add(achievement);
    }
    // Get the user's current level
    public int getLevel(){
        // Level N needs N*100 XP
        int level = 1;
        while(true){
            // Sum to n formula
            int xpForNext = (level * (level+1) / 2) * 100;
            if(totalXP < xpForNext) break;
            level++;
        }
        return level;
    }
    public int getXPForNext(){
        int level = getLevel();
        return (level + 1) * 100;
    }
    // How much xp user has earned in their current level
    public int getXPIntoCurrentLevel(){
        int level = getLevel();
        int xp = 0;
        // Calculate total xp required to reach current level
        for(int i = 0; i < level; i++){
            xp += i*100;
        }
        // How much xp user has got in their current level
        return totalXP-xp;
    }




    // Getters
    public String getEmail() {
        return email;
    }
    public int getTotalXP() {
        return totalXP;
    }
    public int getTotalSessions() {
        return totalSessions;
    }
    public int getTotalCorrect() {
        return totalCorrect;
    }
    public int getTotalIncorrect() {
        return totalIncorrect;
    }
    public long getTotalStudyTime() {
        return totalStudyTime;
    }
    public int getLongestStreak() {
        return longestStreak;
    }
    public int getCurrentStreak() {
        return currentStreak;
    }
    public String getLastStudyDate() {return lastStudyDate;}
    public int getConsecutiveCorrect() {
        return consecutiveCorrect;
    }
    public int getTotalRetries() {
        return totalRetries;
    }
    public ArrayList<String> getUnlockedAchievements() {
        return unlockedAchievements;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }
    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }
    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }
    public void setTotalCorrect(int totalCorrect) {
        this.totalCorrect = totalCorrect;
    }
    public void setTotalIncorrect(int totalIncorrect) {
        this.totalIncorrect = totalIncorrect;
    }
    public void setTotalStudyTime(long totalStudyTime) {
        this.totalStudyTime = totalStudyTime;
    }
    public void setLastStudyDate(String lastDate) {this.lastStudyDate = lastDate;}
    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }
    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }
    public void setConsecutiveCorrect(int consecutiveCorrect) {
        this.consecutiveCorrect = consecutiveCorrect;
    }
    public void setTotalRetries(int totalRetries) {
        this.totalRetries = totalRetries;
    }
    public void setUnlockedAchievements(ArrayList<String> unlockedAchievements) {
        this.unlockedAchievements = unlockedAchievements;
    }

}
