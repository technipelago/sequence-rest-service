package se.technipelago.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.Reactor;
import reactor.event.Event;

/**
 * Created by goran on 2014-05-23.
 */
@RestController
public class SequenceController {

    @Autowired
    private Reactor reactor;

    @Autowired
    private SequenceNumberService sequenceNumberService;

    @RequestMapping("/api/sequence/{tenant}")
    public Iterable<SequenceDefinition> list(final @PathVariable("tenant") Long tenant) {
        return sequenceNumberService.getDefinitions(tenant);
    }

    @RequestMapping(value = "/api/sequence/{tenant}/{name}", method = RequestMethod.POST)
    @Transactional
    public SequenceDefinition create(final @PathVariable("tenant") Long tenant,
                                     final @PathVariable("name") String name,
                                     final @RequestBody RequestParameters params) {
        String format = params.getFormat();
        Long number = params.getNumber();
        return sequenceNumberService.create(tenant, name, format != null ? format : "%d", number != null ? number : 1L);
    }

    @RequestMapping(value = "/api/sequence/{tenant}/{name}", method = RequestMethod.PUT)
    @Transactional
    public SequenceDefinition update(final @PathVariable("tenant") Long tenant,
                                     final @PathVariable("name") String name,
                                     final @RequestBody RequestParameters params) {
        return sequenceNumberService.update(tenant, name, params.getFormat(), params.getNumber());
    }

    @RequestMapping(value = "/api/sequence/{tenant}/{name}", method = RequestMethod.DELETE)
    @Transactional
    public SequenceDefinition delete(final @PathVariable("tenant") Long tenant,
                                     final @PathVariable("name") String name) {
        return sequenceNumberService.delete(tenant, name);
    }

    @RequestMapping(value = "/api/sequence/{tenant}/{name}", method = RequestMethod.GET)
    @Transactional
    public DeferredResult<String> next(final @PathVariable("tenant") Long tenant,
                                       final @PathVariable("name") String name) {
        DeferredResult<String> response = new DeferredResult<String>();
        SequenceEvent data = new SequenceEvent(tenant, name, response);
        reactor.notify("sequence", Event.wrap(data));
        return response;
    }

    private static class RequestParameters {
        private String format;
        private Long number;

        public RequestParameters() {
        }

        public String getFormat() {
            return format;
        }

        public Long getNumber() {
            return number;
        }
    }
}
