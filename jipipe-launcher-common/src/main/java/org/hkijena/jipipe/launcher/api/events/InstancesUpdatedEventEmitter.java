package org.hkijena.jipipe.launcher.api.events;

import org.hkijena.jipipe.api.events.JIPipeEventEmitter;

public class InstancesUpdatedEventEmitter extends JIPipeEventEmitter<InstancesUpdatedEvent, InstancesUpdatedEventListener> {

    @Override
    protected void call(InstancesUpdatedEventListener instancesUpdatedEventListener, InstancesUpdatedEvent event) {
        instancesUpdatedEventListener.onInstanceRepositoryUpdated(event);
    }
}
