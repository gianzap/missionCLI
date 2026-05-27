package orbitsim.util;

import orbitsim.app.MissionCLI;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogManager {

    private final Logger LOG;
    private FileHandler handler;


    public LogManager(String filename){
        try {
            handler = new FileHandler(filename, true);
            handler.setFormatter(new SimpleFormatter());

            LOG = Logger.getLogger("missionTextLog");
            LOG.addHandler(handler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void appendLogInfo(String textContent){
        LOG.log(Level.INFO,textContent);
    }

    public void closeLog(){
        handler.close();
    }


    public void appendLogWarn(String textContent) {
        LOG.log(Level.WARNING,textContent);
    }
}
