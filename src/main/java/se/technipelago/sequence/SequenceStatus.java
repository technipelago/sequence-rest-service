package se.technipelago.sequence;

/**
 * A sequence generator snapshot.
 */
public class SequenceStatus {
    private final long timestamp;
    private final String name;
    private final String format;
    private final long number;

    public SequenceStatus(String name, String format, Long number) {
        this.name = name;
        this.format = format;
        this.number = number != null ? number.longValue() : 0L;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public long getNumber() {
        return number;
    }

    String getNumberFormatted() {
        return String.format(format != null ? format : "%s", number);
    }

    @Override
    public String toString() {
        return name + "=" + getNumberFormatted();
    }
}
