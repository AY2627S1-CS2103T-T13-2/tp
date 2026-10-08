package seedu.address.testutil;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;

/**
 * A clock fixed in October 2026, so that tests about the current month do not depend on today's date.
 */
public class TypicalClock {

    public static final YearMonth CURRENT_MONTH = YearMonth.of(2026, 10);

    public static final YearMonth LAST_MONTH = YearMonth.of(2026, 9);

    public static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-15T04:00:00Z"), ZoneOffset.UTC);

}
