package se.technipelago.sequence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Sequence Definition Repository.
 */
@RepositoryRestResource(collectionResourceRel = "definitions", path = "definitions")
interface SequenceDefinitionRepository extends CrudRepository<SequenceDefinition, Long> {
    Iterable<SequenceDefinition> findAllByTenantId(Long tenant);
    SequenceDefinition findByNameAndTenantId(String name, Long tenant);
}
