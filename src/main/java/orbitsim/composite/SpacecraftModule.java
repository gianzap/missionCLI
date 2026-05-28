package orbitsim.composite;

import orbitsim.exception.SystemFaultException;
import orbitsim.patterns.composite.SpacecraftComponent;
import orbitsim.patterns.composite.SystemStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Nodo composito — aggrega subsystem.
 * getStatus() aggrega lo stato peggiore dei figli.
 */
public class SpacecraftModule implements orbitsim.patterns.composite.SpacecraftComponent {
    private static final Logger LOG = Logger.getLogger(SpacecraftModule.class.getName());
    private final String id, name;
    private final List<orbitsim.patterns.composite.SpacecraftComponent> children = new ArrayList<>();
    private orbitsim.patterns.composite.SystemStatus overrideStatus = null;

    public SpacecraftModule(String id, String name) { this.id = id; this.name = name; }

    public void add(orbitsim.patterns.composite.SpacecraftComponent c) { children.add(c); }

    @Override
    public orbitsim.patterns.composite.SystemStatus getStatus() {
        if (overrideStatus != null) return overrideStatus;
        if (children.isEmpty()) return orbitsim.patterns.composite.SystemStatus.NOMINAL;
        return children.stream()
            .map(orbitsim.patterns.composite.SpacecraftComponent::getStatus)
            .max(Comparator.comparingInt(orbitsim.patterns.composite.SystemStatus::ordinal))
            .orElse(orbitsim.patterns.composite.SystemStatus.NOMINAL);
    }

    public void setStatus(orbitsim.patterns.composite.SystemStatus s) { this.overrideStatus = s; }

    @Override
    public String getStatusReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-20s [%s]\n", name, getStatus()));
        children.forEach(c -> sb.append("    ").append(c.getStatusReport()));
        return sb.toString();
    }

    @Override
    public void shutdown() throws SystemFaultException {
        LOG.warning("Shutting down module: " + name);
        for (orbitsim.patterns.composite.SpacecraftComponent c : children) c.shutdown();
        overrideStatus = SystemStatus.OFFLINE;
    }

    @Override public String getId()   { return id; }
    @Override public String getName() { return name; }
    @Override public List<SpacecraftComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
