package com.simmgames.waystones.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.simmgames.waystones.permissions.Perm;
import com.simmgames.waystones.util.Default;
import com.simmgames.waystones.util.Work;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Data {
    private Logger out;
    private String dataPath;
    private Server server;
    private JavaPlugin plugin;
    public List<Waystone> AllWaystones;
    public List<WayPlayer> players;

    public Data(Logger logger, JavaPlugin plugin) {
        out = logger;
        AllWaystones = new ArrayList<Waystone>();
        players = new ArrayList<WayPlayer>();
        dataPath = plugin.getDataFolder().getAbsolutePath();
        server = plugin.getServer();
        this.plugin = plugin;
    }

    public void Save()
    {
        SaveWaystones();
        // Retire offline players
        List<String> online = new ArrayList<String>();
        for(Player p: server.getOnlinePlayers())
            online.add(p.getUniqueId().toString());
        if(players != null) {
            for (int i = players.size()-1; i >= 0; i--)
                if (!online.contains(players.get(i).UUID))
                    RetirePlayer(players.get(i));

        } else {
            out.log(Level.SEVERE, "Players list is null. Nothing is being saved for players.");
        }
        // Save remaining players
        for(String p: online)
            SavePlayer(p);
    }

    public void Load()
    {
        if(!LoadWaystones())
        {
            MakeWaystoneBackup();
        }
    }

    public WayPlayer GrabPlayer(String playerUUID)
    {
        if(playerUUID.equalsIgnoreCase(Default.UUIDOne))
            return new WayPlayer(Default.UUIDOne, "Admin");
        if(playerUUID.equalsIgnoreCase(Default.UUIDZero))
            return new WayPlayer(Default.UUIDZero, "Null");

        WayPlayer player = playerInList(playerUUID);
        if(player == null) {
            player = LoadPlayer(playerUUID);
        }
        return player;
    }

    private WayPlayer LoadPlayer(String playerUUID)
    {
        if(playerUUID.equalsIgnoreCase(Default.UUIDOne))
            return new WayPlayer(Default.UUIDOne, "Admin");
        if(playerUUID.equalsIgnoreCase(Default.UUIDZero))
            return new WayPlayer(Default.UUIDZero, "Null");

        // Load Waystones List
        String wayplayersLocation = dataPath + "/Players/";
        Gson gson = new Gson();
        String pLoc = wayplayersLocation + playerUUID + ".json";
        if(!FileExists(pLoc))
        {
            WayPlayer wp = new WayPlayer(playerUUID, "Unknown");
            out.log(Level.INFO, ChatColor.AQUA + "New WayPlayer has been created for '" + playerUUID +"'");
            players.add(wp);
            SavePlayer(playerUUID);
        }
        String json = ReadFile(pLoc);

        try {
            Type WayplayerType = new TypeToken<WayPlayer>() {
            }.getType();
            WayPlayer player = gson.fromJson(json, WayplayerType);
            if(player == null)
            {
                if(json.trim().length() > 2)
                    MakePlayerBackup(playerUUID);
                player = new WayPlayer(playerUUID, "Unknown");
            }
            if(playerInList(playerUUID) == null)
            {
                players.add((player));
            }
            return player;
        }
        catch (Exception e)
        {
            out.log(Level.WARNING, "Could not load a waystone list. Remaking it...");
            WayPlayer player = new WayPlayer(playerUUID, "Unknown");
            if(playerInList(playerUUID) == null)
            {
                players.add((player));
            }
            MakePlayerBackup(playerUUID);
            return player;
        }
    }

    public WayPlayer SavePlayer(String playerUUID)
    {
        if(playerUUID.equalsIgnoreCase(Default.UUIDOne))
            return new WayPlayer(Default.UUIDOne, "Admin");
        if(playerUUID.equalsIgnoreCase(Default.UUIDZero))
            return new WayPlayer(Default.UUIDZero, "Null");

        WayPlayer player = GrabPlayer(playerUUID);

        String wayplayersLocation = dataPath + "/Players/";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String wayplayerData = gson.toJson(player);
        WriteFile(wayplayersLocation + playerUUID + ".json", wayplayerData);
        return player;
    }

    public void RetirePlayer(String playerUUID)
    {
        WayPlayer player = SavePlayer(playerUUID);
        players.remove(player);
    }
    public void RetirePlayer(WayPlayer player)
    {
        players.remove(player);
    }

    public Waystone WaystoneFromUUID(UUID waystoneUUID) {
        return WaystoneFromUUID(waystoneUUID.toString());
    }
    public Waystone WaystoneFromUUID(String waystoneUUID) {
        for(Waystone wei: AllWaystones)
        {
            if(wei.uuid.equalsIgnoreCase(waystoneUUID))
                return wei;
        }
        return null;
    }

    public List<Waystone> WaystonesFromStringList(List<String> waystoneUUIDs)
    {
        List<Waystone> waylist = new ArrayList<>();
        for(String uuid: waystoneUUIDs)
        {
            Waystone stone = WaystoneFromUUID(uuid);
            if(stone != null)
                waylist.add(stone);
        }
        return waylist;
    }

    private WayPlayer playerInList(String uuid)
    {
        for(WayPlayer player : players)
        {
            if(player != null)
                if(player.UUID.equalsIgnoreCase(uuid))
                    return player;
        }
        return null;
    }

    private void MakePlayerBackup(String playerUUID)
    {
        out.log(Level.WARNING, "Something wrong was detected with " + playerUUID + "'s player json (Maybe it's new?). Making a backup just in case.");
        File old = new File(dataPath + "/Player/" + playerUUID + ".json");
        File backup = new File(dataPath  + "/PlayerBackup/" + playerUUID + ".0.json");
        int i = 0;
        while(backup.exists())
        {
            i++;
            backup = new File(dataPath  + "/PlayerBackup/" + playerUUID + "." + i + ".json");
        }
        old.renameTo(backup);
        out.log(Level.INFO, "Moving " + old.getName() + " to " + backup.getName() + " in case you just has a typo.");
        SavePlayer(playerUUID);
    }

    private void MakeWaystoneBackup() {
        out.log(Level.WARNING, "Something wrong was detected with the current waystone json (Maybe it's new?). Making a backup just in case.");
        File old = new File(dataPath + "/Waystones.json");
        File backup = new File(dataPath + "/Waystones.old.0.json");
        int i = 0;
        while(backup.exists())
        {
            i++;
            backup = new File(dataPath + "/Waystones.old." + i + ".json");
        }
        old.renameTo(backup);
        out.log(Level.INFO, "Renaming old config to " + backup.getName() + " in case you just has a typo.");
        SaveWaystones();
    }

    private boolean LoadWaystones()
    {
        // Load Waystones List
        String waystonesLocation = dataPath + "/Waystones.json";
        Gson gson = new Gson();
        String json = ReadFile(waystonesLocation);

        try {
            Type WaystoneListType = new TypeToken<ArrayList<Waystone>>() {
            }.getType();
            AllWaystones = gson.fromJson(json, WaystoneListType);
            if (AllWaystones == null) {
                if(json.trim().length() > 2)
                    MakeWaystoneBackup();
                AllWaystones = new ArrayList<Waystone>();
            }
            return true;
        }
        catch (Exception e)
        {
            out.log(Level.WARNING, "Could not load a waystone list. Remaking it...");
            return false;
        }
    }

    private boolean SaveWaystones()
    {
        // Save Waystones List
        String waystonesLocation = dataPath + "/Waystones.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String waystoneData = gson.toJson(AllWaystones);
        WriteFile(waystonesLocation, waystoneData);
        return true;
    }

    private boolean FileExists(String location)
    {
        File myObj = new File(location);
        return myObj.exists();
    }

    private String ReadFile(String location)
    {
        EnsureFileExists(location);
        try
        {
            BufferedReader buffer = new BufferedReader(new FileReader(location));
            String output = "";
            String current;
            while((current = buffer.readLine()) != null)
            {
                output += current + "\n";
            }
            if(buffer != null)
            {
                buffer.close();
            }
            return output;
        }
        catch (IOException e)
        {
            out.log(Level.SEVERE, "Could not read file:\n" + location);
            e.printStackTrace();
            return null;
        }
    }

    private boolean WriteFile(String location, String data)
    {
        EnsureFileExists(location);
        try {
            FileWriter myWriter = new FileWriter(location);
            myWriter.write(data);
            myWriter.close();
            return true;
        } catch (IOException e) {
            out.log(Level.WARNING, "Could not write to a file:\n" + location);
            e.printStackTrace();
            return false;
        }
    }

    private void EnsureFileExists(String location)
    {
        try {
            File myObj = new File(location);
            myObj.getParentFile().mkdir();
            myObj.createNewFile();
        } catch (IOException e) {
            out.log(Level.WARNING, "Could not get a file:\n" + location);
            e.printStackTrace();
        }
    }
}
