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
