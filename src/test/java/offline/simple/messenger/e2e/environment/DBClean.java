package offline.simple.messenger.e2e.environment;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DBClean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @PostConstruct
    public void getTableNames() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                // @Entity가 붙은 엔티티만 필터링
                .filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
                // 실제 매핑된 테이블명을 가져옴 (@Table(name = "xxx") 우선 사용)
                .map(entityType -> {
                    Class<?> javaType = entityType.getJavaType();

                    Table tableAnnotation = javaType.getAnnotation(Table.class);
                    if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
                        // @Table(name = "...")에 지정된 이름 사용
                        return tableAnnotation.name();
                    } else {
                        // @Table이 없거나 name이 비어있으면, 엔티티 이름(entityType.getName()) 그대로 사용
                        return entityType.getName();
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        // H2 DB의 referential integrity 임시 해제
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String tableName : tableNames) {
            // 실제 물리 테이블명을 이용하여 TRUNCATE
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            // 기본 키 AUTO_INCREMENT 재시작
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        }

        // referential integrity 복원
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
