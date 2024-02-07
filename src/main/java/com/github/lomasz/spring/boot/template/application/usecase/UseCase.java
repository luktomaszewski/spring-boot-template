package com.github.lomasz.spring.boot.template.application.usecase;

@FunctionalInterface
interface UseCase<IN extends UseCase.InputValues, OUT extends UseCase.OutputValues> {

    OUT execute(IN input);

    interface InputValues {

    }

    interface OutputValues {

    }
}
