package com.github.lomasz.spring.boot.template.application.usecase;

import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.port.AddTemplatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AddTemplateUseCase implements UseCase<AddTemplateUseCase.Input, AddTemplateUseCase.Output> {

    private final AddTemplatePort addTemplatePort;

    @Override
    public Output execute(Input input) {
        return new Output(addTemplatePort.create(input.template()));
    }

    public record Input(Template template) implements UseCase.Input {
    }

    public record Output(Long id) implements UseCase.Output {
    }

}
