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
import org.springframework.security.access.annotation.Secured;
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
 * Sequence REST Controller.
 */
@RestController
public class SequenceController {

    @Autowired
    private Reactor reactor;

    @Autowired
    private SequenceNumberService sequenceNumberService;

    @Secured("ROLE_ADMIN")
    @RequestMapping("/api/sequence/{tenant}")
    public Iterable<SequenceDefinition> list(final @PathVariable("tenant") Long tenant) {
        return sequenceNumberService.getDefinitions(tenant);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/sequence/{tenant}/{name}", method = RequestMethod.POST)
    @Transactional
    public SequenceDefinition create(final @PathVariable("tenant") Long tenant,
                                     final @PathVariable("name") String name,
                                     final @RequestBody RequestParameters params) {
        String format = params.getFormat();
        Long number = params.getNumber();
        return sequenceNumberService.create(tenant, name, format != null ? format : "%d", number != null ? number : 1L);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/sequence/{tenant}/{name}", method = RequestMethod.GET)
    @Transactional
    public SequenceStatus status(final @PathVariable("tenant") Long tenant,
                                     final @PathVariable("name") String name) {
        return sequenceNumberService.status(tenant, name);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/sequence/{tenant}/{name}", method = RequestMethod.PUT)
    @Transactional
    public SequenceDefinition update(final @PathVariable("tenant") Long tenant,
                                     final @PathVariable("name") String name,
                                     final @RequestBody RequestParameters params) {
        return sequenceNumberService.update(tenant, name, params.getFormat(), params.getCurrent(), params.getNumber());
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/sequence/{tenant}/{name}", method = RequestMethod.DELETE)
    @Transactional
    public SequenceDefinition delete(final @PathVariable("tenant") Long tenant,
                                     final @PathVariable("name") String name) {
        return sequenceNumberService.delete(tenant, name);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/sequence/{tenant}/{name}/next", method = RequestMethod.GET)
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
        private Long current;
        private Long number;

        public RequestParameters() {
        }

        public String getFormat() {
            return format;
        }

        public Long getCurrent() {
            return current;
        }

        public Long getNumber() {
            return number;
        }
    }
}
