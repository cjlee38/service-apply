package apply.application

import apply.createApplicationForm
import apply.createRecruitment
import apply.createUser
import apply.domain.applicationform.ApplicationFormRepository
import apply.domain.applicationform.DuplicateApplicationException
import apply.domain.recruitment.RecruitmentRepository
import apply.domain.user.UserRepository
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import support.test.IntegrationTest
import java.time.LocalDateTime

@IntegrationTest
class ApplicationFormIntegrationTest(
    private val applicationFormService: ApplicationFormService,
    private val userRepository: UserRepository,
    private val applicationFormRepository: ApplicationFormRepository,
    private val recruitmentRepository: RecruitmentRepository
) : DescribeSpec({
    describe("ApplicationForm") {
        context("모집 지원을 할 때 아직 지원하지 않은 경우") {
            it("단독 모집에 지원 가능한다.") {
                val recruitment = recruitmentRepository.save(createRecruitment(termId = 0L, recruitable = true))
                val user = userRepository.save(createUser())
                shouldNotThrowAny {
                    applicationFormService.create(
                        user.id,
                        CreateApplicationFormRequest(recruitment.id)
                    )
                }
            }
        }

        context("이미 지원한 지원에는") {
            it("중복으로 지원할 수 없다.") {
                val recruitment = recruitmentRepository.save(createRecruitment(termId = 0L, recruitable = true))
                val user = userRepository.save(createUser())
                applicationFormRepository.save(
                    createApplicationForm(
                        user.id,
                        recruitment.id,
                        submitted = true,
                        submittedDateTime = LocalDateTime.now()
                    )
                )
                shouldThrow<IllegalStateException> {
                    applicationFormService.create(user.id, CreateApplicationFormRequest(recruitment.id))
                }
            }
        }

        context("동일한 기수의 다른 모집에는") {
            it("지원할 수 없다.") {
                val user = userRepository.save(createUser())
                val appliedRecruitment = recruitmentRepository.save(createRecruitment(termId = 1L))
                applicationFormRepository.save(
                    createApplicationForm(
                        user.id,
                        appliedRecruitment.id,
                        submitted = true,
                        submittedDateTime = LocalDateTime.now()
                    )
                )
                val recruitment = recruitmentRepository.save(createRecruitment(termId = 1L, recruitable = true))
                shouldThrow<DuplicateApplicationException> {
                    applicationFormService.create(user.id, CreateApplicationFormRequest(recruitment.id))
                }
            }
        }

        context("동일한 기수의 다른 모집에 임시 저장되어 있어도") {
            it("지원서를 제출할 수 있다.") {
                val user = userRepository.save(createUser())
                val appliedRecruitment = recruitmentRepository.save(createRecruitment(termId = 1L))
                applicationFormRepository.save(createApplicationForm(user.id, appliedRecruitment.id, submitted = false))
                val recruitment = recruitmentRepository.save(createRecruitment(termId = 1L, recruitable = true))
                applicationFormRepository.save(createApplicationForm(user.id, recruitment.id, submitted = false))
                shouldNotThrow<Exception> {
                    applicationFormService.update(
                        user.id,
                        UpdateApplicationFormRequest(recruitmentId = recruitment.id, submitted = true)
                    )
                }
            }
        }

        context("동일한 기수의 다른 모집에 이미 지원서를 제출한 경우에는") {
            it("지원서를 제출할 수 없다.") {
                val user = userRepository.save(createUser())
                val appliedRecruitment = recruitmentRepository.save(createRecruitment(termId = 1L))
                applicationFormRepository.save(
                    createApplicationForm(
                        user.id,
                        appliedRecruitment.id,
                        submitted = true,
                        submittedDateTime = LocalDateTime.now()
                    )
                )
                val recruitment = recruitmentRepository.save(createRecruitment(termId = 1L, recruitable = true))
                applicationFormRepository.save(createApplicationForm(user.id, recruitment.id, submitted = false))
                shouldThrow<DuplicateApplicationException> {
                    applicationFormService.update(
                        user.id,
                        UpdateApplicationFormRequest(recruitmentId = recruitment.id, submitted = true)
                    )
                }
            }
        }
    }

    afterEach {
        userRepository.deleteAll()
        applicationFormRepository.deleteAll()
        recruitmentRepository.deleteAll()
    }
})
