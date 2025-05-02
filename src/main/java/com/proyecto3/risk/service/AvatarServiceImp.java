package com.proyecto3.risk.service;

import com.proyecto3.risk.exceptions.AvatarException;
import com.proyecto3.risk.model.entities.Avatars;
import com.proyecto3.risk.repository.AvatarRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AvatarServiceImp implements AvatarService {

    @Autowired
    private AvatarRepository avatarRepository;

    public AvatarServiceImp() {
    }

    @Override
    public List<Avatars> GetAllAvatars() {
       List<Avatars> avatarsList = avatarRepository.findAll();

       if(avatarsList == null ) {
           throw new AvatarException("No avatars found");

       }
        return avatarsList;
    }

    @Override
    public Avatars GetAvatarByName(String name) {

        if (name == null || name.isEmpty()) {
            throw new AvatarException("Avatar name cannot be null or empty");
        }

        Avatars avatar =  avatarRepository.findByName(name);

        return avatar;
    }

    @Override
    public Avatars GetAvatarById(int id) {

        if (id <= 0) {
            throw new AvatarException("Avatar ID must be greater than zero");
        }

        Avatars avatar = avatarRepository.findById(id).orElse(null);

        if (avatar == null) {
            throw new AvatarException("Avatar not found with ID: " + id);
        }

        return avatar;
    }
}
