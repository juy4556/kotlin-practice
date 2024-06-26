package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
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
        val request = BookRequest("책 제목", BookType.ECONOMY)

        // when
        bookService.saveBook(request)

        // then
        val result = bookRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("책 제목")
        assertThat(result[0].type).isEqualTo(BookType.ECONOMY)
    }

    @Test
    @DisplayName("책 대여가 정상 동작한다")
    fun loanBookTest() {
        // given
        bookRepository.save(Book.fixture("책 제목"))
        userRepository.save(User("사용자", null))
        val request = BookLoanRequest("사용자", "책 제목")

        // when
        bookService.loanBook(request)

        // then
        val result = userLoanHistoryRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].bookName).isEqualTo("책 제목")
        assertThat(result[0].user.name).isEqualTo("사용자")
        assertThat((result[0].status)).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    @DisplayName("대출되어 있는 책을 대여할 때, 실패한다")
    fun loanBookFailTest() {
        // given
        bookRepository.save(Book.fixture("책 제목"))
        val savedUser = userRepository.save(User("사용자", null))
        userLoanHistoryRepository.save(
            UserLoanHistory.fixture(
                savedUser,
                "책 제목",
                UserLoanStatus.LOANED
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
        bookRepository.save(Book.fixture("책 제목"))
        val savedUser = userRepository.save(User("사용자", null))
        userLoanHistoryRepository.save(
            UserLoanHistory.fixture(
                savedUser,
                "책 제목",
                UserLoanStatus.LOANED
            )
        )
        val request = BookReturnRequest(savedUser.name, "책 제목")

        // when
        bookService.returnBook(request)

        // then
        val result = userLoanHistoryRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    @DisplayName("대출 중인 책의 수를 반환한다")
    fun countLoanedBookTest() {
        // given
        val savedUser = userRepository.save(User("사용자", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "책 제목1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책 제목2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책 제목3", UserLoanStatus.RETURNED),
                UserLoanHistory.fixture(savedUser, "책 제목4", UserLoanStatus.RETURNED),
            )
        )

        // when
        val result = bookService.countLoanedBook()

        // then
        assertThat(result).isEqualTo(2)
    }

    @Test
    @DisplayName("분야별 책 수를 확인한다")
    fun getBookStatisticsTest() {
        // given
        bookRepository.saveAll(
            listOf(
                Book.fixture("책 제목1", BookType.ECONOMY),
                Book.fixture("책 제목2", BookType.ECONOMY),
                Book.fixture("책 제목3", BookType.COMPUTER),
                Book.fixture("책 제목4", BookType.COMPUTER),
                Book.fixture("책 제목5", BookType.COMPUTER),
                Book.fixture("책 제목6", BookType.LANGUAGE),
                Book.fixture("책 제목7", BookType.SCIENCE),
            )
        )

        // when
        val result = bookService.getBookStat()

        // then
        assertThat(result).hasSize(4)
        assertBookCount(result, BookType.ECONOMY, 2L)
        assertBookCount(result, BookType.COMPUTER, 3L)
        assertBookCount(result, BookType.LANGUAGE, 1L)
        assertBookCount(result, BookType.SCIENCE, 1L)
    }

    private fun assertBookCount(result: List<BookStatResponse>, type: BookType, count: Long) {
        result.first { it.type == type }.let {
            assertThat(it.count).isEqualTo(count)
        }
    }
}