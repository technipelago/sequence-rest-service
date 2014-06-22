package se.technipelago.sequence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for SequenceNumberService.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SequenceApplication.class)
public class SequenceServiceIntegrationTests {

    @Autowired
    SequenceNumberService sequenceNumberService;

    @Test
    public void testSequences() {
        SequenceDefinition sequence = sequenceNumberService.create(1L, "test", "%04d", 100L);
        assert sequence.getId() != null;
        assertEquals("0100", sequenceNumberService.next(1L, "test"));
        assertEquals("0101", sequenceNumberService.next(1L, "test"));
        assertEquals("0102", sequenceNumberService.next(1L, "test"));
        sequenceNumberService.update(1L, "test", "%03d", 99L);
        assertEquals("099", sequenceNumberService.next(1L, "test"));
        assertEquals("100", sequenceNumberService.next(1L, "test"));
        assertEquals("101", sequenceNumberService.next(1L, "test"));
        sequenceNumberService.delete(1L, "test");
        try {
            sequenceNumberService.next(1L, "test");
            fail("Sequence [test] should have been deleted, but it still exists");
        } catch(ResourceNotFoundException e) {
            // This should happen.
        }
    }
}
