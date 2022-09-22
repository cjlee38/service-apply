package apply.ui.api

import apply.application.JudgmentFailRequest
import apply.application.JudgmentHistoryResponse
import apply.application.JudgmentHistoryService
import apply.application.JudgmentPassRequest
import apply.application.JudgmentService
import apply.domain.user.User
import apply.security.LoginUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api")
@RestController
class JudgmentRestController(
    private val judgmentService: JudgmentService,
    private val judgmentHistoryService: JudgmentHistoryService
) {
    @PostMapping("/recruitments/{recruitmentId}/missions/{missionId}/judgment")
    fun judgeExample(
        @PathVariable recruitmentId: String,
        @PathVariable missionId: Long,
        @LoginUser user: User,
    ): ResponseEntity<ApiResponse<JudgmentHistoryResponse>> {
        val response = judgmentService.judgeExample(missionId, user.id)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/judgment/pass")
    fun reflectPassResult(
        @RequestBody judgmentPassRequest: JudgmentPassRequest
    ): ResponseEntity<Void> {
        judgmentHistoryService.reflectPassResult(judgmentPassRequest)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/judgment/fail")
    fun reflectFailResult(
        @RequestBody judgmentFailRequest: JudgmentFailRequest
    ): ResponseEntity<Void> {
        judgmentHistoryService.reflectFailResult(judgmentFailRequest)
        return ResponseEntity.ok().build()
    }
}
