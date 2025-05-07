package com.proyecto3.risk.controllers.sessioncontrollers;

import com.nimbusds.jose.shaded.gson.Gson;
import com.proyecto3.risk.model.entities.Player;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Player> sessionPlayers = new ConcurrentHashMap<>();

    public void registerSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void registerPlayer(WebSocketSession session, Player player) {
        sessionPlayers.put(session.getId(), player);
    }

    public Player getPlayer(WebSocketSession session) {
        return sessionPlayers.get(session.getId());
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
        sessionPlayers.remove(session.getId());
    }

    public void sendMessage(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendJsonMessage(WebSocketSession session, Object data) {
        sendMessage(session, new Gson().toJson(data));
    }
}