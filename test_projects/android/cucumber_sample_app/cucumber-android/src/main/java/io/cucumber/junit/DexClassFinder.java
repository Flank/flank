package io.cucumber.junit;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cucumber.runtime.ClassFinder;
import dalvik.system.DexFile;
import io.cucumber.core.model.Classpath;

/**
 * Android specific implementation of {@link ClassFinder} which loads classes contained in the provided {@link DexFile}.
 */
final class DexClassFinder implements ClassFinder {

    /**
     * Symbol name of the manifest class.
     */
    private static final String MANIFEST_CLASS_NAME = "Manifest";

    /**
     * Symbol name of the resource class.
     */
    private static final String RESOURCE_CLASS_NAME = "R";

    /**
     * Symbol name prefix of any inner class of the resource class.
     */
    private static final String RESOURCE_INNER_CLASS_NAME_PREFIX = "R$";

    /**
     * The file name separator.
     */
    private static final String FILE_NAME_SEPARATOR = ".";

    /**
     * The class loader to actually load the classes specified by the {@link DexFile}.
     */
    private static final ClassLoader CLASS_LOADER = DexClassFinder.class.getClassLoader();

    /**
     * The "symbol" representing the default package.
     */
    private static final String DEFAULT_PACKAGE = "";
    private static final Pattern PATH_SEPARATOR_PATTERN = Pattern.compile("/", Pattern.LITERAL);
    /**
     * The {@link DexFile} to load classes from
     */
    private final DexFile dexFile;

    /**
     * Creates a new instance for the given parameter.
     *
     * @param dexFile the {@link DexFile} to load classes from
     */
    DexClassFinder(final DexFile dexFile) {
        this.dexFile = dexFile;
    }

    @Override
    public <T> Collection<Class<? extends T>> getDescendants(final Class<T> parentType, final URI packageName) {
        final List<Class<? extends T>> result = new ArrayList<Class<? extends T>>();
        String packageNameString = PATH_SEPARATOR_PATTERN.matcher(Classpath.resourceName(packageName)).replaceAll(Matcher.quoteReplacement("."));
        final Enumeration<String> entries = dexFile.entries();
        while (entries.hasMoreElements()) {
            final String className = entries.nextElement();
            if (isInPackage(className, packageNameString) && !isGenerated(className)) {
                try {
                    final Class<? extends T> clazz = loadClass(className);
                    if (!parentType.equals(clazz) && parentType.isAssignableFrom(clazz) && canGetMethods(clazz)) {
                        result.add(clazz.asSubclass(parentType));
                    }
                } catch (ClassNotFoundException ignored) {
                    //ignore, class cannot be loaded - same as in ResourceLoaderClassFinder
                } catch (NoClassDefFoundError ignored){
                    //ignore, class cannot be loaded - same as in ResourceLoaderClassFinder
                }
            }
        }
        return result;
    }

    @Override
    public <T> Class<? extends T> loadClass(final String className) throws ClassNotFoundException {
        return (Class<? extends T>) Class.forName(className, false, CLASS_LOADER);
    }

    private boolean isInPackage(final String className, final String packageName) {
        final int lastDotIndex = className.lastIndexOf(FILE_NAME_SEPARATOR);
        final String classPackage = lastDotIndex == -1 ? DEFAULT_PACKAGE : className.substring(0, lastDotIndex);
        return classPackage.startsWith(packageName);
    }

    private static boolean isGenerated(final String className) {
        return isAndroidGenerated(className) || isKotlinGenerated(className);
    }

    private static boolean isAndroidGenerated(final String className) {
        final int lastDotIndex = className.lastIndexOf(FILE_NAME_SEPARATOR);
        final String shortName = lastDotIndex == -1 ? className : className.substring(lastDotIndex + 1);
        return shortName.equals(MANIFEST_CLASS_NAME) || shortName.equals(RESOURCE_CLASS_NAME) || shortName.startsWith(RESOURCE_INNER_CLASS_NAME_PREFIX);
    }

    private static boolean isKotlinGenerated(String className) {
        return className.contains("$$inlined$");
    }

    /**
     * On older apis obtaining all methods via {@link Class#getMethods()} can lead to NoClassDefFoundError if such methods has parameters with unavailable on this api classes such as {@link java.util.function.Function}
     */
    private static boolean canGetMethods(Class<?> clazz) {
        try {
            clazz.getMethods();
        } catch (NoClassDefFoundError ignored){
            return false;
        }
        return true;
    }
}
