package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
//import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Department;
import seedu.address.model.person.Email;
import seedu.address.model.person.Manager;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Rating;
import seedu.address.model.tag.Tag;

/**
 * Togggles the privacy value of an existing person's information in the address book.
 */
public class PrivacyCommand extends Command {

    public static final String COMMAND_WORD = "privacy";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Changes the privacy value of information of the"
            + "person identified by the index number used in the displayed person list. "
            + "Only Phone, Email and Address can be Private.\n"
            + "Input value as y to make private and n to make non-private.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_PHONE + "y/n] "
            + "[" + PREFIX_EMAIL + "y/n] "
            + "[" + PREFIX_ADDRESS + "y/n]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "y "
            + PREFIX_ADDRESS + "n";

    public static final String MESSAGE_EDIT_PRIVACY_SUCCESS = "Privacy modified: %1$s";
    public static final String MESSAGE_NOT_EDITED = "U DIDNT CHANGE ANYTHING IDIOT";

    private final Index index;
    private final FieldsToChange fieldsToChange;

    public PrivacyCommand(Index index, FieldsToChange fieldsToChange) {
        requireNonNull(index);
        requireNonNull(fieldsToChange);

        this.index = index;
        this.fieldsToChange = fieldsToChange;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = changePrivacy(personToEdit, fieldsToChange);
        model.updatePerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_EDIT_PRIVACY_SUCCESS, editedPerson));
    }

    /**
     * Changes the privacy values of a {@code Person} with the options stated in {@code fieldsToChange}
     */
    private static Person changePrivacy(Person personToEdit, FieldsToChange fieldsToChange) {
        assert personToEdit != null;

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        Address address = personToEdit.getAddress();
        Rating rating = personToEdit.getRating();
        Department department = personToEdit.getDepartment();
        Manager manager = personToEdit.getManager();
        Set<Tag> tags = personToEdit.getTags();

        if (fieldsToChange.getPhonePrivacy().isPresent()) {
            phone = new Phone(personToEdit.getPhone().value, "Y");
        }
        if (fieldsToChange.getEmailPrivacy().isPresent()) {
            personToEdit.getEmail().setPrivate(fieldsToChange.getEmailPrivacy().get());
        }
        if (fieldsToChange.getAddressPrivacy().isPresent()) {
            personToEdit.getAddress().setPrivate(fieldsToChange.getAddressPrivacy().get());
        }

        return new Person(name, phone, email, address, rating, department, manager, tags);
    }

    /**
     * Stores the fields to change privacy of the person with.
     */
    public static class FieldsToChange {
        private String phonePrivacy;
        private String emailPrivacy;
        private String addressPrivacy;

        public FieldsToChange() {}

        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(phonePrivacy, emailPrivacy, addressPrivacy);
        }

        public void setPhonePrivacy(String phonePrivacy) { this.phonePrivacy = phonePrivacy; }

        public Optional<String> getPhonePrivacy() { return Optional.ofNullable(phonePrivacy); }

        public void setEmailPrivacy(String emailPrivacy) { this.emailPrivacy = emailPrivacy; }

        public Optional<String> getEmailPrivacy() { return Optional.ofNullable(emailPrivacy); }

        public void setAddressPrivacy(String addressPrivacy) { this.addressPrivacy = addressPrivacy; }

        public Optional<String> getAddressPrivacy() { return Optional.ofNullable(addressPrivacy); }


    }

}