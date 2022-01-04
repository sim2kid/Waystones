package com.simmgames.waystones.events;

import com.simmgames.waystones.data.Config;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.util.Default;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import sun.awt.ConstrainableGraphics;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.currentTimeMillis;

public class TeleportEffects {
    Data data;
    Server server;
    JavaPlugin plugin;
    Random ran;
    Logger out;

    public TeleportEffects(JavaPlugin plugin, Data data)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
        ran = new Random();
        this.data = data;
        out = plugin.getLogger();
    }
    public void RunTeleportEvents(Waystone waystone, Player player)
    {
        this.RunTeleportEvents(waystone, player, 1);
    }

    public void RunTeleportEvents(Waystone waystone, Player player, int karmaToAdd)
    {
        // Update Karma
        WayPlayer wp = data.GrabPlayer(player.getUniqueId().toString());
        for(int i = 0; i < karmaToAdd; i++)
            wp.OldTeleports.add(currentTimeMillis());

        // Short Circuit
        WaystoneShortCircuit(waystone, player);

        // Endermite
        SpawnEndermites(player);

        // TP Sickness
    }

    public double GetPlayersKarma(Player p) { return GetPlayersKarma(p.getUniqueId().toString()); }
    public double GetPlayersKarma(String playerUUID) {
        WayPlayer wp = data.GrabPlayer(playerUUID);
        long currentTime = currentTimeMillis();
        long hour = 60 * 60 * 1000;
        for(int i = wp.OldTeleports.size()-1; i >= 0; i--)
            if(wp.OldTeleports.get(i).longValue() + hour < currentTime)
                wp.OldTeleports.remove(i);
        data.SavePlayer(playerUUID);
        int TPs = wp.OldTeleports.size();
        if(Config.UseKarma())
        {
            double Karma = TPs / Config.KarmaLimit();
            Karma *= Config.KarmaStrength();
            return Karma;
        }
        return 1;
    }

    public void WaystoneShortCircuit(Waystone stone, Player cause)
    {
        if(!Config.UseShortCircuit())
            return;
        double shortChance = Config.ShortCircuitChance();
        if(Config.ShortCircuitIgnoreAdmin() && stone.owner.equalsIgnoreCase(Default.UUIDOne))
            return;
        if(stone.timeLeftUntilFunctional() > 0)
            return;
        if(ran.nextDouble() < shortChance)
        {
            // Run short circuit
            double duration = Config.ShortCircuitDuration() + (Config.ShortCircuitSpread() * ((2.0 * ran.nextDouble()) - 1));
            if(Config.UseKarma())
                duration += Config.ShortCircuitSpread() * Math.pow(GetPlayersKarma(cause), 1.5);
            out.log( Level.INFO, duration + " ");

            if(duration < 10)
                return;

            stone.setCharge((int)duration);
            // Do effects here
            stone.location.getLocation(server).getWorld().spawnParticle(Particle.ELECTRIC_SPARK,
                    stone.location.getLocation(server).add(0.5,0.5,0.5), 400, 1, 1, 1);
            new PlaySoundAfterTime(stone.location.getLocation(server).getWorld(),
                    stone.location.getLocation(server), Sound.ITEM_TOTEM_USE, SoundCategory.BLOCKS,
                    0.3f, 0f).runTaskLater(plugin, (int)(5));
            for(int i = 0; i < 10; i++)
            {
                new PlaySoundAfterTime(stone.location.getLocation(server).getWorld(),
                        stone.location.getLocation(server), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS,
                        4f, 0.6f).runTaskLater(plugin, (int)(i * 20 * 0.25));
            }
        }
    }

    public void SpawnEndermites(Player cause)
    {
        if(!Config.SpawnEndermites())
            return;
        double chance = Config.EndermiteChance() * GetPlayersKarma(cause);
        if(ran.nextDouble() < chance)
        {
            //spawn endermite
            cause.getWorld().spawnEntity(cause.getLocation(), EntityType.ENDERMITE);
        }
    }
}
