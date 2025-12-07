package ch.zhaw.stellarcompass;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import ch.zhaw.stellarcompass.security.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
	"spring.data.mongodb.uri=mongodb://localhost:27017/stellarcompass-test",
	"spring.security.oauth2.resourceserver.jwt.issuer-uri=https://test-domain.auth0.com/"
})
class StellarcompassApplicationTests {

	@Test
	void contextLoads() {
	}

}
