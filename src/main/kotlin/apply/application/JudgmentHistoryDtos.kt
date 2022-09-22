package apply.application

import apply.domain.judgment.JudgmentHistory
import apply.domain.judgment.JudgmentStatusCode

data class JudgmentHistoryResponse(
    val testStatus: TestStatus,
    val pullRequestUrl: String,
    val commitUrl: String,
    val passCount: Int?,
    val totalCount: Int?,
    val message: String?
) {
    constructor(judgmentHistory: JudgmentHistory, pullRequestUrl: String) : this(
        TestStatus.PENDING,
        pullRequestUrl,
        toCommitUrl(pullRequestUrl, judgmentHistory.commitHash),
        judgmentHistory.result?.passCount,
        judgmentHistory.result?.totalCount,
        judgmentHistory.result?.statusCode?.message
    )

    companion object {
        fun toCommitUrl(pullRequestUrl: String, commitHash: String): String = "$pullRequestUrl/commits/$commitHash"
    }
}

data class JudgmentPassRequest(
    val requestKey: String,
    val passCount: Int,
    val totalCount: Int
)

data class JudgmentFailRequest(
    val requestKey: String,
    val statusCode: JudgmentStatusCode,
    val message: String?
)

enum class TestStatus {
    NONE, PENDING, COMPLETED
}
