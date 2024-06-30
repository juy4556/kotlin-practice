package com.group.libraryapp.repository.user.loanhistory

import com.group.libraryapp.domain.loanhistory.QUserLoanHistory.userLoanHistory
import com.group.libraryapp.domain.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.loanhistory.UserLoanStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class UserLoanHistoryQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findBook(bookName: String, status: UserLoanStatus? = null): UserLoanHistory? {
        return queryFactory
            .select(userLoanHistory)
            .from(userLoanHistory)
            .where(
                userLoanHistory.bookName.eq(bookName),
                status?.let { userLoanHistory.status.eq(status)}
            )
            .limit(1)
            .fetchOne()
    }

    fun countBooks(status: UserLoanStatus): Long {
        return queryFactory
            .select(userLoanHistory.count())
            .from(userLoanHistory)
            .where(userLoanHistory.status.eq(status))
            .fetchOne() ?: 0L
    }
}