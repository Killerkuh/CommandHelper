package de.Simon.CommandHelper.commads.Projekt;

import de.steamwar.inventory.SWItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task implements BinarySerializable {

    public static final int MAX_LENGTH_LORE = 30;

    public String title;
    public OfflinePlayer assigned;
    public String description;
    public Priority priority;
    public boolean completed;

    /**
     * Used for new Creation
     * @param title A String representing the title
     * @param description A String
     * @param assigned An OfflinePlayer which the task is Assigned to
     * @param priority The Priority of the Task
     */
    public Task(String title, String description, OfflinePlayer assigned, Priority priority) {
        this.title = title;
        this.assigned = assigned;
        this.description  = description;
        this.priority = priority;
        completed = false;
    }

    /**
     * Used for Loading from File
     */
    public Task () {

    }

    public boolean isCompleted() {
        return completed;
    }

    public void toggle() {
        this.completed = !completed;
    }

    public Priority getPriority() {
        return priority;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(title);
        out.writeUTF(description);
        out.writeByte(priority.value);

        out.writeBoolean(assigned != null);

        if (assigned != null) {
            out.writeLong(assigned.getUniqueId().getMostSignificantBits());
            out.writeLong(assigned.getUniqueId().getLeastSignificantBits());
        }

        out.writeBoolean(completed);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        description = in.readUTF();
        description = in.readUTF();
        priority = Priority.fromValue(in.readUnsignedByte());

            if (in.readBoolean()) {
                assigned = Bukkit.getOfflinePlayer(new UUID(in.readLong(), in.readLong()));
            }

        completed = in.readBoolean();
    }

    public static List<String> splitLore(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : text.split("\\s+")) {
            if (line.length() + word.length() + (line.isEmpty() ? 0 : 1) > maxLength) {
                lines.add(line.toString());
                line.setLength(0);
            }

            if (!line.isEmpty()) line.append(" ");
            line.append(word);
        }

        if (!line.isEmpty()) lines.add(line.toString());

        return lines;
    }

    public SWItem toItem() {

        return SWItem.getPlayerSkull(assigned)
                .setName(priority.colorCode +title)
                .setEnchanted(completed)
                .setLore(splitLore(description, MAX_LENGTH_LORE));
    }
}
