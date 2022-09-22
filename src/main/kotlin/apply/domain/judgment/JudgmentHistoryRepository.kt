package apply.domain.judgment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

fun JudgmentHistoryRepository.findLastByUserIdAndMissionIdAndJudgmentType(
    userId: Long,
    missionId: Long,
    judgmentType: JudgmentType
): JudgmentHistory? =
    findFirstByUserIdAndMissionIdAndJudgmentTypeOrderByIdDesc(userId, missionId, judgmentType)

fun JudgmentHistoryRepository.getByRequestKey(requestKey: String): JudgmentHistory = findByRequestKey(requestKey)
    ?: throw IllegalArgumentException("요청 키가 존재하지 않습니다.")

interface JudgmentHistoryRepository : JpaRepository<JudgmentHistory, Long> {
    fun findFirstByUserIdAndMissionIdAndJudgmentTypeOrderByIdDesc(
        userId: Long,
        missionId: Long,
        judgmentType: JudgmentType
    ): JudgmentHistory?

    @Query(
        "select jh from JudgmentHistory jh where jh.missionId = :missionId and jh.judgmentType = 'REAL' " +
            "and jh.id in (select max(jh2.id) from JudgmentHistory jh2 group by jh2.userId)"
    )
    fun findAllLastOfRealsByMissionId(
        missionId: Long,
    ): List<JudgmentHistory>

    fun findByRequestKey(requestKey: String): JudgmentHistory?
}
