package com.proyecto3.risk.service;

import com.proyecto3.risk.model.Avatars;

import java.util.List;

public interface AvatarService {

    List<Avatars> GetAllAvatars();
    Avatars GetAvatarByName(String name);
}
