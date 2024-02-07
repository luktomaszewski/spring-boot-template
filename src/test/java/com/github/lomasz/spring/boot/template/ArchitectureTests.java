package com.github.lomasz.spring.boot.template;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

@AnalyzeClasses(packages = "com.github.lomasz.spring.boot.template", importOptions = {
        ImportOption.DoNotIncludeJars.class,
        ImportOption.DoNotIncludeTests.class
})
class ArchitectureTests {

    @ArchTest
    static final ArchRule onionArchitectureIsRespected = onionArchitecture()
            .domainModels("..application.domain..")
            .domainServices("..application.port..")
            .applicationServices("..application.usecase..")
            .adapter("out.persistence", "..adapter.out.persistence..")
            .adapter("api", "..adapter.api..");

    @ArchTest
    static ArchRule noGenericExceptions = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    static ArchRule noDeprecatedApi = GeneralCodingRules.DEPRECATED_API_SHOULD_NOT_BE_USED;

}
