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
