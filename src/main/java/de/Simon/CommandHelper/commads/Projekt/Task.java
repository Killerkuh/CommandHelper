package de.Simon.CommandHelper.commads.Projekt;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Task implements BinarySerializable {
    public OfflinePlayer assigned;
    public String description;
    public Priority priority;
    public boolean completed;

    /**
     *
     * @param assigned Player that is Assigned
     * @param task A String
     * @param priority The Priority
     */
    public Task(OfflinePlayer assigned, String task, Priority priority) {
        this.assigned = assigned;
        this.description = task;
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

    public void complete() {
        this.completed = true;
    }

    public Priority getPriority() {
        return priority;
    }

    public void write(DataOutputStream out) throws IOException, IOException {
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
        priority = Priority.fromValue(in.readUnsignedByte());

            if (in.readBoolean()) {
                assigned = Bukkit.getPlayer(new UUID(in.readLong(), in.readLong()));
            }

        completed = in.readBoolean();
    }
}
