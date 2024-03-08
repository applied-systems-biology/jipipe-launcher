package org.hkijena.jipipe.launcher.api;

import org.hkijena.jipipe.api.events.AbstractJIPipeEvent;
import org.hkijena.jipipe.api.events.JIPipeEvent;
import org.hkijena.jipipe.api.events.JIPipeEventEmitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JIPipeInstanceRepository {
    private final List<JIPipeInstance> instanceList = new ArrayList<>();

    private final UpdatedEventEmitter updatedEventEmitter = new UpdatedEventEmitter();

    public JIPipeInstanceRepository() {
    }

    public boolean contains(JIPipeInstance pipePackage) {
        return instanceList.contains(pipePackage);
    }

    public List<JIPipeInstance> getSortedInstanceList() {
        return instanceList.stream().sorted(Comparator.comparing(JIPipeInstance::isInstalled)
                .thenComparing(JIPipeInstance::getVersion, new VersionComparator().reversed())
                .thenComparing(JIPipeInstance::getDisplayName)).collect(Collectors.toList());
    }

    public JIPipeInstance findLatestInstalledInstance() {
        return getSortedInstanceList().stream().filter(JIPipeInstance::isInstalled).findFirst().orElse(null);
    }

    public JIPipeInstance findLatestAvailableInstance() {
        return getSortedInstanceList().stream().filter(JIPipeInstance::isNotInstalled).findFirst().orElse(null);
    }

    public void update(List<JIPipeInstance> availableInstances) {
        instanceList.clear();
        instanceList.addAll(availableInstances);
        updatedEventEmitter.emit(new UpdatedEvent(this));
    }

    public UpdatedEventEmitter getUpdatedEventEmitter() {
        return updatedEventEmitter;
    }

    public static class UpdatedEvent extends AbstractJIPipeEvent {
        public UpdatedEvent(Object source) {
            super(source);
        }
    }

    public interface UpdatedEventListener {
        void onInstanceRepositoryUpdated(UpdatedEvent event);
    }

    public static class UpdatedEventEmitter extends JIPipeEventEmitter<UpdatedEvent, UpdatedEventListener> {

        @Override
        protected void call(UpdatedEventListener updatedEventListener, UpdatedEvent event) {
            updatedEventListener.onInstanceRepositoryUpdated(event);
        }
    }
}
