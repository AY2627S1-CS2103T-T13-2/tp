package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

/** Refreshes payment-dependent UI when the local calendar month changes. */
class PaymentMonthRefresher {
    private final Runnable refreshAction;
    private final Clock clock;
    private Timeline timeline;

    PaymentMonthRefresher(Runnable refreshAction) {
        this(refreshAction, Clock.systemDefaultZone());
    }

    PaymentMonthRefresher(Runnable refreshAction, Clock clock) {
        this.refreshAction = requireNonNull(refreshAction);
        this.clock = requireNonNull(clock);
    }

    /** Schedules the next refresh at the first instant of the next local month. */
    void start() {
        stop();
        timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(millisUntilNextMonth(clock)), event -> {
            refreshAction.run();
            start();
        }));
        timeline.play();
    }

    void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    static long millisUntilNextMonth(Clock clock) {
        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime nextMonth = now.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay(now.getZone());
        return Math.max(1, Duration.between(now, nextMonth).toMillis());
    }
}
