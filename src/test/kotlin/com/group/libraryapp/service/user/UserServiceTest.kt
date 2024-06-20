package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
) {

    @AfterEach
    fun clean() {
        userRepository.deleteAll()
    }

    @Test
    fun saveUserTest() {
        // given
        val request = UserCreateRequest("이름", null);

        // when
        userService.saveUser(request)

        // then
        val result = userRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("이름")
        assertThat(result[0].age).isNull()
    }

    @Test
    fun getUserTest() {
        // given
        userRepository.saveAll(
            listOf(
                User("이름1", 10),
                User("이름2", null),
            )
        )

        // when
        val result = userService.getUsers()

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("이름1")
        assertThat(result[0].age).isEqualTo(10)
        assertThat(result[1].name).isEqualTo("이름2")
        assertThat(result[1].age).isNull()
    }

    @Test
    fun updateUserNameTest() {
        // given
        val savedUser = userRepository.save(User("이전이름", 10))
        val request = UserUpdateRequest(savedUser.id!!, "새이름")

        // when
        userService.updateUserName(request)

        // then
        val result = userRepository.findById(savedUser.id).get()
        assertThat(result.name).isEqualTo("새이름")
    }

    @Test
    fun deleteUserTest() {
        // given
        val savedUser = userRepository.save(User("이름", 10))

        // when
        userService.deleteUser(savedUser.name)

        // then
        val result = userRepository.findAll()
        assertThat(result).isEmpty()
    }
}