package Controllers;
import Models.Session;

public class XPController {
    // XP rules:
    // 1 hour = 500xp
    // 90%+ accuracy = 100xp
    // Correct = 15xp, Incorrect = 5xp

    // Function which returns the xp from a session
    public int calculateXP(Session session){
        int xp = 0;
        // Per answer
        xp += session.getCorrectCount() * 15;
        xp += session.getIncorrectCount() * 5;
        // Time XP, 500 per hour
        xp += (int) Math.round(((session.getElapsedTime() / 3600.0) * 500));
        // Accuracy bonus
        if(session.getAccuracy() >= 90.0) xp += 100;
        return xp;
    }
}
