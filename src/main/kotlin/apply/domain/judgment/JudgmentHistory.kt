package apply.domain.judgment

import support.domain.BaseEntity
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
class JudgmentHistory(
    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val missionId: Long,

    @Column(nullable = false, columnDefinition = "char(36)")
    val requestKey: String,

    @Column(nullable = false, columnDefinition = "char(40)")
    val commitHash: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val judgmentType: JudgmentType,

    @Column
    @Embedded
    var result: JudgmentResult? = null,

    id: Long = 0L
) : BaseEntity(id) {
    fun isCompleted(commitHash: String): Boolean =
        isCommitHashEqual(commitHash) && isResultOK() && isExampleType()

    private fun isCommitHashEqual(commitHash: String) = this.commitHash == commitHash

    private fun isResultOK(): Boolean = result?.isOK ?: false

    private fun isExampleType() = judgmentType == JudgmentType.EXAMPLE

    fun insertPassResult(passCount: Int, totalCount: Int) {
        result = JudgmentResult(passCount, totalCount, JudgmentStatusCode.OK)
    }

    fun insertFailResult(statusCode: JudgmentStatusCode) {
        result = JudgmentResult(null, null, statusCode)
    }
}
