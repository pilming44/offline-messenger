package offline.simple.messenger.e2e.environment;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 통합(E2E) 테스트용 부모 클래스
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class E2ESql {

    @Autowired
    private DBClean dbClean;

    @BeforeEach
    void cleanDatabase() {
        dbClean.execute();
    }
}
