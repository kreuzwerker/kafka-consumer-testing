package de.kreuzwerker.blogs.kafkaconsumertesting.user;

import de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public Optional<UserResponse> getUserByUserId(String userId) {
        Optional<UserEntity> userOptional = userRepository.findUserEntityByUserId(userId);
        if(userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            return Optional.of(UserResponse.builder().userId(user.getUserId()).userName(user.getUserName()).name(user.getFirstName()+' '+user.getLastName()).build());
        }
        return Optional.empty();
    }

    public void updateDb(UserEvent event) {
        if(userRepository.findUserEntityByUserId(event.getId()).isPresent()) {
            UserEntity user = userRepository.findUserEntityByUserId(event.getId()).get();
            user.setLastName(event.getLastName());
        } else {
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(event.getId());
            userEntity.setFirstName(event.getFirstName());
            userEntity.setLastName(event.getLastName());
            UserEntity saved = userRepository.save(userEntity);
            saved.setUserName(userEntity.getFirstName()+saved.getPid());
        }
    }
}
