package com.simmgames.waystones;

import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class Data {
    private Logger out;
    public WaystoneList waystoneList;
    public List<WayPlayer> players;

    public Data(Logger logger) {
        out = logger;
        waystoneList = new WaystoneList();
        players = new ArrayList<WayPlayer>();
    }

    public boolean Save()
    {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream("Waystones.json")));
            out.writeObject(waystoneList);
            out.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public void Load ()
    {

    }
}
