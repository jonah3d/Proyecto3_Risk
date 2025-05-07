package com.proyecto3.risk.controllers.sessioncontrollers;

import com.nimbusds.jose.shaded.gson.Gson;
import com.proyecto3.risk.model.entities.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
public class PlayerSession {
    private final Player player;
    private final WebSocketSession session;

    public PlayerSession(Player player, WebSocketSession session) {
        this.player = player;
        this.session = session;
    }

    public Player getPlayer() {
        return player;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void sendMessage(String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send message to player: " + player.getId(), e);
        }
    }

    public void sendJsonMessage(Object data) {
        sendMessage(new Gson().toJson(data));
    }
}