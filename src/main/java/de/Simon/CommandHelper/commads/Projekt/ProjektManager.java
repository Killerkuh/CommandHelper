package de.Simon.CommandHelper.commads.Projekt;

import de.steamwar.linkage.api.Disable;
import de.steamwar.linkage.api.Enable;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ProjektManager implements Enable, Disable {
    public static final List<Projekt> projekts = new ArrayList<>();
    public static File saveFile = new File(
            Bukkit.getWorlds().getFirst().getWorldFolder(),
            "projekt.save"
    );
    public static Projekt createProjekt(String name) {
        Projekt projekt = new Projekt(name);
        projekts.add(projekt);
        return projekt;
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
            if (!saveFile.exists() || saveFile.length() == 0) return;

            try (DataInputStream in = new DataInputStream(new FileInputStream(saveFile))) {

                int count = in.readInt();

                for (int i = 0; i < count; i++) {
                    Projekt projekt = new Projekt();
                    projekt.read(in);
                    projekts.add(projekt);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disable() {
        try (DataOutputStream out = new DataOutputStream(
                new FileOutputStream(saveFile))) {

            out.writeInt(projekts.size());

            for (Projekt projekt : projekts) {
                projekt.write(out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
