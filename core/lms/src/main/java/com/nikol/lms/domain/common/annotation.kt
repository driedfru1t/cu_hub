package com.nikol.lms.domain.common

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "Эта модель является гипотетической заготовкой. Её структура не подтверждена спецификацией OpenAPI и может измениться."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class UnstableLmsApi