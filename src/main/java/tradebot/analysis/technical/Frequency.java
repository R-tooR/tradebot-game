package tradebot.analysis.technical;

public enum Frequency {
    SECOND(1000),
    MINUTE(60_000),
    _15_MINUTES(15*60_000),
    HOUR(60 * 60_000),
    _3_HOURS(180 * 60_000),
    _12_HOURS(720 * 60_000),
    DAY(1440 * 60_000);

    public final long milliseconds;

    Frequency(long milliseconds) {
        this.milliseconds = milliseconds;
    }
}
