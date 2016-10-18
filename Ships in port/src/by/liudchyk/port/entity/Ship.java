package by.liudchyk.port.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * Created by Admin on 09.10.2016.
 */
public class Ship implements Runnable{
    private static final Logger LOG = LogManager.getLogger();
    private Port port = Port.getPort();
    private int shipId;
    private final int CAPACITY = 10;
    private int boxes;

    public Ship(int shipId, int boxes) {
        this.shipId = shipId;
        if (boxes < 0) {
            this.boxes = 0;
        }
        if(boxes>CAPACITY){
            this.boxes = CAPACITY;
        }else {
            this.boxes = boxes;
        }
    }

    @Override
    public String toString() {
        return "Ship{" +
                "boxes=" + boxes +
                ", shipId=" + shipId +
                '}';
    }

    @Override
    public void run() {
        Random rand = new Random();
        Operation action = Operation.generateOperation();
        switch (action){
            case LOAD:
                Integer numOfContainers = rand.nextInt(CAPACITY - boxes);
                LOG.info(shipId + " ship wants to load " + numOfContainers + " boxes");
                port.enterThePort(Operation.LOAD, numOfContainers, 0);
                boxes += numOfContainers;
                break;
            case UNLOAD:
                numOfContainers = rand.nextInt(boxes);
                LOG.info(shipId + " ship wants to unload " + numOfContainers + " boxes");
                port.enterThePort(Operation.UNLOAD, 0, numOfContainers);
                boxes -= numOfContainers;
                break;
            case BOTH:
                numOfContainers = rand.nextInt(boxes);
                int numOfLoad = rand.nextInt(CAPACITY - boxes + numOfContainers);
                LOG.info(shipId + " ship wants to unload " + numOfContainers + " boxes and to load " + numOfLoad);
                port.enterThePort(Operation.BOTH, numOfLoad, numOfContainers);
                boxes += numOfLoad;
                boxes -= numOfContainers;
                break;
            default:
                LOG.error("Some unknown operation");
        }
        LOG.info(shipId+"ship has "+boxes+" boxes.\n"+"end of run()");
    }

}
