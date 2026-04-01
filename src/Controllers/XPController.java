package Controllers;
import Models.Session;

public class XPController {
    // XP rules:
    // 1 hour = 500xp
    // 90%+ accuracy = 100xp
    // Correct = 15xp, Incorrect = 5xp
    // Constants
    private static final int correct = 15;
    private static final int incorrect = 5;
    private static final int hourlyRate = 500;
    private static final int bonus = 100;

    // Function which returns the xp from a session
    public int calculateXP(Session session){
        int xp = 0;
        // Per answer
        xp += session.getCorrectCount() * correct;
        xp += session.getIncorrectCount() * incorrect;
        // Time XP, 500 per hour
        xp += (int) Math.round(((session.getElapsedTime() / 3600.0) * hourlyRate));
        // Accuracy bonus
        if(session.getAccuracy() >= 90.0) xp += bonus;
        return xp;
    }


}
