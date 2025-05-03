package com.proyecto3.risk.configurations;

import com.proyecto3.risk.model.dtos.*;
import com.proyecto3.risk.model.entities.Avatars;
import com.proyecto3.risk.model.entities.Continent;
import com.proyecto3.risk.model.entities.Country;
import com.proyecto3.risk.model.entities.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();


        mapper.addMappings(new PropertyMap<UserRegistrationDto, User>() {
            @Override
            protected void configure() {
                skip(destination.getAvatar());
            }
        });

        mapper.addMappings(new PropertyMap<User, UserResponseDto>() {
            @Override
            protected void configure() {

            }
        });


        mapper.addMappings(new PropertyMap<Avatars, AvatarResponseDto>() {
            @Override
            protected void configure() {

            }
        });

        mapper.addMappings(new PropertyMap<UpdateUserDto, User>() {
            @Override
            protected void configure() {
                skip(destination.getAvatar());
            }
        });


        mapper.addMappings(new PropertyMap<Country, CountryResponseDto>() {
            @Override
            protected void configure() {

            }
        });


        mapper.addMappings(new PropertyMap<Continent, ContinentResponseDto>() {
            @Override
            protected void configure() {

            }
        });

        return mapper;
    }
}
