package seedu.address.model.common;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of records in which no two records have the same ID, kept in ascending order of ID.
 * Students and lessons each have a subclass that supplies how to read an ID and what to throw when a record is
 * not found.
 *
 * @param <T> the kind of record in the list.
 * @param <I> the kind of ID that identifies a record.
 */
public abstract class UniqueIdList<T, I extends Comparable<I>> implements Iterable<T> {

    private final ObservableList<T> internalList = FXCollections.observableArrayList();
    private final ObservableList<T> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    private final Function<T, I> idOf;
    private final Supplier<? extends RuntimeException> notFound;
    private final String duplicateIdMessage;
    private final String replacementIdMessage;

    /**
     * Creates an empty list.
     *
     * @param idOf Reads the ID of a record.
     * @param notFound Creates the exception to throw when a record is not in the list.
     * @param duplicateIdMessage The message to report when two records have the same ID.
     * @param replacementIdMessage The message to report when a replacement record has a different ID.
     */
    protected UniqueIdList(Function<T, I> idOf, Supplier<? extends RuntimeException> notFound,
            String duplicateIdMessage, String replacementIdMessage) {
        this.idOf = requireNonNull(idOf);
        this.notFound = requireNonNull(notFound);
        this.duplicateIdMessage = requireNonNull(duplicateIdMessage);
        this.replacementIdMessage = requireNonNull(replacementIdMessage);
    }

    /**
     * Returns true if the list contains a record with the given ID.
     */
    public boolean contains(I id) {
        requireNonNull(id);
        return internalList.stream().anyMatch(record -> idOf.apply(record).equals(id));
    }

    /**
     * Returns the record with the given ID, if there is one.
     */
    public Optional<T> find(I id) {
        requireNonNull(id);
        return internalList.stream().filter(record -> idOf.apply(record).equals(id)).findFirst();
    }

    /**
     * Adds a record to the list, keeping the list in ascending order of ID.
     *
     * @throws IllegalArgumentException if a record with the same ID is already in the list.
     */
    public void add(T toAdd) {
        requireNonNull(toAdd);
        I id = idOf.apply(toAdd);
        if (contains(id)) {
            throw new IllegalArgumentException(duplicateIdMessage);
        }
        int position = 0;
        while (position < internalList.size() && idOf.apply(internalList.get(position)).compareTo(id) < 0) {
            position++;
        }
        internalList.add(position, toAdd);
    }

    /**
     * Replaces {@code target} with {@code edited}, which must have the same ID.
     *
     * @throws RuntimeException the not-found exception of the subclass, if {@code target} is not in the list.
     * @throws IllegalArgumentException if {@code edited} has a different ID from {@code target}.
     */
    public void replace(T target, T edited) {
        requireNonNull(target);
        requireNonNull(edited);
        int index = internalList.indexOf(target);
        if (index == -1) {
            throw notFound.get();
        }
        if (!idOf.apply(target).equals(idOf.apply(edited))) {
            throw new IllegalArgumentException(replacementIdMessage);
        }
        internalList.set(index, edited);
    }

    /**
     * Removes the record with the given ID and returns it.
     *
     * @throws RuntimeException the not-found exception of the subclass, if there is no such record.
     */
    public T remove(I id) {
        requireNonNull(id);
        T toRemove = find(id).orElseThrow(notFound);
        internalList.remove(toRemove);
        return toRemove;
    }

    /**
     * Replaces the contents of this list with {@code records}, sorted by ID.
     *
     * @throws IllegalArgumentException if {@code records} contains two records with the same ID.
     */
    protected void setAll(List<T> records) {
        requireNonNull(records);
        Set<I> seen = new HashSet<>();
        for (T record : records) {
            if (!seen.add(idOf.apply(record))) {
                throw new IllegalArgumentException(duplicateIdMessage);
            }
        }
        List<T> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparing(idOf));
        internalList.setAll(sorted);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<T> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<T> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // comparing classes also handles nulls, and keeps a list of students apart from a list of lessons
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        return internalList.equals(((UniqueIdList<?, ?>) other).internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

}
