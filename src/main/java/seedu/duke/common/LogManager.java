package seedu.duke.common;

import seedu.duke.ui.UserInterface;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogManager {
    private static LogManager logManager = null;
    private static final Logger LOGGER = Logger.getLogger("log");

    private LogManager() {
        UserInterface userInterface = UserInterface.getInstance();

        try {
            FileHandler fileHandler = new FileHandler("./logfile.txt");
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            userInterface.showToUser(Messages.MESSAGE_INITIALIZATION_LOGGER_FAILED);
        }
    }

    public static LogManager getLoggerInstance() {
        if (logManager == null) {
            logManager = new LogManager();
        }
        return logManager;
    }

    public Logger getLogger() {
        return LOGGER;
    }
}