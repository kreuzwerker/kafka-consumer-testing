package de.kreuzwerker.blogs.kafkaconsumertesting.user;

import de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;

  public Optional<UserResponse> getUserByUserId(String userId) {
    Optional<UserEntity> userOptional = userRepository.findUserEntityByUserId(userId);
    if (userOptional.isPresent()) {
      UserEntity user = userOptional.get();
      return Optional.of(
          UserResponse.builder()
              .userId(user.getUserId())
              .userName(user.getUserName())
              .name(user.getFirstName() + ' ' + user.getLastName())
              .build());
    }
    return Optional.empty();
  }

  @Transactional
  public void updateDb(UserEvent event) {
    if (userRepository.findUserEntityByUserId(event.getId()).isPresent()) {
      UserEntity user = userRepository.findUserEntityByUserId(event.getId()).get();
      user.setFirstName(event.getFirstName());
      user.setLastName(event.getLastName());
      log.info("updateDB > updated user {}", user.getUserId());
    } else {
      UserEntity userEntity = new UserEntity();
      userEntity.setUserId(event.getId());
      userEntity.setFirstName(event.getFirstName());
      userEntity.setLastName(event.getLastName());
      UserEntity saved = userRepository.save(userEntity);
      saved.setUserName(userEntity.getFirstName() + saved.getPid());
      log.info(
          "updateDB > created new user {} with key {}",
          userEntity.getUserId(),
          userEntity.getPid());
    }
  }

  @Transactional
  public void deleteUser(UserEvent event) {
    if (userRepository.findUserEntityByUserId(event.getId()).isPresent()) {
      UserEntity user = userRepository.findUserEntityByUserId(event.getId()).get();
      log.info("delete user > deleting user with {} and key {}", user.getUserId(), user.getPid());
      userRepository.delete(user);
    }
  }
}
