package org.hkijena.jipipe.launcher.api.events;

public interface InstancesUpdatedEventListener {
    void onInstanceRepositoryUpdated(InstancesUpdatedEvent event);
}
