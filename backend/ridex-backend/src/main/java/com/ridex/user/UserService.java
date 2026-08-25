package com.ridex.user;

import com.ridex.user.dto.CreateUserRequest;
import com.ridex.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // refactored signup method
    public UserResponse createUser(CreateUserRequest request){

            if(userRepository.existsByPhone(request.phone())){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number is already in use.");
            }

           if(request.email() != null && userRepository.existsByEmail(request.email())){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already in use.");
           }

           User user = new User();
           user.setPhone(request.phone());
           user.setName(request.name());
           user.setEmail(request.email());

           return UserResponse.from(userRepository.save(user));
        }
    }


