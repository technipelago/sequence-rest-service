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

import javax.persistence.*;
import java.io.Serializable;

/**
 * Entity that holds the next available sequence number for a specific sequence definition.
 */
@Entity
public class SequenceNumber implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "definition_id")
    private SequenceDefinition definition;

    @Column(name = "sequence_group", nullable = true, length = 40)
    private String group;

    @Column(nullable = false)
    private Long number = 1L;

    protected SequenceNumber() {
    }

    public SequenceNumber(SequenceDefinition sequenceDefinition) {
        this.definition = sequenceDefinition;
    }

    public SequenceNumber(SequenceDefinition definition, Long number) {
        this.definition = definition;
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public SequenceDefinition getDefinition() {
        return definition;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return getNumber().toString();
    }
}
