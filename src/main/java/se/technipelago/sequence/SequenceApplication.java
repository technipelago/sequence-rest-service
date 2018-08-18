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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.function.Consumer;
import reactor.spring.context.config.EnableReactor;

import static reactor.event.selector.Selectors.$;

@EnableReactor
@SpringBootApplication
public class SequenceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SequenceApplication.class, args);
    }

    @Autowired
    private SequenceNumberService sequenceNumberService;

    @Bean
    public Reactor reactor(final Environment env) {
        Reactor reactor = Reactors.reactor(env, Environment.RING_BUFFER);

        reactor.on($("sequence"), new Consumer<Event<SequenceEvent>>() {
            public void accept(Event<SequenceEvent> ev) {
                SequenceEvent data = ev.getData();
                DeferredResult<String> result = data.getResult();
                try {
                    String number = sequenceNumberService.next(data.getTenant(), data.getName());
                    result.setResult("{ \"number\": \"" + number + "\" }\n");
                } catch (Exception e) {
                    LOG.error("Exception in sequence event listener", e);
                    result.setErrorResult(e);
                }
            }
        });

        return reactor;
    }
}
