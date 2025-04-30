package com.proyecto3.risk.service;

import com.proyecto3.risk.exceptions.AvatarException;
import com.proyecto3.risk.model.Avatars;
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
}
