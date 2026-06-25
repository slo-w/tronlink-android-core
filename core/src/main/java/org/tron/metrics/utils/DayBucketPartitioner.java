package org.tron.metrics.utils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DayBucketPartitioner {

    private static final DateTimeFormatter DAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DayBucketPartitioner() {
    }

    public static String todayUtc() {
        return LocalDate.now(ZoneOffset.UTC).format(DAY_FORMAT);
    }

    /**
     * Splits database entries and incoming entries by the current UTC day.
     *
     * @param dbAll    all entries currently in the database
     * @param incoming new/updated entries to be persisted
     * @param getDay   function that extracts the day string from an entity
     * @return a {@link DayPartition} containing stale (before-today) and current-day entries
     */
    public static <T> DayPartition<T> splitByDay(List<T> dbAll,
                                                  List<T> incoming,
                                                  Function<T, String> getDay) {
        String dayNow = todayUtc();
        List<T> staleEntries = dbAll.stream()
                .filter(entity -> !dayNow.equals(getDay.apply(entity)))
                .collect(Collectors.toList());
        List<T> currentDayEntries = incoming.stream()
                .filter(entity -> dayNow.equals(getDay.apply(entity)))
                .collect(Collectors.toList());
        return new DayPartition<>(dayNow, staleEntries, currentDayEntries);
    }

    public static class DayPartition<T> {
        private final String dayNow;
        private final List<T> staleEntries;
        private final List<T> currentDayEntries;

        public DayPartition(String dayNow, List<T> staleEntries, List<T> currentDayEntries) {
            this.dayNow = dayNow;
            this.staleEntries = staleEntries;
            this.currentDayEntries = currentDayEntries;
        }

        public String getDayNow() {
            return dayNow;
        }

        public List<T> getStaleEntries() {
            return staleEntries;
        }

        public List<T> getCurrentDayEntries() {
            return currentDayEntries;
        }
    }
}