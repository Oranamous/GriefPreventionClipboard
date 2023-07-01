package io.github.oranamous.griefpreventionclipboard;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public final class GriefPreventionClipboard extends JavaPlugin {

    HashMap<UUID, Clipboard> clipboards = new HashMap<>();

    Component noClaim = Component.text("You are not standing in a claim.").color(TextColor.color(255, 100, 100));
    Component noCommandPermission = Component.text("You do not have permission to manage this claim!").color(TextColor.color(255, 100, 100));
    Component confirmCommand = Component.text("Type \"/pasteperms confirm\" to confirm this action.").color(TextColor.color(255, 100, 100));
    Component copiedPermissions = Component.text("Copied permissions to your clipboard.").color(TextColor.color(50, 255, 50));
    Component pastedPermissions = Component.text("Pasted permissions from your clipboard.").color(TextColor.color(50, 255, 50));

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        clipboards.clear();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (sender instanceof Player player) {
            if (cmd.getName().equalsIgnoreCase("copyperms")) {
                this.copyPermsCommand(player);
                return true;
            } else if (cmd.getName().equalsIgnoreCase("pasteperms")) {
                boolean confirmed = false;
                if (args.length >= 1) {
                    if (args[0].toLowerCase().startsWith("confirm")) {
                        confirmed = true;
                    }
                }
                this.pastePermsCommand(player, confirmed);
                return true;
            }
        }

        return false;
    }

    private void copyPermsCommand(Player player) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
        if (claim != null) {
            if (claim.checkPermission(player, ClaimPermission.Manage, null) == null) {
                copyPerms(player, claim);
                player.sendMessage(copiedPermissions);
            } else {
                player.sendMessage(noCommandPermission);
            }
        } else {
            player.sendMessage(noClaim);
        }
    }

    private void pastePermsCommand(Player player, boolean confirmed) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);
        if (claim != null) {
            if (claim.checkPermission(player, ClaimPermission.Manage, null) == null) {
                if (confirmed) {
                    pastePerms(player, claim);
                    player.sendMessage(pastedPermissions);
                } else {
                    player.sendMessage(confirmCommand);
                }
            } else {
                player.sendMessage(noCommandPermission);
            }
        } else {
            player.sendMessage(noClaim);
        }
    }

    private void copyPerms(Player player, Claim claim) {
        Clipboard clipboard = new Clipboard();
        claim.getPermissions(clipboard.builders, clipboard.containers, clipboard.accessors, clipboard.managers);
        savePlayerClipBoard(player, clipboard);
    }

    private void pastePerms(Player player, Claim claim) {
        Clipboard clipboard = getPlayerClipBoard(player);
        clipboard.builders.forEach((builder) -> claim.setPermission(builder, ClaimPermission.Build));
        clipboard.containers.forEach((builder) -> claim.setPermission(builder, ClaimPermission.Inventory));
        clipboard.accessors.forEach((builder) -> claim.setPermission(builder, ClaimPermission.Access));
        clipboard.managers.forEach((builder) -> claim.setPermission(builder, ClaimPermission.Manage));
    }

    public Clipboard getPlayerClipBoard(Player player) {
        UUID uuid = player.getUniqueId();
        if (clipboards.containsKey(uuid)) {
            return clipboards.get(uuid);
        } else {
            return new Clipboard();
        }
    }

    public void savePlayerClipBoard(Player player, Clipboard clipboard) {
        clipboards.put(player.getUniqueId(), clipboard);
    }
}
