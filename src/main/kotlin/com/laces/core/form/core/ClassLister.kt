package com.laces.core.form.core

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter

class ClassLister(
        private val packages: List<String>
) {

    fun <T : Annotation>  allClassesWithAnnotation(
            clazz: Class<T>,
            filter: (T) -> Boolean = {true}
    ): List<Class<*>> {
        return packages
                .flatMap { listAllClassesInPackageWithAnnotation(it, clazz) }
                .filter { filter(it.getAnnotation(clazz)) }
    }

    private fun <T: Annotation> listAllClassesInPackageWithAnnotation(packageName: String, annotationClass: Class<T>): List<Class<*>> {
        // create scanner and disable default filters (that is the 'false' argument)
        val provider = ClassPathScanningCandidateComponentProvider(false)

        // add include filters which matches all the classes (or use your own)
        provider.addIncludeFilter(AnnotationTypeFilter(annotationClass))

        // get matching classes defined in the package
        val classes = provider.findCandidateComponents(packageName)

        // this is how you can load the class type from BeanDefinition instance
        return classes.map { Class.forName(it.beanClassName) }
    }

}