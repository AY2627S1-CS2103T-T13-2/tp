package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BRYAN;
import static seedu.address.testutil.TypicalStudents.CHLOE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.student.exceptions.StudentNotFoundException;
import seedu.address.testutil.StudentBuilder;

public class UniqueStudentListTest {

    private final UniqueStudentList list = new UniqueStudentList();

    @Test
    public void contains_andFind() {
        assertFalse(list.contains(ALICE.getId()));
        assertEquals(Optional.empty(), list.find(ALICE.getId()));

        list.add(ALICE);

        assertTrue(list.contains(ALICE.getId()));
        assertEquals(Optional.of(ALICE), list.find(ALICE.getId()));
        assertFalse(list.contains(BRYAN.getId()));
    }

    @Test
    public void contains_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> list.contains(null));
        assertThrows(NullPointerException.class, () -> list.find(null));
    }

    @Test
    public void add_keepsAscendingOrderOfId() {
        list.add(CHLOE);
        list.add(ALICE);
        list.add(BRYAN);

        assertEquals(Arrays.asList(ALICE, BRYAN, CHLOE), list.asUnmodifiableObservableList());
    }

    @Test
    public void add_sameId_throwsIllegalArgumentException() {
        list.add(ALICE);
        Student sameIdDifferentPerson = new StudentBuilder(BRYAN).withId(ALICE.getId().value).build();

        assertThrows(IllegalArgumentException.class, UniqueStudentList.MESSAGE_DUPLICATE_ID, () ->
                list.add(sameIdDifferentPerson));
    }

    @Test
    public void add_sameNameAndContactWithDifferentId_isAllowed() {
        // add rejects such a student, but a hand-edited file can contain one, and it is listed as it is
        list.add(ALICE);
        Student duplicate = new StudentBuilder(ALICE).withId(9).build();

        list.add(duplicate);

        assertEquals(Arrays.asList(ALICE, duplicate), list.asUnmodifiableObservableList());
    }

    @Test
    public void replace() {
        list.add(ALICE);
        Student paid = ALICE.markPaid(java.time.YearMonth.of(2026, 10));

        list.replace(ALICE, paid);

        assertEquals(Collections.singletonList(paid), list.asUnmodifiableObservableList());
    }

    @Test
    public void replace_targetMissing_throwsStudentNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> list.replace(ALICE, ALICE));
    }

    @Test
    public void replace_differentId_throwsIllegalArgumentException() {
        list.add(ALICE);
        assertThrows(IllegalArgumentException.class, () -> list.replace(ALICE, BRYAN));
    }

    @Test
    public void remove() {
        list.add(ALICE);
        list.add(BRYAN);

        assertEquals(ALICE, list.remove(ALICE.getId()));

        assertEquals(Collections.singletonList(BRYAN), list.asUnmodifiableObservableList());
    }

    @Test
    public void remove_missing_throwsStudentNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> list.remove(ALICE.getId()));
    }

    @Test
    public void setStudents_sortsById() {
        list.setStudents(Arrays.asList(CHLOE, ALICE, BRYAN));

        assertEquals(Arrays.asList(ALICE, BRYAN, CHLOE), list.asUnmodifiableObservableList());
    }

    @Test
    public void setStudents_replacesEarlierContents() {
        list.add(ALICE);

        list.setStudents(Collections.singletonList(BRYAN));

        assertEquals(Collections.singletonList(BRYAN), list.asUnmodifiableObservableList());
    }

    @Test
    public void setStudents_duplicateId_throwsIllegalArgumentException() {
        List<Student> sameId = Arrays.asList(ALICE, new StudentBuilder(BRYAN).withId(ALICE.getId().value).build());

        assertThrows(IllegalArgumentException.class, () -> list.setStudents(sameId));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> list.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void equals() {
        UniqueStudentList other = new UniqueStudentList();
        assertTrue(list.equals(other));
        assertTrue(list.equals(list));
        assertFalse(list.equals(null));
        assertFalse(list.equals(5));

        list.add(ALICE);
        assertFalse(list.equals(other));
        other.add(ALICE);
        assertTrue(list.equals(other));
        assertEquals(list.hashCode(), other.hashCode());
    }
}
