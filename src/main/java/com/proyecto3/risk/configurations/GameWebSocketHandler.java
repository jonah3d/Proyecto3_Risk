package com.proyecto3.risk.configurations;

import com.proyecto3.risk.controllers.GameController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private GameController gameController;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        gameController.handleConnect(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        gameController.handleDisconnect(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        gameController.handleMessage(session, message.getPayload());
    }
}