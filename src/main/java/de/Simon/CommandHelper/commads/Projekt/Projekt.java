
package de.Simon.CommandHelper.commads.Projekt;

import de.steamwar.inventory.SWItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Projekt implements BinarySerializable  {


    public String name;
    private final List<Task> tasks = new ArrayList<>();

    /**
     * Should be Used when Creating a new Projekt
     * @param name the Name of The Projekt
     */
    public Projekt(String name) {
        this.name = name;
    }

    /**
     * Is Used for Loading from File
     */
    public Projekt() {

    }

    public boolean isCompleted() {
        return !tasks.isEmpty() && tasks.stream().allMatch(Task::isCompleted);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    public double getProgress() {
        if (tasks.isEmpty()) return 0.0;

        long completed = tasks.stream()
                .filter(Task::isCompleted)
                .count();

        return (double) completed / tasks.size();
    }

    public List<Task> getTasksByPriority() {
        return tasks.stream()
                .sorted((a, b) -> Double.compare(
                        b.getPriority().getValue(),
                        a.getPriority().getValue()
                ))
                .toList();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeInt(tasks.size());

        for (Task task : tasks) {
            task.write(out);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        name = in.readUTF();
        int taskCount = in.readInt();
        for (int i = 0; i < taskCount; i++) {
            Task task = new Task();
            task.read(in);
            tasks.add(task);
        }
    }

    public SWItem getItem() {
        SWItem item = new SWItem();
        item.setItemStack(new ItemStack(Material.PAPER));
        item.setName(name+ ": "+getProgress()*100+"%");
        item.setEnchanted(isCompleted());
        item.setLore(
                "Right Click to Edit Projekt" ,
                "Left click to see task");
        return item;
    }
}
