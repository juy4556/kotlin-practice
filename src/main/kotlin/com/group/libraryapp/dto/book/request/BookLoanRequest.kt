package com.group.libraryapp.dto.book.request

import lombok.AllArgsConstructor

@AllArgsConstructor
class BookLoanRequest (
    val userName: String,
    val bookName: String
)
