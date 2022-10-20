/*
 * Copyright 2005-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.core.uaa.auth;

import org.openwms.core.event.RootApplicationEvent;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * A ClientEvent.
 *
 * @author Heiko Scherrer
 */
public class ClientEvent extends RootApplicationEvent implements Serializable {

    private EventType type;

    public ClientEvent(Client source, EventType type) {
        super(source);
        Assert.notNull(type, "type must not be null");
        this.type = type;
    }

    public enum EventType {
        CREATED,
        MODIFIED,
        DELETED
    }

    public EventType getType() {
        return type;
    }

    @Override
    public Client getSource() {
        return (Client) super.getSource();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
