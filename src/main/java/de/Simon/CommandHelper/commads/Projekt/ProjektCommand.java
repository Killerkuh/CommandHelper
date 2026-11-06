    package de.Simon.CommandHelper.commads.Projekt;

    import de.Simon.CommandHelper.CommandHelper;
    import de.steamwar.command.*;
    import de.steamwar.inventory.*;
    import de.steamwar.linkage.Linked;
    import org.bukkit.Bukkit;
    import org.bukkit.Material;
    import org.bukkit.OfflinePlayer;
    import org.bukkit.entity.Player;

    import java.util.ArrayList;
    import java.util.List;

    import static de.Simon.CommandHelper.commads.Projekt.ProjektManager.projekts;

    @Linked
    public class ProjektCommand extends SWCommand {
        public ProjektCommand() {
            super("Todo", "TodoList");
        }

        @Register("")
        public void genericCommand(@Validator Player player) {
            openProjektsViewInv(player);
        }
        @Register("new")
        public void newProjekt(@Validator Player player, String name) {
            if (projekts.stream().anyMatch(projekt -> projekt.name.equalsIgnoreCase(name))) {
                CommandHelper.message.send("todo.alreadyexits", player, name);
                return;
            }

            ProjektManager.createProjekt(name);
            openProjektsViewInv(player);
        }


        public static SWItem generateProjektItem(Projekt projekt, Player player) {
            return new SWItem(Material.PAPER, projekt.name).setLore(
                    CommandHelper.message.parse("todo.projekt.inv.item.lore.1", player),
                    CommandHelper.message.parse("todo.projekt.inv.item.lore.2", player));
        }
        public static void openProjektsViewInv(Player player) {
            new SWListInv<>(
                    player,
                    CommandHelper.message.parse("todo.projekt.inv.title", player),
                    projekts.stream()
                            .map(projekt -> new SWListInv.SWListEntry<>(
                                    generateProjektItem(projekt, player),
                                    projekt
                            ))
                            .toList(),
                    (type, projekt) -> {
                        if (type.isRightClick()) {
                            openProjektEdit(player, projekt,()->openProjektsViewInv(player));

                        } else{
                            openTaskViewInv(player, projekt);
                        }
                    }
            ).open();
        }

        public static SWItem generateTaskItem(Task task, Player player) {
            return SWItem.getPlayerSkull(task.assigned.getName())
                    .setEnchanted(task.completed)
                    .setName(task.priority.getColorCode()+task.description);
        }

        public static void openTaskViewInv(Player player, Projekt projekt) {
            List<SWListInv.SWListEntry<Task>> entries = new ArrayList<>();

            entries.add(new SWListInv.SWListEntry<>(new SWItem(Material.GREEN_CONCRETE, "Add Task"), null));
            entries.addAll(projekt.getTasks().stream().map(task ->
                    new SWListInv.SWListEntry<>(generateTaskItem(task, player), task)
            ).toList());

            new SWListInv<Task>(player, projekt.name, entries, (clickType, task) -> {
                if (task == null) {
                    openTaskCreateInv(player, projekt);
                    return;
                }

                if (clickType.isRightClick()) {
                    task.completed = !task.completed;
                    openTaskViewInv(player, projekt);
                } else {
                    openTaskEditInv(player, task, () -> openTaskViewInv(player, projekt));
                }
            }).open();
        }

        public static void openTaskCreateInv(Player player, Projekt projekt) {
            Task task = new Task(player, "", Priority.NORMAL);
            SWInventory inv = new SWInventory(player, 9, "Create Task");

            inv.setItem(2, new SWItem(Material.PAPER, "Description", type -> {
                SWAnvilInv anvilInv = new SWAnvilInv(player, "Enter Description");
                anvilInv.setCallback(s -> {
                    if (!s.isBlank()) {
                        task.description = s;
                    }
                    openTaskCreateInv(player, projekt);
                });
                anvilInv.open();
            }));

            inv.setItem(4, new SWItem(
                    task.getPriority().getBanner(),
                    task.getPriority().getColorCode() + task.getPriority().name(),
                    clickType -> {
                        task.priority = task.priority.next();
                        openTaskCreateInv(player, projekt);
                    }));

            inv.setItem(6, SWItem.getPlayerSkull(task.assigned)
                    .setName(task.assigned.getName())
                    .setCallback(clickType -> {
                        SWAnvilInv anvilInv = new SWAnvilInv(player, "Enter Playername");
                        anvilInv.setCallback(s -> {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
                            if (offlinePlayer.hasPlayedBefore()) {
                                task.assigned = offlinePlayer;
                            }
                            openTaskCreateInv(player, projekt);
                        });
                        anvilInv.open();
                    }));

            inv.setItem(8, new SWItem(Material.GREEN_CONCRETE, "Create Task").setCallback(clickType -> {
                if (task.description.isBlank()) {
                    return;
                }

                projekt.getTasks().add(task);
                openTaskViewInv(player, projekt);
            }));

            inv.open();
        }

        public static void openTaskEditInv (Player player, Task task, Runnable onClose) {

            SWInventory inv = new SWInventory(player, 9, CommandHelper.message.parse("todo.task.inv.edit.title", player));
            setDescriptionEditorItem(inv, player, task);
            setPriorityEditorItem(inv, player, task);
            setAssingnedEditorItem(inv, player,task);
            setDeleteTaskItem(inv, player, task, onClose );
            inv.addCloseRunnable(onClose);
            inv.open();

        }
        private static void setDescriptionEditorItem(SWInventory inv, Player player, Task task) {
            inv.setItem(2, new SWItem(Material.PAPER, "Description", type -> {
                SWAnvilInv AInv = new SWAnvilInv(player,"Enter New Description", task.description);
                AInv.setCallback(s-> {
                    task.description = s;
                    setDescriptionEditorItem(inv, player, task);
                    inv.open();
                });
                AInv.open();

            }));
        }
        private static void setPriorityEditorItem(SWInventory inv, Player player, Task task) {
            inv.setItem(4,new SWItem(task.getPriority().getBanner(), task.getPriority().getColorCode()+task.getPriority().name(), clickType -> {
                task.priority = task.priority.next();
                setPriorityEditorItem(inv, player, task);
            }));
        }
        private static void setAssingnedEditorItem(SWInventory inv, Player player, Task task) {
            SWItem head = SWItem.getPlayerSkull(task.assigned)
                    .setName(task.assigned.getName())
                    .setCallback(clickType->{
                        SWAnvilInv AInv = new SWAnvilInv(player,"Enter Playername");
                        AInv.setCallback(s -> {
                            OfflinePlayer OPlayer = Bukkit.getOfflinePlayer(s);
                            if(OPlayer.hasPlayedBefore()) {
                                task.assigned = OPlayer;
                                setAssingnedEditorItem(inv, player, task);
                            }
                            inv.open();
                        });
                        AInv.open();
                    });

            inv.setItem(6,head);
        }
        private static void setDeleteTaskItem(SWInventory inv, Player player, Task task, Runnable onclose) {
            inv.setItem(8, new SWItem(Material.RED_CONCRETE,"Delete Task").setCallback(clickType -> {
                SWAnvilInv AInv = new SWAnvilInv(player, "Type: \"Confirm\" to Delete this task");
                AInv.setCallback(s->{
                    if(s.equalsIgnoreCase("confirm")) {
                        ProjektManager.deleteTask(task);
                        onclose.run();
                    }
                });
                AInv.open();
            }));
        }

        private static void openProjektEdit(Player player, Projekt projekt, Runnable onCLose) {
            SWInventory inv = new SWInventory(player, 9, "Edit Projects");
            inv.addCloseRunnable(onCLose);
            //AIR AIR PAper->Name AIR AIR AIR RED_GLASS-> DELTE AIR AIR
            setProjektNameEditorItem(inv,player,projekt);
            setProjektDeleteEditorItem(inv,player,projekt);
            inv.open();

        }
        private static void setProjektNameEditorItem(SWInventory inv, Player player, Projekt projekt) {
            inv.setItem(3,new SWItem(Material.PAPER, "Change Name").setCallback(type->{
                SWAnvilInv Anvilinv = new SWAnvilInv(player, "Change Name");
                Anvilinv.setCallback(s -> {
                    projekt.name = s;
                    setProjektNameEditorItem(inv, player, projekt);
                    inv.open();
                });
                Anvilinv.open();
            }));
        }
        private static void setProjektDeleteEditorItem(SWInventory inv, Player player, Projekt projekt){
            inv.setItem(7, new SWItem(Material.RED_CONCRETE, "Delete Projekt").setCallback(clickType -> {
                SWAnvilInv AInv = new SWAnvilInv(player, "Type: "+projekt.name+" to Delete Projekt");
                AInv.setCallback(s -> {
                    if (s.equalsIgnoreCase(projekt.name)) {
                        ProjektManager.deleteProjekt(projekt);
                        openProjektsViewInv(player);
                    } else {
                        inv.open();
                    }
                });
                AInv.open();
            }));

        }

    }