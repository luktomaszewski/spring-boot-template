package com.github.lomasz.spring.boot.template.application.usecase;

@FunctionalInterface
interface UseCase<IN extends UseCase.Input, OUT extends UseCase.Output> {

    OUT execute(IN input);

    interface Input {
    }

    interface Output {
    }
}
