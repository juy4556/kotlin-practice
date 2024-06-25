package com.group.libraryapp.domain.loanhistory

import com.group.libraryapp.dto.book.response.BookStatResponse
import org.springframework.data.jpa.repository.JpaRepository

interface UserLoanHistoryRepository : JpaRepository<UserLoanHistory, Long> {

    fun findByBookNameAndStatus(bookName: String, status: UserLoanStatus): UserLoanHistory?
    fun findAllByStatus(status: UserLoanStatus): List<UserLoanHistory>
}
