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

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.LockModeType;

/**
 * Sequence Number Repository.
 */
@RepositoryRestResource(collectionResourceRel = "sequence", path = "sequence")
interface SequenceNumberRepository extends PagingAndSortingRepository<SequenceNumber, Long> {

    Iterable<SequenceNumber> findByDefinition(SequenceDefinition definition);

    SequenceNumber findByDefinitionAndGroupIsNull(SequenceDefinition definition);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    SequenceNumber findById(Long id);
}
