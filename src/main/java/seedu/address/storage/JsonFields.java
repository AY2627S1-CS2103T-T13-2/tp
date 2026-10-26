package seedu.address.storage;

import java.util.function.Function;
import java.util.function.Predicate;

import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Helpers for turning the fields of a Jackson-friendly object back into model objects. Every field in the data file
 * must be present and valid, and the first problem found is the one reported.
 */
final class JsonFields {

    private JsonFields() {}

    /**
     * Returns {@code value}, which must have been present in the data file.
     *
     * @param missingFieldFormat The message to report if the value is missing, with {@code %s} for the field name.
     * @throws IllegalValueException if {@code value} is null.
     */
    static <T> T required(T value, String missingFieldFormat, String fieldName) throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(String.format(missingFieldFormat, fieldName));
        }
        return value;
    }

    /**
     * Returns the model object that {@code build} makes from {@code raw}, which must be valid.
     *
     * @throws IllegalValueException with {@code constraintsMessage} if {@code isValid} rejects {@code raw}.
     */
    static <S, T> T validated(S raw, Predicate<S> isValid, String constraintsMessage, Function<S, T> build)
            throws IllegalValueException {
        if (!isValid.test(raw)) {
            throw new IllegalValueException(constraintsMessage);
        }
        return build.apply(raw);
    }

}
