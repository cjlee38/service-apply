package apply.application

import apply.domain.term.Term
import apply.domain.term.TermRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Transactional
@Service
class TermService(
    private val termRepository: TermRepository
) {
    @PostConstruct
    private fun populateDummy() {
        if (termRepository.count() != 0L) {
            return
        }
        val terms = listOf(
            Term("1기"),
            Term("2기"),
            Term("3기"),
            Term("4기")
        )
        termRepository.saveAll(terms)
    }

    fun findAllTermSelectData(): List<TermSelectData> {
        val terms = listOf(Term.SINGLE) + termRepository.findAll().sortedBy { it.name }
        return terms.map(::TermSelectData)
    }

    fun getDataById(id: Long): TermSelectData {
        val term = termRepository.findByIdOrNull(id) ?: throw IllegalArgumentException("해당 id의 기수를 찾을 수 없습니다.")
        return TermSelectData(term.name, term.id)
    }
}