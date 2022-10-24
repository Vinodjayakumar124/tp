package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.event.Event;
import seedu.address.model.event.UidList;
import seedu.address.model.person.Person;

/**
 * Creates mailing list of customer name and email address in a event.
 */
public class MailEventCommand extends Command {
    public static final String COMMAND_WORD = "mailEvent";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Creates mailing list of all persons in an event in csv format, "
            + "and stores the csv file in the /data folder. "
            + "Parameters: "
            + "EVENT INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 3";
    public static final String MESSAGE_MAIL_EVENT_SUCCESS = " Generated mailing list for event: %s, "
            + "stored %s.csv file in /data folder.";
    public static final String FILE_OPS_ERROR_MESSAGE = "Could not save mailing list to file: %s.csv";
    public static final String EVENT_EMPTY_ERROR_MESSAGE = "There is no person in the event: %s.";
    private final Index eventIndex;
    /**
     * Constructor of MailEvent Command
     * @param eventIndex index of the event to create mailing list with.
     */
    public MailEventCommand(Index eventIndex) {
        requireNonNull(eventIndex);
        this.eventIndex = eventIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownPersonList = model.getFilteredPersonList();
        List<Event> lastShownEventList = model.getFilteredEventList();
        Event eventToMail = lastShownEventList.get(eventIndex.getZeroBased());
        String eventTitle = eventToMail.getEventTitle();
        // check if index is valid
        if (eventIndex.getZeroBased() >= lastShownEventList.size()) {
            throw new CommandException(MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }
        // check if there is any person in the event
        if (eventToMail.getUids().size() == 0) {
            throw new CommandException(String.format(EVENT_EMPTY_ERROR_MESSAGE, eventTitle));
        }
        // assume eventTitle is alphanumeric
        File csvFile = new File(String.format("data/%s.csv", eventTitle));
        try {
            FileWriter fw = new FileWriter(csvFile);
            CSVWriter writer = new CSVWriter(fw);
            writer.writeAll(getMailingList(eventToMail, lastShownPersonList));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException(String.format(FILE_OPS_ERROR_MESSAGE, eventTitle));
        }
        return new CommandResult(String.format(MESSAGE_MAIL_EVENT_SUCCESS, eventTitle, eventTitle));
    }

    private static List<String[]> getMailingList(Event event, List<Person> personList) {
        UidList uids = event.getUids();
        List<String[]> dataLines = new ArrayList<>();
        String[] header = {"Name", "Email"};
        dataLines.add(header);
        for (Person person : personList) {
            if (uids.contains(person.getUid())) {
                dataLines.add(new String[] {person.getName().toString(), person.getEmail().toString()});
            }
        }
        return dataLines;
    }
}
