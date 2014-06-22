package se.technipelago.sequence;

import org.springframework.web.context.request.async.DeferredResult;

import java.io.Serializable;

/**
 * Sequence reactor events carry this payload.
 */
public class SequenceEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long tenant;
    private String name;
    private DeferredResult<String> result;

    public SequenceEvent(Long tenant, String name, DeferredResult<String> result) {
        this.tenant = tenant;
        this.name = name;
        this.result = result;
    }

    public Long getTenant() {
        return tenant;
    }

    public String getName() {
        return name;
    }

    public DeferredResult<String> getResult() {
        return result;
    }
}
