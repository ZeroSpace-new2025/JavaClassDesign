package account;

public enum AccountLevel {
    GUEST(0),
    USER(1),
    ADMIN(25565);

    private final int level;

    AccountLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
