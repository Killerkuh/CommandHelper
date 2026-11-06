package de.Simon.CommandHelper.commads.Projekt;

import org.bukkit.Color;
import org.bukkit.Material;

public enum Priority {
    LOW(1, Color.GREEN),
    NORMAL(2, Color.YELLOW),
    HIGH(3, Color.ORANGE),
    CRITICAL(4, Color.RED);

    public final int value;
    public final Color color;

    Priority(int value, Color color) {

        this.value = value;
        this.color = color;
    }

    public static Priority fromValue(int value) {
        for (Priority priority : values()) {
            if (priority.value == value) {
                return priority;
            }
        }

        throw new IllegalArgumentException("Unknown priority value: " + value);
    }
    public String getColorCode() {
        return switch (this) {
            case LOW      -> "§a";
            case NORMAL   -> "§e";
            case HIGH     -> "§x§F§F§A§5§0§0";
            case CRITICAL -> "§c";
        };
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
}