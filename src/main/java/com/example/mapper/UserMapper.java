package com.example.mapper;

import com.example.config.MapperConfig;
import com.example.dto.user.UserResponseDto;
import com.example.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);
}
