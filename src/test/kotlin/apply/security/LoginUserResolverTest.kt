package apply.security

import apply.application.UserService
import apply.domain.user.Gender
import apply.domain.user.Password
import apply.domain.user.User
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.context.request.NativeWebRequest
import support.createLocalDate
import support.test.UnitTest
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

@UnitTest
internal class LoginUserResolverTest {
    @MockK
    private lateinit var userService: UserService

    @MockK
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var loginUserResolver: LoginUserResolver
    private lateinit var methodParameter: MethodParameter
    private lateinit var nativeWebRequest: NativeWebRequest

    @BeforeEach
    internal fun setUp() {
        loginUserResolver = LoginUserResolver(jwtTokenProvider, userService)
        methodParameter = mockk()
        nativeWebRequest = mockk()
    }

    @ParameterizedTest
    @CsvSource("user,true", "administrator,true", "guest,false")
    fun `@LoginUser 주어진 메서드의 서포트 여부를 확인한다`(methodName: String, expected: Boolean) {
        val method = TestAuthController::class.java.getDeclaredMethod(methodName, User::class.java)
        val loginUserParameter: MethodParameter = MethodParameter.forExecutable(method, 0)
        assertThat(loginUserResolver.supportsParameter(loginUserParameter)).isEqualTo(expected)
    }

    private class TestAuthController {
        fun user(@LoginUser user: User) {}
        fun administrator(@LoginUser(administrator = true) user: User) {}
        fun guest(user: User) {}
    }

    @Test
    fun `관리 API를 호출하면 실패한다`() {
        every { methodParameter.getParameterAnnotation<LoginUser>(any()) } returns createAdministratorAnnotation()
        assertThrows<LoginFailedException> {
            loginUserResolver.resolveArgument(methodParameter, null, nativeWebRequest, null)
        }
    }

    @Test
    fun `요청의 Authorization 헤더로 저장된 회원을 불러온다`() {
        every { methodParameter.getParameterAnnotation<LoginUser>(any()) } returns createUserAnnotation()
        every { nativeWebRequest.getHeader(AUTHORIZATION) } returns "Bearer valid_token"
        every { jwtTokenProvider.isValidToken("valid_token") } returns true
        every { jwtTokenProvider.getSubject("valid_token") } returns "user_email@email.com"
        val expectedUser = User(
            name = "홍길동1",
            email = "user_email@email.com",
            phoneNumber = "010-0000-0000",
            gender = Gender.MALE,
            birthday = createLocalDate(2020, 4, 17),
            password = Password("password")
        )
        every { userService.getByEmail("user_email@email.com") } returns expectedUser

        val result = loginUserResolver.resolveArgument(methodParameter, null, nativeWebRequest, null)
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedUser)
    }

    @ParameterizedTest
    @CsvSource(
        "Bearertokeninfo",
        "''",
        "Bearer"
    )
    fun `요청의 Authorization 헤더의 형식이 올바르지 않을 경우 예외가 발생한다`(header: String) {
        every { methodParameter.getParameterAnnotation<LoginUser>(any()) } returns createUserAnnotation()
        every { nativeWebRequest.getHeader(AUTHORIZATION) } returns header

        assertThrows<LoginFailedException> {
            loginUserResolver.resolveArgument(methodParameter, null, nativeWebRequest, null)
        }
    }

    @Test
    fun `요청의 Authorization 헤더가 존재하지 않을 경우 예외가 발생한다`() {
        every { methodParameter.getParameterAnnotation<LoginUser>(any()) } returns createUserAnnotation()
        every { nativeWebRequest.getHeader(AUTHORIZATION) } returns null

        assertThrows<LoginFailedException> {
            loginUserResolver.resolveArgument(methodParameter, null, nativeWebRequest, null)
        }
    }

    @Test
    fun `요청의 토큰이 유효하지 않은 경우 예외가 발생한다`() {
        every { methodParameter.getParameterAnnotation<LoginUser>(any()) } returns createUserAnnotation()
        every { nativeWebRequest.getHeader(AUTHORIZATION) } returns "invalid_token"
        every { jwtTokenProvider.isValidToken("invalid_token") } returns false

        assertThrows<LoginFailedException> {
            loginUserResolver.resolveArgument(methodParameter, null, nativeWebRequest, null)
        }
    }

    private fun createUserAnnotation(): LoginUser = LoginUser::class.createInstance()

    private fun createAdministratorAnnotation(): LoginUser? = LoginUser::class.primaryConstructor?.call(true)
}
