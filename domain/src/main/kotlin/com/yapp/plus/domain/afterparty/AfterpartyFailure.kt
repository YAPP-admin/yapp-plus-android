package com.yapp.plus.domain.afterparty

enum class AfterpartyFailure {
    InvalidRequest,
    AuthenticationRequired,
    ResponseRequired,
    Forbidden,
    NotFound,
    VotingClosed,
    RevisionConflict,
    FinancialEditLocked,
    Unavailable,
}
