package com.laces.core.form.core

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.RegexPatternTypeFilter
import java.util.regex.Pattern

class ClassLister {

    fun listAllClassesInPackage(packageName: String): List<Class<*>> {
        // create scanner and disable default filters (that is the 'false' argument)
        val provider = ClassPathScanningCandidateComponentProvider(false)

        // add include filters which matches all the classes (or use your own)
        provider.addIncludeFilter(RegexPatternTypeFilter(Pattern.compile(".*")))

        // get matching classes defined in the package
        val classes = provider.findCandidateComponents(packageName)

        // this is how you can load the class type from BeanDefinition instance
        return classes.map { Class.forName(it.beanClassName) }
    }
}