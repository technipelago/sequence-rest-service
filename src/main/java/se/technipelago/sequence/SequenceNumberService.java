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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service for managing sequences.
 */
@Service
public class SequenceNumberService {

    @Autowired
    private SequenceDefinitionRepository sequenceDefinitionRepository;

    @Autowired
    private SequenceNumberRepository sequenceNumberRepository;

    public Iterable<SequenceDefinition> getDefinitions(final Long tenant) {
        return sequenceDefinitionRepository.findAllByTenantId(tenant);
    }

    /**
     * Create new sequence.
     *
     * @param tenant tenant id
     * @param name   name of sequence
     * @param format number format
     * @param start  start number for the sequence
     * @return the created SequenceDefinition
     */
    @Transactional
    public SequenceDefinition create(final Long tenant, final String name, final String format, final Long start) {
        SequenceDefinition sequenceDefinition = sequenceDefinitionRepository.findByNameAndTenantId(name, tenant);
        if (sequenceDefinition == null) {
            sequenceDefinition = new SequenceDefinition(tenant, name, format);
            sequenceDefinition = sequenceDefinitionRepository.save(sequenceDefinition);
            if (start != null) {
                SequenceNumber sequenceNumber = new SequenceNumber(sequenceDefinition, start);
                sequenceNumberRepository.save(sequenceNumber);
            }

        }
        return sequenceDefinition;
    }

    /**
     * Delete a sequence.
     *
     * @param tenant tenant id
     * @param name   name of sequence
     * @return deleted SequenceDefinition
     */
    @Transactional
    public SequenceDefinition delete(final Long tenant, final String name) {
        SequenceDefinition sequenceDefinition = sequenceDefinitionRepository.findByNameAndTenantId(name, tenant);
        if (sequenceDefinition == null) {
            throw new ResourceNotFoundException("Sequence definition [" + name + "] not found in tenant [" + tenant + "]");
        }

        sequenceNumberRepository.delete(sequenceNumberRepository.findByDefinition(sequenceDefinition));

        sequenceDefinitionRepository.delete(sequenceDefinition);

        return sequenceDefinition;
    }

    /**
     * Update existing sequence. To reduce concurrency problems the caller must specify both the current number
     * and the new start number. If the specified current number does not match the persistent number, update will fail.
     *
     * @param tenant        tenant id
     * @param name          name of sequence
     * @param format        number format
     * @param currentNumber current start number
     * @param nextNumber    new start number
     * @return updated SequenceDefinition
     */
    @Transactional
    public SequenceDefinition update(final Long tenant, final String name, final String format, final Long currentNumber, final Long nextNumber) {
        SequenceDefinition sequenceDefinition = sequenceDefinitionRepository.findByNameAndTenantId(name, tenant);
        if (sequenceDefinition == null) {
            throw new ResourceNotFoundException("Sequence definition [" + name + "] not found in tenant [" + tenant + "]");
        }

        if (format != null) {
            sequenceDefinition.setFormat(format);
            sequenceDefinitionRepository.save(sequenceDefinition);
        }

        if (nextNumber != null) {
            SequenceNumber sequenceNumber = sequenceNumberRepository.findByDefinitionAndGroupIsNull(sequenceDefinition);
            if (sequenceNumber == null) {
                sequenceNumber = new SequenceNumber(sequenceDefinition, nextNumber);
            } else {
                sequenceNumber = sequenceNumberRepository.findById(sequenceNumber.getId());
                if (sequenceNumber == null) {
                    throw new org.springframework.data.rest.webmvc.ResourceNotFoundException("Sequence number [" + name + "] not found in tenant [" + tenant + "]");
                }
                Long persistentNumber = sequenceNumber.getNumber();
                if (!persistentNumber.equals(currentNumber)) {
                    throw new IllegalStateException("Sequence number [" + name + "] in tenant [" + tenant + "] was changed to [" + persistentNumber + "] by another request");
                }
                sequenceNumber.setNumber(nextNumber);
            }
            sequenceNumberRepository.save(sequenceNumber);
        }

        return sequenceDefinition;
    }

    /**
     * Get next number from a sequence.
     *
     * @param tenant tenant id
     * @param name   name of sequence
     * @return next number from the sequence
     */
    @Transactional
    public String next(final Long tenant, final String name) {
        SequenceDefinition sequenceDefinition = sequenceDefinitionRepository.findByNameAndTenantId(name, tenant);
        if (sequenceDefinition == null) {
            throw new ResourceNotFoundException("Sequence definition [" + name + "] not found in tenant [" + tenant + "]");
        }

        SequenceNumber sequenceNumber = sequenceNumberRepository.findByDefinitionAndGroupIsNull(sequenceDefinition);
        if (sequenceNumber == null) {
            sequenceNumber = new SequenceNumber(sequenceDefinition, 1L);
            sequenceNumber = sequenceNumberRepository.save(sequenceNumber);
        }

        sequenceNumber = sequenceNumberRepository.findById(sequenceNumber.getId());
        if (sequenceNumber == null) {
            throw new org.springframework.data.rest.webmvc.ResourceNotFoundException("Sequence number [" + name + "] not found in tenant [" + tenant + "]");
        }
        Long number = sequenceNumber.getNumber();
        sequenceNumber.setNumber(number + 1);
        sequenceNumberRepository.save(sequenceNumber);

        final String format = sequenceDefinition.getFormat();
        return format != null ? String.format(format, number) : number.toString();
    }

    /**
     * Get status for a sequence.
     *
     * @param tenant tenant id
     * @param name   name of sequence
     * @return snapshot of the sequence status
     */
    @Transactional
    public SequenceStatus status(final Long tenant, final String name) {
        SequenceDefinition sequenceDefinition = sequenceDefinitionRepository.findByNameAndTenantId(name, tenant);
        if (sequenceDefinition == null) {
            throw new ResourceNotFoundException("Sequence definition [" + name + "] not found in tenant [" + tenant + "]");
        }

        SequenceNumber sequenceNumber = sequenceNumberRepository.findByDefinitionAndGroupIsNull(sequenceDefinition);
        if (sequenceNumber == null) {
            sequenceNumber = new SequenceNumber(sequenceDefinition, 1L);
            sequenceNumber = sequenceNumberRepository.save(sequenceNumber);
        }

        return new SequenceStatus(sequenceDefinition.getName(), sequenceDefinition.getFormat(), sequenceNumber.getNumber());
    }
}
