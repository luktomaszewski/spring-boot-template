package com.github.lomasz.spring.boot.template.application.usecase;

import com.github.lomasz.spring.boot.template.application.domain.model.NewTemplate;
import com.github.lomasz.spring.boot.template.application.port.TemplateStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AddTemplateUseCase implements UseCase<AddTemplateUseCase.Input, AddTemplateUseCase.Output> {

    private final TemplateStorage templateStorage;

    @Override
    public Output execute(Input input) {
        return new Output(templateStorage.create(input.newTemplate()));
    }

    public record Input(NewTemplate newTemplate) implements UseCase.Input {
    }

    public record Output(Long id) implements UseCase.Output {
    }

}
