package com.javarefresher.topics.springmvc;

import com.javarefresher.core.ConsolePrinter;
import com.javarefresher.core.StudyTopic;

import java.util.Map;
import java.util.Optional;

public final class SpringMvcStudyTopic implements StudyTopic {
    @Override
    public String key() {
        return "spring-mvc";
    }

    @Override
    public String title() {
        return "Spring Boot MVC Patterns and Antipatterns";
    }

    @Override
    public void run(ConsolePrinter printer) {
        printer.topicHeader(key(), title());
        printer.section("Functionality", "Simulates a controller-service-repository flow with DTO mapping and exception-to-response translation.");
        printer.section("Design patterns utilized", "Layered Architecture + Constructor Injection + DTO Mapper + Controller exception boundary.");
        printer.interviewFrame(
                "Expose candidate-profile data through an API endpoint while preserving clean layer boundaries.",
                "Fat controller: business logic, repository calls, and entity exposure all mixed in endpoint code.",
                "Thin controller delegates to service, service coordinates rules/data access, mapper isolates API DTO shape.",
                "Lead interviews often test architecture decisions that keep APIs maintainable, testable, and secure."
        );

        CandidateRepository repository = new InMemoryCandidateRepository();
        CandidateMapper mapper = new CandidateMapper();
        CandidateService service = new CandidateService(repository, mapper);
        CandidateController controller = new CandidateController(service);

        System.out.println();
        System.out.println("Recommended layered flow:");
        ApiResponse<CandidateDto> found = controller.getCandidateProfile("1001");
        ApiResponse<CandidateDto> missing = controller.getCandidateProfile("9999");
        System.out.println(" GET /api/candidates/1001 -> " + formatResponse(found));
        System.out.println(" GET /api/candidates/9999 -> " + formatResponse(missing));
        System.out.println(" note: repository returns entities, service applies rules, controller maps exceptions to HTTP-style responses.");

        System.out.println();
        System.out.println("Antipattern demonstration:");
        FatCandidateController fatController = new FatCandidateController(repository);
        Object rawResponse = fatController.getCandidateProfileRaw("1001");
        System.out.println(" fat controller raw response -> " + rawResponse);
        System.out.println(" issue: entity internals and business rules leak directly through controller code.");

        System.out.println();
        System.out.println("Interview talking points:");
        System.out.println(" - Use constructor injection to keep dependencies explicit and test-friendly.");
        System.out.println(" - Keep controllers orchestration-focused, not business-rule-focused.");
        System.out.println(" - Map entities to DTOs so API contracts can evolve independently from persistence models.");
    }

    private static <T> String formatResponse(ApiResponse<T> response) {
        if (response.error() != null) {
            return "status=%d error=\"%s\"".formatted(response.status(), response.error());
        }
        return "status=%d body=%s".formatted(response.status(), response.body());
    }

    private record CandidateEntity(
            String id,
            String fullName,
            String stage,
            String internalNotes,
            String compensationBand
    ) {}

    private record CandidateDto(
            String id,
            String displayName,
            String stage
    ) {}

    private record ApiResponse<T>(
            int status,
            T body,
            String error
    ) {
        private static <T> ApiResponse<T> ok(T body) {
            return new ApiResponse<>(200, body, null);
        }

        private static <T> ApiResponse<T> notFound(String error) {
            return new ApiResponse<>(404, null, error);
        }
    }

    private static final class CandidateNotFoundException extends RuntimeException {
        private CandidateNotFoundException(String message) {
            super(message);
        }
    }

    private interface CandidateRepository {
        Optional<CandidateEntity> findById(String id);
    }

    private static final class InMemoryCandidateRepository implements CandidateRepository {
        private final Map<String, CandidateEntity> entities = Map.of(
                "1001", new CandidateEntity("1001", "Ava Thompson", "Onsite", "Strong system design", "L5"),
                "1002", new CandidateEntity("1002", "Noah Patel", "Screen", "Needs deeper concurrency examples", "L4")
        );

        @Override
        public Optional<CandidateEntity> findById(String id) {
            return Optional.ofNullable(entities.get(id));
        }
    }

    private static final class CandidateMapper {
        private CandidateDto toDto(CandidateEntity entity) {
            return new CandidateDto(entity.id(), entity.fullName(), entity.stage());
        }
    }

    private static final class CandidateService {
        private final CandidateRepository repository;
        private final CandidateMapper mapper;

        private CandidateService(CandidateRepository repository, CandidateMapper mapper) {
            this.repository = repository;
            this.mapper = mapper;
        }

        private CandidateDto getCandidateProfile(String id) {
            CandidateEntity entity = repository.findById(id)
                    .orElseThrow(() -> new CandidateNotFoundException("Candidate %s not found".formatted(id)));

            CandidateEntity normalized = normalizeStage(entity);
            return mapper.toDto(normalized);
        }

        private CandidateEntity normalizeStage(CandidateEntity entity) {
            String normalizedStage = entity.stage().toUpperCase();
            return new CandidateEntity(
                    entity.id(),
                    entity.fullName(),
                    normalizedStage,
                    entity.internalNotes(),
                    entity.compensationBand()
            );
        }
    }

    private static final class CandidateController {
        private final CandidateService service;

        private CandidateController(CandidateService service) {
            this.service = service;
        }

        private ApiResponse<CandidateDto> getCandidateProfile(String id) {
            try {
                CandidateDto dto = service.getCandidateProfile(id);
                return ApiResponse.ok(dto);
            } catch (CandidateNotFoundException ex) {
                return ApiResponse.notFound(ex.getMessage());
            }
        }
    }

    private static final class FatCandidateController {
        private final CandidateRepository repository;

        private FatCandidateController(CandidateRepository repository) {
            this.repository = repository;
        }

        private Object getCandidateProfileRaw(String id) {
            CandidateEntity entity = repository.findById(id).orElse(null);
            if (entity == null) {
                return Map.of("error", "candidate missing");
            }

            String stage = entity.stage().toUpperCase();
            return new CandidateEntity(
                    entity.id(),
                    entity.fullName(),
                    stage,
                    entity.internalNotes(),
                    entity.compensationBand()
            );
        }
    }
}
