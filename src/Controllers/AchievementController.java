package Controllers;

import Models.Session;
import Models.UserStats;

import java.util.ArrayList;

public class AchievementController {

    // Return list of newly unlocked achievements after a session
    public ArrayList<String> checkForAchievements(UserStats stats, Session session, boolean isRetrySession){
        ArrayList<String> newAchievements = new ArrayList<>();
        // Correct streak achievements
        // 5 in a row
        tryUnlock("Sharp Mind", stats,
                stats.getConsecutiveCorrect() >= 5, newAchievements);
        // 10 in a row
        tryUnlock("Perfect Run", stats,
                stats.getConsecutiveCorrect() >= 10, newAchievements);
        // Accuracy
        tryUnlock("Flawless", stats,
                session.getAccuracy() == 100.0, newAchievements);
        // All time answer achievements
        // 10 flashcards reviewed
        tryUnlock("Getting Started", stats,
                stats.getTotalCorrect() + stats.getTotalIncorrect() >= 10, newAchievements);
        // 100 correct
        tryUnlock("Knowledge Expert", stats,
                stats.getTotalCorrect() >= 100, newAchievements);
        // 100 flashcards reviewed
        tryUnlock("100 Club", stats,
                stats.getTotalCorrect() + stats.getTotalIncorrect() >= 100, newAchievements);
        // Session count achievements
        // 5 sessions
        tryUnlock("Revision Rookie", stats,
                stats.getTotalSessions() >= 5, newAchievements);
        // 50 sessions
        tryUnlock("Revision Master", stats,
                stats.getTotalSessions() >= 50, newAchievements);
        // Streaks
        // 3 days in a row
        tryUnlock("Study Streak", stats,
                stats.getCurrentStreak() >= 3, newAchievements);
        // 7 days in a row
        tryUnlock("Dedicated Learner", stats,
                stats.getCurrentStreak() >= 7, newAchievements);
        // 30 days in revision total
        tryUnlock("Revision Veteran", stats,
                stats.getTotalStudyTime() >= 30*24*3600, newAchievements);
        // Retrying achievements
        // Retry a session
        tryUnlock("Keep Trying", stats,
                isRetrySession, newAchievements);
        // Retry 10 sessions
        tryUnlock("Never Give Up", stats,
                stats.getTotalRetries() >= 10, newAchievements);
        // If user has unlocked all achievements
        tryUnlock("Ultimate Scholar", stats,
                stats.getUnlockedAchievements().size() >= 13 , newAchievements);
        return newAchievements;
    }


    // Unlocks an achievement if the conditions have been met and not already unlocked
    private void tryUnlock(String achievement, UserStats stats,
                           boolean condition, ArrayList<String> unlockedAchievements) {
        if(condition && !stats.isAchievementUnlocked(achievement)) {
            stats.addAchievement(achievement);
            unlockedAchievements.add(achievement);
        }
    }
}
