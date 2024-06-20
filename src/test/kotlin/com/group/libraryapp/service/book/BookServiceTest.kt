package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {
    @BeforeEach
    fun clean() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
        userLoanHistoryRepository.deleteAll()
    }

    @Test
    @DisplayName("책 등록이 정상 동작한다")
    fun saveBookTest() {
        // given
        val request = BookRequest("책 제목")

        // when
        bookService.saveBook(request)

        // then
        val result = bookRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("책 제목")
    }

    @Test
    @DisplayName("책 대여가 정상 동작한다")
    fun loanBookTest() {
        // given
        bookRepository.save(Book("책 제목"))
        userRepository.save(User("사용자", null))
        val request = BookLoanRequest("사용자", "책 제목")

        // when
        bookService.loanBook(request)

        // then
        val result = userLoanHistoryRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].bookName).isEqualTo("책 제목")
        assertThat(result[0].user.name).isEqualTo("사용자")
        assertThat((result[0].isReturn)).isFalse
    }

    @Test
    @DisplayName("대출되어 있는 책을 대여할 때, 실패한다")
    fun loanBookFailTest() {
        // given
        bookRepository.save(Book("책 제목"))
        val savedUser = userRepository.save(User("사용자", null))
        userLoanHistoryRepository.save(
            UserLoanHistory(
                savedUser,
                "책 제목",
                false
            )
        )
        val request = BookLoanRequest("사용자", "책 제목")

        // when & then
        val message = assertThrows<IllegalArgumentException> {
            bookService.loanBook(request)
        }.message
        assertThat(message).isEqualTo("이미 대여된 책입니다")
    }

    @Test
    @DisplayName("책 반납이 정상 동작한다")
    fun returnBookTest() {
        // given
        bookRepository.save(Book("책 제목"))
        val savedUser = userRepository.save(User("사용자", null))
        userLoanHistoryRepository.save(
            UserLoanHistory(
                savedUser,
                "책 제목",
                false
            )
        )
        val request = BookReturnRequest(savedUser.name, "책 제목")

        // when
        bookService.returnBook(request)

        // then
        val result = userLoanHistoryRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].isReturn).isTrue
    }
}