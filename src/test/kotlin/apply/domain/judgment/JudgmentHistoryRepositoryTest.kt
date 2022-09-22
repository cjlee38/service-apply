package apply.domain.judgment

import apply.REQUEST_KEY
import apply.createJudgmentHistory
import apply.domain.judgment.JudgmentType.EXAMPLE
import apply.domain.judgment.JudgmentType.REAL
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import support.test.RepositoryTest

@RepositoryTest
class JudgmentHistoryRepositoryTest(
    private val judgmentHistoryRepository: JudgmentHistoryRepository
) : ExpectSpec({
    extensions(SpringTestExtension(SpringTestLifecycleMode.Root))

    context("쟈동채점 실행 이력") {
        judgmentHistoryRepository.save(createJudgmentHistory(id = 1L))
        judgmentHistoryRepository.save(createJudgmentHistory(id = 2L))
        judgmentHistoryRepository.save(createJudgmentHistory(id = 3L))
        expect("userId와 missionId로 마지막 예제 테스트 실행 이력을 찾는다") {
            val lastHistory =
                judgmentHistoryRepository.findLastByUserIdAndMissionIdAndJudgmentType(1L, 1L, EXAMPLE)

            lastHistory!!.id shouldBe 3L
        }

        expect("userId와 missionId로 마지막 본 테스트 실행 이력을 찾는다") {
            judgmentHistoryRepository.save(createJudgmentHistory(id = 4L, judgmentType = REAL))
            judgmentHistoryRepository.save(createJudgmentHistory(id = 5L, judgmentType = EXAMPLE))

            val lastHistory =
                judgmentHistoryRepository.findLastByUserIdAndMissionIdAndJudgmentType(1L, 1L, REAL)

            lastHistory!!.id shouldBe 4L
        }

        expect("missionId로 유저들의 마지막 본 테스트 실행 이력을 찾는다") {
            judgmentHistoryRepository.save(createJudgmentHistory(id = 1L, userId = 1L, judgmentType = REAL))
            judgmentHistoryRepository.save(createJudgmentHistory(id = 2L, userId = 1L, judgmentType = REAL))
            judgmentHistoryRepository.save(createJudgmentHistory(id = 3L, userId = 2L, judgmentType = REAL))
            judgmentHistoryRepository.save(createJudgmentHistory(id = 4L, userId = 2L, judgmentType = REAL))
            judgmentHistoryRepository.save(createJudgmentHistory(id = 5L, userId = 3L, judgmentType = REAL))
            judgmentHistoryRepository.save(createJudgmentHistory(id = 6L, userId = 3L, judgmentType = REAL))
            judgmentHistoryRepository.save(createJudgmentHistory(id = 7L, userId = 3L, judgmentType = REAL))
            judgmentHistoryRepository.save(createJudgmentHistory(id = 8L, userId = 4L, judgmentType = REAL))

            val histories = judgmentHistoryRepository.findAllLastOfRealsByMissionId(1L)

            histories.map { it.id } shouldContainExactly listOf(2L, 4L, 7L, 8L)
        }

        expect("request key로 실행 이력을 찾는다") {
            judgmentHistoryRepository.save(createJudgmentHistory(
                requestKey = "find-by-request-key"
            ))

            val history = judgmentHistoryRepository.findByRequestKey("find-by-request-key")

            history.shouldNotBeNull()
        }
    }
})
