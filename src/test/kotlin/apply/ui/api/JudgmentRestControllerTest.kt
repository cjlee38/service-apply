package apply.ui.api

import apply.application.JudgmentHistoryService
import apply.application.JudgmentService
import apply.createJudgmentHistoryResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.test.web.servlet.post
import support.test.web.servlet.bearer

@WebMvcTest(JudgmentRestController::class)
internal class JudgmentRestControllerTest : RestControllerTest() {
    @MockkBean
    private lateinit var judgmentService: JudgmentService

    @MockkBean
    private lateinit var judgmentHistoryService: JudgmentHistoryService

    @Test
    fun `예제 테스트를 실행한다`() {
        val response = createJudgmentHistoryResponse()
        every { judgmentService.judgeExample(any(), any()) } returns response

        mockMvc.post("/api/recruitments/{recruitmentId}/missions/{missionId}/judgment", 1L, 1L) {
            bearer("valid_token")
        }.andExpect {
            status { isOk }
            content { success(response) }
        }.andDo {
            handle(document("judgment-post"))
        }
    }

    // todo: now: returns something
    @Test
    fun `성공 결과를 수신한다`() {
        every { judgmentHistoryService.reflectPassResult(any()) } just Runs

        mockMvc.post("/api/judgment/pass")
            .andExpect {
                status { isOk }
//                content {  }
            }.andDo {
                handle(document("judgment-pass-result-post"))
            }
    }

    @Test
    fun `실패 결과를 수신한다`() {
        every { judgmentHistoryService.reflectFailResult(any()) } just Runs

        mockMvc.post("/api/judgment/fail")
            .andExpect {
                status { isOk }
//                content {  }
            }.andDo {
                handle(document("judgment-fail-result-post"))
            }
    }
}
