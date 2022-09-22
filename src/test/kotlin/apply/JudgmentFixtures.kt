package apply

import apply.application.JudgmentFailRequest
import apply.application.JudgmentHistoryResponse
import apply.application.JudgmentPassRequest
import apply.application.TestStatus
import apply.application.github.CommitResponse
import apply.application.judgmentserver.JudgmentRequest
import apply.domain.judgment.Commit
import apply.domain.judgment.JudgmentFailCause
import apply.domain.judgment.JudgmentHistory
import apply.domain.judgment.JudgmentItem
import apply.domain.judgment.JudgmentResult
import apply.domain.judgment.JudgmentStatusCode
import apply.domain.judgment.JudgmentType
import apply.domain.judgment.ProgrammingLanguage
import java.time.LocalDateTime

const val COMMIT_HASH = "commit-hash"
const val COMMIT_URL =
    "https://github.com/woowacourse/service-apply/pull/367/commits/eeb43de3f53f4bec08e7d63f07badb66c12dfa31"
const val REQUEST_KEY = "request-key"
const val TEST_NAME = "kotlin-baseball-precourse"

fun createJudgmentHistory(
    userId: Long = 1L,
    missionId: Long = 1L,
    requestKey: String = REQUEST_KEY,
    commitHash: String = COMMIT_HASH,
    judgmentType: JudgmentType = JudgmentType.EXAMPLE,
    result: JudgmentResult = createJudgmentHistoryResult(),
    id: Long = 0L
): JudgmentHistory {
    return JudgmentHistory(userId, missionId, requestKey, commitHash, judgmentType, result, id)
}

fun createJudgmentHistoryResult(
    correctCount: Int = 10,
    totalCount: Int = 10,
    statusCode: JudgmentStatusCode = JudgmentStatusCode.OK
): JudgmentResult {
    return JudgmentResult(correctCount, totalCount, statusCode)
}

fun createJudgmentHistoryResponse(
    testStatus: TestStatus = TestStatus.PENDING,
    pullRequestUrl: String = PULL_REQUEST_URL,
    commitUrl: String = COMMIT_URL,
    passCount: Int? = null,
    totalCount: Int? = null,
    message: String? = null
): JudgmentHistoryResponse {
    return JudgmentHistoryResponse(testStatus, pullRequestUrl, commitUrl, passCount, totalCount, message)
}

fun createCommit(
    commitHash: String = COMMIT_HASH,
    pullRequestUrl: String = PULL_REQUEST_URL
): Commit {
    return Commit(commitHash, pullRequestUrl)
}

fun createCommitResponse(
    hash: String = COMMIT_HASH,
    date: LocalDateTime = LocalDateTime.now()
): CommitResponse {
    return CommitResponse(hash, date)
}

fun createJudgmentRequest(
    testName: String = TEST_NAME,
    judgmentType: JudgmentType = JudgmentType.EXAMPLE,
    pullRequestUrl: String = PULL_REQUEST_URL,
    commitHash: String = COMMIT_HASH,
    programmingLanguage: ProgrammingLanguage = ProgrammingLanguage.KOTLIN
): JudgmentRequest {
    return JudgmentRequest(testName, judgmentType, pullRequestUrl, commitHash, programmingLanguage)
}

fun createJudgmentItem(
    missionId: Long = 1L,
    evaluationItemId: Long = 1L,
    testName: String = TEST_NAME,
    programmingLanguage: ProgrammingLanguage = ProgrammingLanguage.KOTLIN,
    id: Long = 0L
): JudgmentItem {
    return JudgmentItem(missionId, evaluationItemId, testName, programmingLanguage, id)
}

fun createJudgmentPassRequest(
    requestKey: String = REQUEST_KEY,
    passCount: Int = 5,
    totalCount: Int = 10
): JudgmentPassRequest {
    return JudgmentPassRequest(requestKey, passCount, totalCount)
}

fun createJudgmentFailRequest(
    requestKey: String = REQUEST_KEY,
    statusCode: JudgmentStatusCode = JudgmentStatusCode.INTERNAL_SERVER_ERROR,
    message: String? = ""
): JudgmentFailRequest {
    return JudgmentFailRequest(requestKey, statusCode, message)
}

fun createJudgmentFailCause(
    requestKey: String = REQUEST_KEY,
    message: String = "internal-server-error happened",
    id: Long = 0L
): JudgmentFailCause {
    return JudgmentFailCause(requestKey, message, id)
}
