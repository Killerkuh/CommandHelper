package de.Simon.CommandHelper;

import de.steamwar.linkage.AbstractLinker;
import de.steamwar.linkage.SpigotLinker;
import de.steamwar.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandHelper extends JavaPlugin {

    public SpigotLinker spigotLinker;
    public static Message message;

    @Override
    public void onEnable() {
        message = new Message("de.Simon.CommandHelper", getClassLoader());
        spigotLinker = new SpigotLinker(this, message) {
            @Override
            protected void linkObject(Object any) {
                super.linkObject(any);
            }
        };
        try {
            spigotLinker.link();
        } catch (AbstractLinker.LinkException e) {
            e.printStackTrace();
            Bukkit.shutdown();
        }
    }

    @Override
    public void onDisable() {
        spigotLinker.unlink();
    }
}
