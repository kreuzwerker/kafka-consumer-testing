package de.kreuzwerker.blogs.kafkaconsumertesting.user;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {

    String userId;
    String name;
    String userName;
}
