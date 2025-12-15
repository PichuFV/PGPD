package common;

public final class Fork {
    private final int id;

    public Fork(int id) { this.id = id; }
    public int id() { return id; }

    @Override
    public String toString() {
        return "G" + id;
    }
}
