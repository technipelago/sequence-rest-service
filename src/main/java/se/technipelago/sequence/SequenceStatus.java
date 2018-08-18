/*
 * Copyright (c) 2018 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
