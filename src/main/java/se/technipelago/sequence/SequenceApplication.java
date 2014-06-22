package se.technipelago.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.function.Function;
import reactor.spring.context.config.EnableReactor;

import static reactor.event.selector.Selectors.$;

@Configuration
@EnableAutoConfiguration
@EnableReactor
@ComponentScan
public class SequenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SequenceApplication.class, args);
    }

    @Autowired
    private SequenceNumberService sequenceNumberService;

    @Bean
    public Reactor reactor(Environment env) {
        Reactor reactor = Reactors.reactor(env, Environment.RING_BUFFER);

        reactor.receive($("sequence"), new Function<Event<SequenceEvent>, DeferredResult<String>>() {
            public DeferredResult<String> apply(Event<SequenceEvent> ev) {
                SequenceEvent data = ev.getData();
                DeferredResult<String> result = data.getResult();
                try {
                    String number = sequenceNumberService.next(data.getTenant(), data.getName());
                    result.setResult(number);
                } catch(Exception e) {
                    System.err.println(e.getMessage());
                    result.setErrorResult(e);
                }
                return result;
            }
        });

        return reactor;
    }
}
