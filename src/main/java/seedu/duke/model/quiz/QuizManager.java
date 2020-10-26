package seedu.duke.model.quiz;

import seedu.duke.common.LogManager;
import seedu.duke.common.Messages;
import seedu.duke.exception.EmptyParameterException;
import seedu.duke.exception.MissingParameterException;
import seedu.duke.exception.QuizParamException;
import seedu.duke.model.ModelManager;
import seedu.duke.ui.UserInterface;
import seedu.duke.model.quiz.UserAnswerManager;
import seedu.duke.model.quiz.Quiz;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class QuizManager extends ModelManager implements QuizInteractable {
    public static final int EMPTY_SIZE = 0;
    public static final int USER_INPUT_OFFSET = 9;
    private final ArrayList<Quiz> quizzes;
    private ArrayList<Quiz> lastIncorrectQuizzes = new ArrayList<>();
    private ArrayList<Integer> lastIncorrectAnswers = new ArrayList<>();
    private static final Logger logger = LogManager.getLogManagerInstance().getLogger();
    public static int noOfQues;
    private final UserInterface userInterface;
    public static String correctnessLogo;
    public static ArrayList<Integer> quizIndexes = new ArrayList<Integer>();

    public QuizManager(ArrayList<Quiz> quizzes) {
        this.quizzes = quizzes;
        this.userInterface = UserInterface.getInstance();
    }

    public ArrayList<Quiz> getQuizList() {
        return quizzes;
    }

    UserAnswerManager userAnswerManager = new UserAnswerManager();

    public int getQuizListSize() {
        assert quizzes != null;
        return quizzes.size();
    }

    //@@author elizabethcwt
    public void takeQuiz(String[] separatedInputs) {
        try {
            noOfQues = Integer.parseInt(separatedInputs[1]);
            assert noOfQues != 0 : "noOfQues should not be 0";

            if (!((noOfQues == 10) || (noOfQues == 20) || (noOfQues == 30))) {

                // Assert that noOfQues is NOT one of the acceptable values
                assert (!((noOfQues == 10) || (noOfQues == 20) || (noOfQues == 30)));

                // If user inputs an invalid number of quiz questions (not 10, 20 or 30)
                userInterface.showToUser(Messages.MESSAGE_INVALID_NUM_OF_QUIZ_QUESTIONS);

                // If user inputs a valid number of quiz questions (either 10, 20 or 30)
            } else if ((noOfQues > getQuizListSize()) && (getQuizListSize() < 10)) {

                // Assert that noOfQues is more than the quiz size, and quiz size is less than 10
                assert ((noOfQues > getQuizListSize()) && (getQuizListSize() < 10));

                // If user wants to try more questions than he/she has in the current quiz, and has less than 10
                // questions
                userInterface.showToUser(String.format(Messages.MESSAGE_INSUFFICIENT_QUES_LESS_THAN_10,
                        getQuizListSize()));
            } else if (noOfQues > getQuizListSize() && (getQuizListSize() >= 10)) {

                // Assert that noOfQues is more than the quiz size, and quiz size is at least 10
                assert ((noOfQues > getQuizListSize()) && (getQuizListSize() >= 10));

                // If user wants to try more questions than he/she has in the current quiz, but has at least 10
                // questions
                userInterface.showToUser(String.format(Messages.MESSAGE_INSUFFICIENT_QUES_MORE_THAN_10,
                        getQuizListSize()));
            } else {

                // If user enters a valid number of questions
                if (noOfQues <= getQuizListSize()) {

                    // Assert that noOfQues is valid
                    assert (noOfQues <= getQuizListSize()) : "noOfQues is invalid";

                    // Create a new list of the question indexes
                    quizIndexes = new ArrayList<>();
                    for (int i = 0; i < quizzes.size(); i++) {
                        quizIndexes.add(i);
                    }

                    // Shuffle the question indexes
                    Collections.shuffle(quizIndexes);

                    //TODO handle input: "quiz abc"

                    int questionCounter = 0;
                    while (questionCounter < noOfQues) {

                        // Assert that questionCounter is less than noOfQues
                        assert (questionCounter < noOfQues) : "questionCounter should not be more than noOfQues";

                        questionCounter = testForValidInput(questionCounter);
                    }

                    // Initialising counter for correctly answered questions
                    int correctCounter = 0;

                    // Assert that correctCounter is 0 initially
                    assert (correctCounter == 0) : "questionCounter should be 0 initially";

                    // Clear arraylist to store incorrect quizzes
                    lastIncorrectQuizzes.clear();
                    lastIncorrectAnswers.clear();

                    // Compare and note if students' answers are correct
                    for (int k = 0; k < noOfQues; k++) {
                        if (userAnswerManager.getUserAnswers().get(k).equals(Integer.parseInt(quizzes.get(quizIndexes
                                .get(k)).getAnswer()))) {
                            userAnswerManager.getCorrectness().add(true);
                            correctCounter++;
                        } else {
                            userAnswerManager.getCorrectness().add(false);
                            lastIncorrectQuizzes.add(quizzes.get(quizIndexes.get(k)));
                            lastIncorrectAnswers.add(userAnswerManager.getUserAnswers().get(k));
                        }
                    }

                    for (int l = 0; l < noOfQues; l++) {

                        // Assigning the correctness logo to be printed with questions post-quiz
                        if (userAnswerManager.getCorrectness().get(l).equals(true)) {

                            // Assert that the correctness of the user's input is true in this if loop
                            assert (userAnswerManager.getCorrectness().get(l).equals(true)) : "User's answer should be"
                                    + " correct for this question";

                            correctnessLogo = " [CORRECT ☺︎]";
                        } else {

                            // Assert that the correctness of the user's input is false in this else loop
                            assert (userAnswerManager.getCorrectness().get(l).equals(false)) : "User's answer should be"
                                    + " incorrect for this question";

                            correctnessLogo = " [WRONG ☹︎]";
                        }

                        // Print out all quiz questions, user's answers, correctness, correct answers and explanations
                        System.out.println("Question " + (l + 1) + ": ");
                        System.out.println(quizzes.get(quizIndexes.get(l)).printPostQuizQuestion(userAnswerManager
                                .getUserAnswers().get(l), correctnessLogo));
                    }

                    // Print out quiz score
                    System.out.println("You scored " + correctCounter + " out of " + noOfQues + "! "
                            + "Scroll up to review your quiz.\n");

                    // Empty userAnswers ArrayList and correctness ArrayList
                    userAnswerManager.getUserAnswers().clear();
                    userAnswerManager.getCorrectness().clear();

                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            userInterface.showToUser(Messages.MESSAGE_INVALID_HELP_COMMAND);
        }
    }

    //@@author elizabethcwt
    public int testForValidInput(int questionCounter) {
        // Print out each question
        System.out.println();
        System.out.println("Question " + (questionCounter + 1) + ": ");
        System.out.print(quizzes.get(quizIndexes.get(questionCounter)).printQuizQuestion());

        // Create a Scanner object
        Scanner in = new Scanner(System.in);
        String userInput = in.nextLine();
        userInput.replaceAll("\\s+", "");

        if (userInput.equals("")) {

            // Assert that the user's input is ""
            assert (userInput.equals(""));

            System.out.println("OOPS! Please enter your answer for the question above!\n");
        } else {

            boolean b = userInput.equals("1") || userInput.equals("2") || userInput.equals("3")
                    || userInput.equals("4");

            if (b) {

                // Assert that the user's input is one of the valid options
                assert (b) : "User's input should be one of the valid options (1, 2, 3 or 4)";

                // Store user's quiz answers into ArrayList
                userAnswerManager.getUserAnswers().add(Integer.parseInt(userInput));

                questionCounter++;
                return questionCounter;
            } else {
                System.out.println("OOPS! Incorrect answer format! Your answer can only be either 1, 2, 3 or 4!\n");
            }
        }
        return questionCounter;
    }

    //@@author untitle4
    @Override
    public void delete(String[] userInputs) throws IndexOutOfBoundsException {
        int quizIndex;

        try {
            quizIndex = Integer.parseInt(userInputs[2]);
        } catch (NumberFormatException e) {
            userInterface.showToUser(Messages.MESSAGE_QUIZ_DELETE_ERROR_NON_NUMBER);
            return;
        } catch (IndexOutOfBoundsException e) {
            userInterface.showToUser(Messages.MESSAGE_QUIZ_DELETE_ERROR_NON_NUMBER);
            return;
        }

        if ((quizIndex <= 0) || (quizIndex > getQuizListSize())) {
            throw new IndexOutOfBoundsException();
        }

        userInterface.showToUser("Noted. I've removed this quiz question: \n" + quizzes.get(quizIndex - 1));

        quizzes.remove(quizIndex - 1);
        getQuizStatement();
    }

    // format: add quiz /q question /o1 option 1 /o2 option 2 /o3 option 3 /o4 option 4 /a answer /exp explanation
    @Override
    public void add(String userInput) throws QuizParamException, EmptyParameterException {
        if (!userInput.contains(" /q ")) {
            userInterface.showToUser("question not found");
            throw new QuizParamException();
        }
        if (!userInput.contains(" /a ")) {
            userInterface.showToUser("answer not found");
            throw new QuizParamException();
        }
        if (!userInput.contains(" /o1 ") && !userInput.contains(" /o2 ")
                && !userInput.contains(" /o3 ") && !userInput.contains(" /o4 ")) {
            userInterface.showToUser("options not provided");
            throw new QuizParamException();
        }
        String[] separatedInputs = userInput.trim().split("/");

        String question = separatedInputs[1].substring(1).trim();
        String option1 = separatedInputs[2].substring(2).trim();
        String option2 = separatedInputs[3].substring(2).trim();
        String option3 = separatedInputs[4].substring(2).trim();
        String option4 = separatedInputs[5].substring(2).trim();
        String answer = separatedInputs[6].substring(1).trim();

        if (question.equals(" ") || option1.equals(" ") || option2.equals(" ")
                || option3.equals(" ") || option4.equals(" ") || answer.equals(" ")) {
            logger.log(Level.WARNING, "question or options or answer is empty");
            throw new EmptyParameterException();
        }

        if (separatedInputs.length > 7) {
            String explanation = separatedInputs[7].substring(3).trim();
            quizzes.add(new Quiz(question, option1, option2, option3, option4, answer, explanation));
        } else {
            quizzes.add(new Quiz(question, option1, option2, option3, option4, answer));
        }

        userInterface.showToUser("Quiz question added!\n");
    }

    //@@author untitle4
    @Override
    public void find(String userInput) throws MissingParameterException {
        String param = userInput.substring(USER_INPUT_OFFSET).trim();

        if (param.length() == EMPTY_SIZE) {
            throw new MissingParameterException();
        }

        FindQuiz findQuiz = new FindQuiz(param, quizzes);
        ArrayList<String> filteredQuizzes = findQuiz.filterQuizzes();

        if (filteredQuizzes.size() == EMPTY_SIZE) {
            userInterface.showToUser(Messages.MESSAGE_NO_QUIZZES_FOUND);
            return;
        }

        userInterface.printArray(filteredQuizzes);
    }

    public void recordedQuizzes() {
        if (lastIncorrectQuizzes.size() == 0) {
            userInterface.showToUser("Congratulations! You get full marks in your last attempt!");
        } else {
            userInterface.showToUser("Here are the incorrect quizzes in your last quiz attempt:\n");
            for (int i = 0; i < lastIncorrectQuizzes.size(); i++) {
                userInterface.showToUser(lastIncorrectQuizzes.get(i).printQuizQuestion());
                userInterface.showToUser("Your answer: (" + lastIncorrectAnswers.get(i) + ")");
                userInterface.showToUser("Correct answer: (" + lastIncorrectQuizzes.get(i).getAnswer() + ")\n");
                if (!lastIncorrectQuizzes.get(i).getExplanation().equals("")) {
                    userInterface.showToUser("Explanation: " + lastIncorrectQuizzes.get(i).getExplanation() + "\n");
                }
            }
        }

    }

    @Override
    public void list() {
        if (quizzes.size() == EMPTY_SIZE) {
            userInterface.showToUser("Quiz list is empty. Add some!\n");
        } else {
            for (int i = 0; i < quizzes.size(); i++) {
                userInterface.showToUser("Question " + (i + 1) + ":\n" + quizzes.get(i));
            }
        }
    }

    private void getQuizStatement() {
        String quizStatement = getQuizListSize() == 1 ? " quiz" : " quizzes";
        userInterface.showToUser("Now you have " + getQuizListSize() + quizStatement + " in the quiz list.");
    }
}
