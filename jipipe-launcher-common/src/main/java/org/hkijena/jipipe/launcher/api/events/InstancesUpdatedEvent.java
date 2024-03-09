package org.hkijena.jipipe.launcher.api.events;

import org.hkijena.jipipe.api.events.AbstractJIPipeEvent;

public class InstancesUpdatedEvent extends AbstractJIPipeEvent {
    public InstancesUpdatedEvent(Object source) {
        super(source);
    }
}
