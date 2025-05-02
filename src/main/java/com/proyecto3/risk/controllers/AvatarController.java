package com.proyecto3.risk.controllers;

import com.proyecto3.risk.exceptions.AvatarException;
import com.proyecto3.risk.model.entities.Avatars;
import com.proyecto3.risk.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    @GetMapping("/avatars")
    public List<Avatars> getAvatars() {

      var listOfAvatars =   avatarService.GetAllAvatars();
      if(listOfAvatars == null || listOfAvatars.isEmpty()) {
            throw new AvatarException("No avatars found");
        }


        return listOfAvatars;
    }

    @GetMapping("/avatars/{name}")
    public Avatars getAvatar(@PathVariable String name) {
        if (name == null || name.isEmpty()) {
            throw new AvatarException("Avatar name cannot be null or empty");
        }

        Avatars avatar = avatarService.GetAvatarByName(name);
        if (avatar == null) {
            throw new AvatarException("Avatar not found");
        }

        return avatar;
    }
}
