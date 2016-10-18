package by.liudchyk.port.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Admin on 09.10.2016.
 */
public class Port {
    private static final Logger LOG = LogManager.getLogger();
    private static AtomicBoolean instanceCreated = new AtomicBoolean(false);
    private static ReentrantLock lock = new ReentrantLock();
    private static Timer timer = new Timer();
    private static Port port;
    private final int PORT_CAPACITY = 100;
    private final int NUM_OF_BERTHS = 4;
    private boolean[] berths = new boolean[NUM_OF_BERTHS];
    private AtomicInteger boxesInPort = new AtomicInteger();
    private final Semaphore semaphore = new Semaphore(NUM_OF_BERTHS);

    private Port(){
        Random rand = new Random();
        boxesInPort.set(rand.nextInt(PORT_CAPACITY));
        LOG.info("Port capacity: "+PORT_CAPACITY+", boxes in port: "+boxesInPort);
    }

    public static Timer getTimer() {
        return timer;
    }

    public static Port getPort(){
        if(!instanceCreated.get()) {
            lock.lock();
            try {
                if (port == null) {
                    port = new Port();
                    timer.schedule(new Servant(),0,1000);
                    instanceCreated.getAndSet(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return port;
    }

    public void enterThePort(Operation operation, int numOfLoad, int numOfUnload){
        try{
            semaphore.acquire();
            int index = findFreeBerth();
            TimeUnit.MILLISECONDS.sleep(1000);
            switch (operation){
                case LOAD:
                    takeBoxes(numOfLoad);
                    break;
                case UNLOAD:
                    putBoxes(numOfUnload);
                    break;
                case BOTH:
                    putBoxes(numOfUnload);
                    takeBoxes(numOfLoad);
                    break;
                default: LOG.error("No such operation");
            }
            releaseBerth(index);
        }catch (InterruptedException e){
            LOG.error(e);
        }finally {
            semaphore.release();
        }
    }

    private int takeBoxes(int number){
        int num = boxesInPort.addAndGet(-number);
        LOG.info(num + " boxes in a port");
        return num;
    }

    private int putBoxes(int number){
        int num = boxesInPort.addAndGet(number);
        LOG.info(num + " boxes in a port");
        return num;
    }

    private int findFreeBerth(){
        int index = 0;
        try {
            lock.lock();
            for (int i = 0; i < NUM_OF_BERTHS; i++) {
                if (!berths[i]) {
                    berths[i]=true;
                    index = i;
                    break;
                }
            }
            LOG.info(index + " berth become busy");
        }finally {
            lock.unlock();
        }
        return index;    }

    private void releaseBerth(int index){
        try {
            lock.lock();
            berths[index]=false;
            LOG.info(index + " berth become opened");
        }finally {
            lock.unlock();
        }
    }

    public void checkPort(){
        if(boxesInPort.get()/Double.valueOf(PORT_CAPACITY)<0.15){
            boxesInPort.getAndSet(boxesInPort.get()+PORT_CAPACITY/2);
            LOG.info(boxesInPort.get()+" after servant");
        }
        if(boxesInPort.get()/Double.valueOf(PORT_CAPACITY)>0.85){
            boxesInPort.getAndSet(boxesInPort.get()-PORT_CAPACITY/2);
            LOG.info(boxesInPort.get()+" after servant");
        }
    }
}
