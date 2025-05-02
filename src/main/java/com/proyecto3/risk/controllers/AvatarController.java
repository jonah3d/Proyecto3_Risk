package com.proyecto3.risk.controllers;

import com.proyecto3.risk.exceptions.AvatarException;
import com.proyecto3.risk.model.dtos.AvatarResponseDto;
import com.proyecto3.risk.model.entities.Avatars;
import com.proyecto3.risk.service.AvatarService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/avatars")
    public ResponseEntity<List<AvatarResponseDto>> getAvatars() {

      var listOfAvatars =   avatarService.GetAllAvatars();
      if(listOfAvatars == null || listOfAvatars.isEmpty()) {
            throw new AvatarException("No avatars found");
        }

      List<AvatarResponseDto> avatarResponseDtos = listOfAvatars.stream()
                .map(avatar -> modelMapper.map(avatar, AvatarResponseDto.class))
                .toList();

        return new ResponseEntity<>(avatarResponseDtos, HttpStatus.OK);
    }

    @GetMapping("/avatars/{name}")
    public ResponseEntity<AvatarResponseDto> getAvatar(@PathVariable String name) {
        if (name == null || name.isEmpty()) {
            throw new AvatarException("Avatar name cannot be null or empty");
        }

        Avatars avatar = avatarService.GetAvatarByName(name);
        if (avatar == null) {
            throw new AvatarException("Avatar not found");
        }


        AvatarResponseDto avatarResponseDto = modelMapper.map(avatar, AvatarResponseDto.class);

        return new ResponseEntity<>(avatarResponseDto, HttpStatus.OK);
    }
}
