package distributed.systems;

import distributed.systems.cluster.callback.OnElectionAction;
import distributed.systems.cluster.management.LeaderElection;
import distributed.systems.cluster.callback.OnElectionCallback;
import distributed.systems.cluster.management.ServiceRegistry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Application implements Watcher {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private static final int DEFAULT_PORT = 8000;
    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws Exception {
        int currentServerPort = args.length == 1 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        Application application = new Application();
        ZooKeeper zooKeeper = application.connectToZookeeper();

        ServiceRegistry serviceRegistry = new ServiceRegistry(zooKeeper);
        OnElectionCallback onElectionCallback = new OnElectionAction(serviceRegistry, currentServerPort);
        LeaderElection leaderElection = new LeaderElection(zooKeeper, onElectionCallback);
        leaderElection.volunteerForLeadership();
        leaderElection.reelectLeader();

        application.run();
        application.close();
        System.out.println("Disconnected from Zookeeper, exiting application");
    }

    private ZooKeeper connectToZookeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
        return this.zooKeeper;
    }

    private void run() throws InterruptedException {
        synchronized (zooKeeper) {
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Successfully connected to Zookeeper");
                } else {
                    synchronized (zooKeeper) {
                        System.out.println("Disconnected from Zookeeper event");
                        zooKeeper.notifyAll();
                    }
                }
        }
    }
}
