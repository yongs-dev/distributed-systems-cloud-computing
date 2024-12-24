import java.util.List;

public class WebClientApplication {

    private static final String WORKER_ADDRESS_1 = "http://localhost:8081/task";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8082/task";

    public static void main(String[] args) {
        Aggregator aggregator = new Aggregator();
        String task1 = "10,200";
        String task2 = "123456789,100000000000000,700000002342343";

        List<String> results = aggregator.sendTasksToWorkers(List.of(WORKER_ADDRESS_1, WORKER_ADDRESS_2), List.of(task1, task2));

        for (String result : results) {
            System.out.println("result: " + result);
        }
    }
}
