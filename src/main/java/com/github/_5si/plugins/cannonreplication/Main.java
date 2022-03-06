package com.github._5si.plugins.cannonreplication;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.LinkedHashSet;
import java.util.Set;

public class Main extends JavaPlugin implements CommandExecutor {

    public void onEnable() {
        getCommand("cannonshot").setExecutor(this);
    }

    int addOrRemoveTicks = 1; // change this delay based on how the shot is at the wall, this is here just incase i got something wrong by 1 or 2 ticks, everything is relative to the "1rev"

    public void createNewCannonShot(final Location loc, final double motX, final double motY, final double motZ, final boolean slabbust) {
        final Shot first = new Shot();
        final Shot second = new Shot();
        final World world = loc.getWorld();
        TNTPrimed tnt = world.spawn(loc, TNTPrimed.class);// spawn 1rev tnt in even though i hate 1rev :/
        FallingBlock sand;
        tnt.setFuseTicks(10 + addOrRemoveTicks + 4); // 10 gametcks + extra 1 or 0 (depends on if i can do math or not) + 4 for the 2nd shot
        first.tnt.add(tnt);
        for (int i = 0; i < 313; ++i) { // add the hammer!
            tnt = world.spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(14 + addOrRemoveTicks + 4);
            second.tnt.add(tnt);
        }
        int hammer = 14 + addOrRemoveTicks + 4;
        if (slabbust) { // add the slabbust to the shot wahoo!
            for (int i = 0; i < 255; ++i) {
                tnt = world.spawn(loc, TNTPrimed.class);
                tnt.setFuseTicks(14 + addOrRemoveTicks + 4);
                first.tnt.add(tnt);
            }
        }

        for (int i = 0; i < 255; ++i) { // add sand :D
            sand = world.spawnFallingBlock(loc, 12, (byte) 0);
            first.sand.add(sand);
        }

        tnt = world.spawn(loc, TNTPrimed.class); // add 1stopper tnt for hammer
        tnt.setFuseTicks(hammer + 6); // set delay
        second.tnt.add(tnt);

        for (int i = 0; i < 6; ++i) { // os sand
            sand = world.spawnFallingBlock(loc, 12, (byte) 0);
            second.sand.add(sand);
        }

        for (int i = 0; i < 10; ++i) { // os hammer
            tnt = world.spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7);
            second.tnt.add(tnt);
        }

        for (int i = 0; i < 4; ++i) { // scatter!
            tnt = world.spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7 + 10);
            second.tnt.add(tnt);
        }

        for (int i = 0; i < 6; ++i) { // beginning of webnuke
            tnt = world.spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7 + 10 + 3);
            second.tnt.add(tnt);
        }

        for (int i = 0; i < 350; ++i) { // rest of webnuke
            tnt = world.spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7 + 10 + 3);
            second.tnt.add(tnt);
            tnt = world.spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7 + 10 + 3);
            first.tnt.add(tnt);
        }

        sand = world.spawnFallingBlock(loc, 12, (byte) 0); // finally, oneshot sand!
        second.sand.add(sand);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> { // delay by 1 cus it looks nicer to use this idk lmao
            first.tnt.forEach((entity) -> entity.setVelocity(new Vector(motX, motY, motZ)));
            first.sand.forEach((entity) -> entity.setVelocity(new Vector(motX, motY, motZ)));
        }, 1L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> { // delay by 4 later than first shot movement.
            second.tnt.forEach((entity) -> entity.setVelocity(new Vector(motX, motY, motZ)));
            second.sand.forEach((entity) -> entity.setVelocity(new Vector(motX, motY, motZ)));
        }, 5L);

    }

    static class Shot {
        final Set<FallingBlock> sand = new LinkedHashSet<>();
        final Set<TNTPrimed> tnt = new LinkedHashSet<>();
    }

    @Override
    public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
        if (var1 instanceof Player) {
            if (var4.length >= 3) {
                double x, y, z;
                try {
                    x = Double.parseDouble(var4[0]);
                    y = Double.parseDouble(var4[1]);
                    z = Double.parseDouble(var4[2]);
                } catch (Throwable nonNumeric) {
                    var1.sendMessage("not numeric");
                    return false;
                }
                boolean doslabbust = false;
                try {
                    doslabbust = Boolean.parseBoolean(var4[3]);
                } catch (Throwable ignored) {

                }
                createNewCannonShot(((Player) var1).getLocation(), x, y, z, doslabbust);
            } else {
                var1.sendMessage("not enough args");
            }
        } else {
            var1.sendMessage("you are not a player");
        }
        return false;
    }

}
