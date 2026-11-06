package de.Simon.CommandHelper.commads.Projekt;

import org.bukkit.Material;

public enum Priority {
    LOW(1, "§a"),
    NORMAL(2, "§e"),
    HIGH(3, "§c"),
    CRITICAL(4, "§4");

    public final int value;
    public final String colorCode;

    Priority(int value, String colorCode) {

        this.value = value;
        this.colorCode = colorCode;
    }

    public static Priority fromValue(int value) {
        for (Priority priority : values()) {
            if (priority.value == value) {
                return priority;
            }
        }

        throw new IllegalArgumentException("Unknown priority value: " + value);
    }
    public Material getBanner() {
        return switch (this) {
            case LOW -> Material.GREEN_BANNER;
            case NORMAL -> Material.YELLOW_BANNER;
            case HIGH -> Material.ORANGE_BANNER;
            case CRITICAL -> Material.RED_BANNER;
        };
    }

    public double getValue() {
        return value;
    }
    public Priority next() {
        Priority[] priorities = values();
        return priorities[(ordinal() + 1) % priorities.length];
    }
    public String getName() {
        return colorCode+name();
    }
}