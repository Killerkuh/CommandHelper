package de.Simon.CommandHelper.commads.Projekt;

import de.steamwar.linkage.Linked;
import de.steamwar.linkage.api.Disable;
import de.steamwar.linkage.api.Enable;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
@Linked
public class ProjektManager implements Enable, Disable {
    public static final List<Projekt> projekts = new ArrayList<>();
    public static final File SAVE_FILE = new File(
            Bukkit.getWorlds().getFirst().getWorldFolder(),
            "projekt.save"
    );
    public static void createProjekt(String name) {
        Projekt projekt = new Projekt(name);
        projekts.add(projekt);
    }
    public static void deleteTask(Task task) {
        projekts.forEach(projekt -> projekt.getTasks().remove(task));
    }
    public static void deleteProjekt(Projekt projekt) {
        projekts.remove(projekt);
    }
    @Override
    public void enable() {
        try {
            if (!SAVE_FILE.exists() || SAVE_FILE.length() == 0) return;

            try (DataInputStream in = new DataInputStream(new FileInputStream(SAVE_FILE))) {

                int count = in.readInt();

                for (int i = 0; i < count; i++) {
                    Projekt projekt = new Projekt();
                    projekt.read(in);
                    projekts.add(projekt);
                }
            }

        } catch (IOException e) {
            throw new SecurityException("Could not Load Projekts: ", e.getCause());
        }
    }

    public void disable() {
        try (DataOutputStream out = new DataOutputStream(
                new FileOutputStream(SAVE_FILE))) {

            out.writeInt(projekts.size());

            for (Projekt projekt : projekts) {
                projekt.write(out);
            }

        } catch (IOException e) {
            throw new SecurityException("Could not Save Projekts to "+SAVE_FILE+" because:", e.getCause());
        }
    }


}
