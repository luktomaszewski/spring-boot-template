package com.github.lomasz.spring.boot.template.application.usecase;

import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.port.TemplateStorage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetTemplateUseCase implements UseCase<GetTemplateUseCase.Input, GetTemplateUseCase.Output> {

    private final TemplateStorage templateStorage;

    @Override
    public Output execute(Input input) {
        return new Output(templateStorage.findById(input.id()));
    }

    public record Input(Long id) implements InputValues {
    }

    public record Output(Optional<Template> template) implements OutputValues {
    }

}
