package com.group.libraryapp.dto.user.response

import com.group.libraryapp.domain.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.User

data class UserLoanHistoryResponse(
    val name: String,
    val books: List<BookHistoryResponse>
) {
    companion object {
        fun from(user: User): UserLoanHistoryResponse {
            return UserLoanHistoryResponse(
                user.name,
                user.userLoanHistories.map(BookHistoryResponse::from)
            )
        }
    }
}

data class BookHistoryResponse(
    val name: String,
    val isReturn: Boolean,
) {
    companion object {
        fun from(history: UserLoanHistory): BookHistoryResponse {
            return BookHistoryResponse(history.bookName, history.isReturn)
        }
    }
}