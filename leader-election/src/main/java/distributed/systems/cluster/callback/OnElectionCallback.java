package distributed.systems.cluster.callback;

import org.apache.zookeeper.KeeperException;

public interface OnElectionCallback {

    void onElectedToBeLeader() throws InterruptedException, KeeperException;

    void onWorker();
}
