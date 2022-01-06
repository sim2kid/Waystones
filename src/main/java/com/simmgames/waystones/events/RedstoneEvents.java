package com.simmgames.waystones.events;

import com.simmgames.waystones.data.Data;
import org.bukkit.Server;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class RedstoneEvents implements Listener
{
    Logger out;
    Data data;
    Server server;

    public RedstoneEvents(Logger logger, Data pluginData, Server server)
    {
        out = logger;
        data = pluginData;
        this.server = server;
    }


}
