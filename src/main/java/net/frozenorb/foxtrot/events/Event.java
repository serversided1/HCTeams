package net.frozenorb.foxtrot.events;

public interface Event {
    public String getName();
    public boolean isActive();
    public void tick();
    public void setActive(boolean active);
    public boolean isHidden();
    public void setHidden(boolean hidden);
    public boolean activate();
    public boolean deactivate();

    public EventType getType();
}
