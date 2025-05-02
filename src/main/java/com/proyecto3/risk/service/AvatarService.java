package com.proyecto3.risk.service;

import com.proyecto3.risk.model.entities.Avatars;

import java.util.List;

public interface AvatarService {

    List<Avatars> GetAllAvatars();
    Avatars GetAvatarByName(String name);
    Avatars GetAvatarById(int id);
}
