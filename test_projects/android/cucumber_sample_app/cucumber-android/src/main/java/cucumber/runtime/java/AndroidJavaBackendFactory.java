package cucumber.runtime.java;

import cucumber.api.java.ObjectFactory;
import cucumber.runtime.BackendSupplier;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.DefaultTypeRegistryConfiguration;
import cucumber.runtime.Env;
import cucumber.runtime.Reflections;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.stepexpression.TypeRegistry;

import static java.util.Collections.singletonList;

/**
 * This factory is responsible for creating the {@see JavaBackend} with dex class finder.
 */
public class AndroidJavaBackendFactory {
    public static BackendSupplier createBackend(RuntimeOptions runtimeOptions, ClassFinder classFinder) {
        return () -> {
            final Reflections reflections = new Reflections(classFinder);
            final ObjectFactory delegateObjectFactory = ObjectFactoryLoader.loadObjectFactory(classFinder,
                    JavaBackend.getObjectFactoryClassName(Env.INSTANCE), JavaBackend.getDeprecatedObjectFactoryClassName(Env.INSTANCE));
            final TypeRegistryConfigurer typeRegistryConfigurer = reflections.instantiateExactlyOneSubclass(TypeRegistryConfigurer.class,
                    runtimeOptions.getGlue(), new Class[0], new Object[0], new DefaultTypeRegistryConfiguration());
            final TypeRegistry typeRegistry = new TypeRegistry(typeRegistryConfigurer.locale());
            typeRegistryConfigurer.configureTypeRegistry(typeRegistry);
            return singletonList(new JavaBackend(delegateObjectFactory, classFinder, typeRegistry));
        };
    }
}
