package apply.domain.judgment

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class JudgmentResult(
    @Column
    val passCount: Int?,

    @Column
    val totalCount: Int?,

    @Column
    @Enumerated(EnumType.STRING)
    val statusCode: JudgmentStatusCode
) {
    val isOK: Boolean
        get() = !statusCode.isError()
}
