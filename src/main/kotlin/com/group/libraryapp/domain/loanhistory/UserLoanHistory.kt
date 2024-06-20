package com.group.libraryapp.domain.loanhistory

import com.group.libraryapp.domain.user.User
import javax.persistence.*

@Entity
class UserLoanHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,
    val bookName: String,
    var isReturn: Boolean,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    public fun doReturn() {
        this.isReturn = true
    }
}
