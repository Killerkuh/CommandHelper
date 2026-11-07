    package de.Simon.CommandHelper.commads.Projekt;

    import de.steamwar.command.*;
    import de.steamwar.inventory.*;
    import de.steamwar.linkage.Linked;
    import org.bukkit.Bukkit;
    import org.bukkit.Material;
    import org.bukkit.OfflinePlayer;
    import org.bukkit.entity.Player;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.concurrent.atomic.AtomicReference;

    import static de.Simon.CommandHelper.commads.Projekt.ProjektManager.projekts;

    @Linked
    public class ProjektCommand extends SWCommand {
        public static SWItem newProjektItem = new SWItem(Material.GREEN_CONCRETE,"New Projekt");
        public static SWItem newTaskItem = new SWItem(Material.GREEN_CONCRETE, "New Task");
        public ProjektCommand() {
            super("Todo", "TodoList");
        }

        @Register("")
        public void genericCommand(@Validator Player player) {
            openProjektsViewInv(player);
        }



        public static void openProjektsViewInv(Player player) {
            List<SWListInv.SWListEntry<Projekt>> entries = new ArrayList<>(projekts.stream()
                    .map(p-> new SWListInv.SWListEntry<>(p.getItem(), p)).toList());
            entries.add(new SWListInv.SWListEntry<>(newProjektItem, null));

            SWListInv<Projekt> inv = new SWListInv<>(player,
                    "Projekts",
                    entries,
                    (clickType, projekt) -> {
                        if(projekt == null) openProjektCreateInv(player);
                        else if(clickType.isRightClick()) openProjektEdit(player, projekt);
                        else openTaskViewInv(player,projekt);
                    });
            inv.open();
        }
        private static void openProjektCreateInv(Player player) {
            AtomicReference<String> description = new AtomicReference<>("Enter A Title");
            SWInventory inv = new SWInventory(player,9, "Create Projekts");
            inv.setItem(2,new SWItem(Material.PAPER, description.get()));
            inv.setCallback(2,clickType -> {
                SWAnvilInv swAnvilInv  = new SWAnvilInv(player, "Enter A Title", description.get());
                swAnvilInv.setCallback(s->{
                    if (!s.isBlank() && projekts.stream().noneMatch(p -> p.name.equalsIgnoreCase(s))) {
                        description.set(s);
                        inv.setItem(2,new SWItem(Material.PAPER, description.get()));
                    }
                    inv.open();
                });
                swAnvilInv.open();
            });
            inv.setItem(6, new SWItem(Material.GREEN_STAINED_GLASS_PANE, "§2Create"));
            inv.setCallback(6,click-> {
                ProjektManager.createProjekt(description.get());
                openProjektsViewInv(player);
            }
            );
            inv.open();
        }


        public static void openTaskViewInv(Player player, Projekt projekt) {

            List<SWListInv.SWListEntry<Task>> entries = new ArrayList<>(projekt.getTasksByPriority().stream().map(t->
                    new SWListInv.SWListEntry<>(t.toItem(), t)).toList());
            entries.add(new SWListInv.SWListEntry<>(newTaskItem, null));
            SWListInv<Task> inv = new SWListInv<>(player, "Tasks: " + projekt.name, entries, (clickType, task) -> {
                if (task == null) {
                    openTaskCreateInv(player, projekt);
                } else if(clickType.isRightClick()) {
                    task.toggle();
                    openTaskViewInv(player, projekt);
                } else {
                    openTaskEditInv(player, task, projekt);
                }
            });
            inv.open();
        }

        public static void openTaskCreateInv(Player player, Projekt projekt) {
            AtomicReference<String> title = new AtomicReference<>("title");
            AtomicReference<String> description = new AtomicReference<>("Click to Edit Description");
            AtomicReference<Priority> priority = new AtomicReference<>(Priority.LOW);
            AtomicReference<OfflinePlayer> OffPlayer = new AtomicReference<>(player);

            SWInventory inv = new SWInventory(player, 9,"Create Task");

            inv.setItem(0, new SWItem(Material.PAPER, title.get()));
            inv.setCallback(0, clickType ->{
                SWAnvilInv swAnvilInv = new SWAnvilInv(player,"Set Title", title.get());
                swAnvilInv.setCallback(s->{
                    if(!s.isBlank() && projekt.getTasks().stream().noneMatch(t-> t.title.equalsIgnoreCase(s.trim()))) {
                        title.set(s);
                        inv.setItem(0, new SWItem(Material.PAPER, title.get()));
                    }
                    inv.open();
                });
                swAnvilInv.open();
            });

            inv.setItem(2, new SWItem(Material.PAPER, description.get()));
            inv.setCallback(2, clickType ->{
                SWAnvilInv swAnvilInv = new SWAnvilInv(player,"Set Description", description.get());
                swAnvilInv.setCallback(s->{
                    if(!s.isBlank()) {
                        description.set(s);
                        inv.setItem(2, new SWItem(Material.PAPER, description.get()));
                    }

                    inv.open();
                });
                swAnvilInv.open();
            });
            inv.setItem(4, new SWItem(priority.get().getBanner(),  priority.get().getName()));
            inv.setCallback(4,clickType -> {
                priority.set(priority.get().next());
                inv.setItem(4,new SWItem(priority.get().getBanner(),  priority.get().getName()));
            });
            inv.setItem(6, SWItem.getPlayerSkull(player).setName(player.getName()));
            inv.setCallback(6,click->{
                SWAnvilInv swAnvilInv = new SWAnvilInv(player,"Set Player", OffPlayer.get().getName());
                swAnvilInv.setCallback(s->{
                    OfflinePlayer newOffPlayer = Bukkit.getOfflinePlayer(s);
                    if(!s.isBlank() && newOffPlayer.hasPlayedBefore() ) {
                        OffPlayer.set(newOffPlayer);
                        inv.setItem(6, SWItem.getPlayerSkull(OffPlayer.get()).setName(OffPlayer.get().getName()));
                    }
                    inv.open();
                });
                swAnvilInv.open();
            });
            inv.setItem(8, new SWItem(Material.GREEN_CONCRETE, "Create Task"));
            inv.setCallback(8, click->{
                projekt.addTask(new Task(title.get(), description.get(), OffPlayer.get(), priority.get()));
                openTaskViewInv(player, projekt);
            });

            inv.open();
        }

        public static void openTaskEditInv(Player player, Task task, Projekt projekt) {
            SWInventory inv = new SWInventory(player, 9, "Edit Task");

            inv.setItem(0, new SWItem(Material.PAPER, task.title));
            inv.setCallback(0, click -> {
                SWAnvilInv anvil = new SWAnvilInv(player, "Set Title", task.title);
                anvil.setCallback(s -> {
                    if (!s.isBlank() && projekt.getTasks().stream().noneMatch(t -> t != task && t.title.equals(s))) {
                        task.title = s;
                        inv.setItem(0, new SWItem(Material.PAPER, task.title));
                    }
                    inv.open();
                });
                anvil.open();
            });

            inv.setItem(2, new SWItem(Material.PAPER, task.description));
            inv.setCallback(2, click -> {
                SWAnvilInv anvil = new SWAnvilInv(player, "Set Description", task.description);
                anvil.setCallback(s -> {
                    if (!s.isBlank()) {
                        task.description = s;
                        inv.setItem(2, new SWItem(Material.PAPER, task.description));
                    }
                    inv.open();
                });
                anvil.open();
            });

            inv.setItem(4, new SWItem(task.priority.getBanner(), task.priority.getName()));
            inv.setCallback(4, click -> {
                task.priority = task.priority.next();
                inv.setItem(4, new SWItem(task.priority.getBanner(), task.priority.getName()));
            });

            inv.setItem(6, SWItem.getPlayerSkull(task.assigned).setName(task.assigned.getName()));
            inv.setCallback(6, click -> {
                SWAnvilInv anvil = new SWAnvilInv(player, "Set Player", task.assigned.getName());
                anvil.setCallback(s -> {
                    OfflinePlayer newPlayer = Bukkit.getOfflinePlayer(s);
                    if (!s.isBlank() && newPlayer.hasPlayedBefore()) {
                        task.assigned = newPlayer;
                        inv.setItem(6, SWItem.getPlayerSkull(newPlayer).setName(newPlayer.getName()));
                    }
                    inv.open();
                });
                anvil.open();
            });

            inv.setItem(8, new SWItem(Material.RED_CONCRETE, "Delete Task"));
            inv.setCallback(8, click -> {
                SWAnvilInv anvil = new SWAnvilInv(player, "Type: " + task.title + " to confirm");
                anvil.setCallback(s -> {
                    if (s.equalsIgnoreCase(task.title)) {
                        ProjektManager.deleteTask(task);
                        openTaskViewInv(player, projekt);
                    } else {
                        inv.open();
                    }
                });
                anvil.open();
            });

            inv.open();
        }


        private static void openProjektEdit(Player player, Projekt projekt) {
            SWInventory inv = new SWInventory(player, 9,"Edit Projekt");
            inv.setItem(2, new SWItem(Material.PAPER, projekt.name));
            inv.setCallback(2,click->{
                SWAnvilInv swAnvilInv = new SWAnvilInv(player, "Enter Description", projekt.name);
                swAnvilInv.setCallback(s -> {
                    if(!s.isBlank() && projekts.stream().noneMatch(p->s.equals(p.name))) {
                        projekt.name = s;
                        inv.setItem(2, new SWItem(Material.PAPER, projekt.name));
                    }
                    inv.open();
                });
                swAnvilInv.open();
            });
            inv.setItem(6, new SWItem(Material.RED_CONCRETE, "Delete Projekt"));
            inv.setCallback(6, click->{
                SWAnvilInv swAnvilInv = new SWAnvilInv(player, "Type: "+ projekt.name.toLowerCase()+" to confirm");
                swAnvilInv.setCallback(s->{
                    if(s.equalsIgnoreCase(projekt.name)) {
                        ProjektManager.deleteProjekt(projekt);
                        openProjektsViewInv(player);
                    }
                    else {
                        inv.open();
                    }
                });
                swAnvilInv.open();
            });
            inv.open();
        }

    }