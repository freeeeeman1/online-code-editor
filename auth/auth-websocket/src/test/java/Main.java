import auxilary.WebSocketClient;
import com.netcracker.edu.auth.handlers.Requests;

import java.util.ArrayDeque;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        WebSocketClient webSocketClient = new WebSocketClient(new ArrayDeque<>(), Requests.SUCCESSFUL_CONFIRM_EMAIL_RESET);

        webSocketClient.sendRequest();
    }
}
