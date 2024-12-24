import networking.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Aggregator {
    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
        CompletableFuture<String>[] futures = new CompletableFuture[workersAddresses.size()];

        for (int i = 0; i < futures.length; i++) {
            String workerAddress = workersAddresses.get(i);
            String task = tasks.get(i);

            byte[] requestPayload = task.getBytes();

            futures[i] = webClient.sendTask(workerAddress, requestPayload)
                    .exceptionally(ex -> {
                        System.err.println("Error sending task to " + workerAddress + ": " + ex.getMessage());
                        return "Error";
                    });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }

        return Stream.of(futures).map(CompletableFuture::join).collect(Collectors.toList());
    }
}
