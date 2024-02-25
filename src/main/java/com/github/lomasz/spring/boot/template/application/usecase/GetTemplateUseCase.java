package com.github.lomasz.spring.boot.template.application.usecase;

import com.github.lomasz.spring.boot.template.application.domain.model.Template;
import com.github.lomasz.spring.boot.template.application.port.GetTemplatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetTemplateUseCase implements UseCase<GetTemplateUseCase.Input, GetTemplateUseCase.Output> {

    private final GetTemplatePort getTemplatePort;

    @Override
    public Output execute(Input input) {
        return new Output(getTemplatePort.findById(input.id()));
    }

    public record Input(Long id) implements UseCase.Input {
    }

    public record Output(Template template) implements UseCase.Output {
    }

}
