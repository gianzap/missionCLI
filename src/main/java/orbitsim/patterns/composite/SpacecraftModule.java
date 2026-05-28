package orbitsim.patterns.composite;

import orbitsim.exception.SystemFaultException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Nodo composito — aggrega subsystem.
 * getStatus() aggrega lo stato peggiore dei figli.
 */
public class SpacecraftModule implements SpacecraftComponent {
    private static final Logger LOG = Logger.getLogger(SpacecraftModule.class.getName());
    private final String id, name;
    private final List<SpacecraftComponent> children = new ArrayList<>();
    private SystemStatus overrideStatus = null;

    public SpacecraftModule(String id, String name) { this.id = id; this.name = name; }

    public void add(SpacecraftComponent c) { children.add(c); }

    @Override
    public SystemStatus getStatus() {
        if (overrideStatus != null) return overrideStatus;
        if (children.isEmpty()) return SystemStatus.NOMINAL;
        return children.stream()
                .map(SpacecraftComponent::getStatus)
                .max(Comparator.comparingInt(SystemStatus::ordinal))
                .orElse(SystemStatus.NOMINAL);
    }

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
        for (SpacecraftComponent c : children) c.shutdown();
        overrideStatus = SystemStatus.OFFLINE;
    }

    @Override public String getId()   { return id; }
    @Override public String getName() { return name; }
    @Override public List<SpacecraftComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
