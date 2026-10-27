package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

public class PaymentMonthRefresherTest {
    @Test
    public void millisUntilNextMonth_middleOfMonth_returnsTimeToBoundary() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-15T04:30:00Z"), ZoneId.of("Asia/Singapore"));
        long expected = Duration.between(Instant.parse("2026-10-15T04:30:00Z"),
                Instant.parse("2026-10-31T16:00:00Z")).toMillis();

        assertEquals(expected, PaymentMonthRefresher.millisUntilNextMonth(clock));
    }

    @Test
    public void millisUntilNextMonth_endOfYear_usesJanuaryBoundary() {
        Clock clock = Clock.fixed(Instant.parse("2026-12-31T15:59:59Z"), ZoneId.of("Asia/Singapore"));
        assertEquals(1000, PaymentMonthRefresher.millisUntilNextMonth(clock));
    }
}
