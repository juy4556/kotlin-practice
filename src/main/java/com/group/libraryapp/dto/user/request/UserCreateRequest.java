package com.group.libraryapp.dto.user.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserCreateRequest {

  private String name;
  private Integer age;

  public String getName() {
    return name;
  }

  public Integer getAge() {
    return age;
  }

}
