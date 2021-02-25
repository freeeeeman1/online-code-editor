package com.netcracker.edu.project.client;

import static com.netcracker.edu.project.client.Requests.CREATE_PROJECT;
import static com.netcracker.edu.project.client.Requests.DELETE_PROJECT;
import static com.netcracker.edu.project.client.Requests.GET_PROJECT_CONTENT;
import static com.netcracker.edu.project.client.Requests.GET_USER_CONTENT;

public class ClientMain {

    public static void main(String[] args) throws InterruptedException {
        WebSocketClient webSocketClient = new WebSocketClient();

        webSocketClient.sendRequest(GET_USER_CONTENT);
        Thread.sleep(1000);
        System.out.println(webSocketClient.getResponses());

        webSocketClient.sendRequest(CREATE_PROJECT);
        Thread.sleep(1000);
        System.out.println(webSocketClient.getResponses());

        webSocketClient.sendRequest(GET_PROJECT_CONTENT);
        Thread.sleep(1000);
        System.out.println(webSocketClient.getResponses());

        webSocketClient.sendRequest(DELETE_PROJECT);
        Thread.sleep(100);
        System.out.println(webSocketClient.getResponses());
    }
}
