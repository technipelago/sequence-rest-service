package se.technipelago.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by goran on 2014-06-21.
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

    @Transactional
    public SequenceDefinition update(final Long tenant, final String name, final String format, final Long nextNumber) {
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
                sequenceNumber.setNumber(nextNumber);
            }
            sequenceNumberRepository.save(sequenceNumber);
        }

        return sequenceDefinition;
    }

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

}
