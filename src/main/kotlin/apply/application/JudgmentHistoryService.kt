package apply.application

import apply.application.judgmentserver.JudgmentRequest
import apply.domain.assignment.Assignment
import apply.domain.judgment.Commit
import apply.domain.judgment.JudgmentFailCause
import apply.domain.judgment.JudgmentFailCauseRepository
import apply.domain.judgment.JudgmentHistory
import apply.domain.judgment.JudgmentHistoryRepository
import apply.domain.judgment.JudgmentItemRepository
import apply.domain.judgment.JudgmentType
import apply.domain.judgment.findLastByUserIdAndMissionIdAndJudgmentType
import apply.domain.judgment.getByMissionId
import apply.domain.judgment.getByRequestKey
import apply.domain.judgmentserver.JudgmentServer
import org.springframework.stereotype.Service

@Service
class JudgmentHistoryService(
    private val judgmentHistoryRepository: JudgmentHistoryRepository,
    private val judgmentItemRepository: JudgmentItemRepository,
    private val judgmentFailCauseRepository: JudgmentFailCauseRepository,
    private val judgmentServer: JudgmentServer
) {
    fun findOrCreateHistory(
        assignment: Assignment,
        judgmentType: JudgmentType,
        latestCommit: Commit
    ): JudgmentHistoryResponse {
        val lastHistory = judgmentHistoryRepository
            .findLastByUserIdAndMissionIdAndJudgmentType(assignment.missionId, assignment.userId, judgmentType)
        return (
            lastHistory?.takeIf { it.isCompleted(latestCommit.hash) }
                ?: requestAndSave(assignment, latestCommit, judgmentType)
            ).let { JudgmentHistoryResponse(it, assignment.pullRequestUrl) }
    }

    private fun requestAndSave(assignment: Assignment, commit: Commit, judgmentType: JudgmentType): JudgmentHistory {
        val requestKey = requestJudgment(assignment.missionId, commit, judgmentType)
        return judgmentHistoryRepository.save(
            JudgmentHistory(
                assignment.userId,
                assignment.missionId,
                requestKey,
                commit.hash,
                judgmentType
            )
        )
    }

    private fun requestJudgment(missionId: Long, commit: Commit, judgmentType: JudgmentType): String {
        val item = judgmentItemRepository.getByMissionId(missionId)
            .also { if (!it.isValid()) throw IllegalArgumentException("자동채점 항목이 유효하지 않습니다.") }

        return judgmentServer.requestJudgement(
            JudgmentRequest(
                item.testName!!,
                judgmentType,
                commit.pullRequestUrl,
                commit.hash,
                item.programmingLanguage!!
            )
        )
    }

    /*
    TODO : update evaluation answer score by judgment result
     */
    fun reflectPassResult(judgmentPassRequest: JudgmentPassRequest) {
        val judgmentHistory = judgmentHistoryRepository.getByRequestKey(judgmentPassRequest.requestKey)
        judgmentHistory.insertPassResult(judgmentPassRequest.passCount, judgmentPassRequest.totalCount)
    }

    fun reflectFailResult(judgmentFailRequest: JudgmentFailRequest) {
        val judgmentHistory = judgmentHistoryRepository.getByRequestKey(judgmentFailRequest.requestKey)
        judgmentHistory.insertFailResult(judgmentFailRequest.statusCode)
        judgmentFailCauseRepository.save(JudgmentFailCause(judgmentFailRequest.requestKey, judgmentFailRequest.message))
    }
}
