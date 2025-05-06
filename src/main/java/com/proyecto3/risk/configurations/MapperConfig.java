package com.proyecto3.risk.configurations;

import com.proyecto3.risk.model.dtos.*;
import com.proyecto3.risk.model.entities.*;
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


        mapper.addMappings(new PropertyMap<Card, CardResponseDto>() {
            @Override
            protected void configure() {
                map().setId(source.getId());
                map().setImage(source.getImage());
                using(ctx -> {
                    CardType ct = ((Card) ctx.getSource()).getCTypes();
                    return ct != null ? ct.getId() : null;
                }).map(source, destination.getcType());

                using(ctx -> {
                    Country country = ((Card) ctx.getSource()).getCountry();
                    return country != null ? country.getId() : null;
                }).map(source, destination.getCountryId());
            }
        });


        // Add Border to BorderResponseDto mapping
        mapper.addMappings(new PropertyMap<Border, BorderResponseDto>() {
            @Override
            protected void configure() {
              //  map().setCountry1(modelMapper().map(source.getCountry1(), CountryResponseDto.class));
              //  map().setCountry2(modelMapper().map(source.getCountry2(), CountryResponseDto.class));
            }
        });

        return mapper;
    }
}
