package by.liudchyk.port.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;

/**
 * Created by Admin on 11.10.2016.
 */
public class Servant extends TimerTask{
    private static final Logger LOG = LogManager.getLogger();
    private Port port = Port.getPort();

    @Override
    public void run() {
        LOG.info("servant started");
        port.checkPort();
        LOG.info("servant ended");
    }
}

