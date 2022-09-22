package apply.domain.judgment

import support.domain.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Lob

@Entity
class JudgmentFailCause(
    @Column(nullable = false, unique = true, columnDefinition = "char(36)")
    val requestKey: String,

    @Lob
    val message: String?,

    id: Long = 0L
) : BaseEntity(id)
