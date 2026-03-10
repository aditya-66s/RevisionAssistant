package RecordClasses;

import java.util.ArrayList;

/**
 * @param date Date of Session to be used for streaks
 * @param accuracy The accuracy, used for graph data
 * @param studySeconds Elapsed time of a session
 * @param correct  Number of correct answers
 * @param incorrect Number of incorrect answers
 * @param deckName The deck name of the session
 * @param answerHistory correct = t, incorrect = f
 */
public record SessionRecord(String date, double accuracy, long studySeconds, int correct, int incorrect,
                            String deckName, ArrayList<Boolean> answerHistory) {
}
