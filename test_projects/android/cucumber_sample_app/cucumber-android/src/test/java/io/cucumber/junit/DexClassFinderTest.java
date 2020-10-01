package io.cucumber.junit;

import com.google.common.collect.Lists;

import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import dalvik.system.DexFile;
import io.cucumber.core.model.GluePath;
import io.cucumber.junit.shadow.ShadowDexFile;
import io.cucumber.junit.stub.unwanted.SomeUnwantedClass;
import io.cucumber.junit.stub.wanted.Manifest;
import io.cucumber.junit.stub.wanted.R;
import io.cucumber.junit.stub.wanted.SomeClass;
import io.cucumber.junit.stub.wanted.SomeKotlinClass;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowDexFile.class}, manifest = Config.NONE)
public class DexClassFinderTest {

    private DexFile dexFile;
    private DexClassFinder dexClassFinder;

    @Before
    public void beforeEachTest() throws IOException {
        dexFile = new DexFile("notImportant");
        dexClassFinder = new DexClassFinder(dexFile);
    }

    @Test
    public void only_loads_classes_from_specified_package() throws Exception {

        // given
        setDexFileEntries(SomeClass.class, SomeKotlinClass.class, SomeUnwantedClass.class);

        // when
        final Collection<Class<?>> descendants = getDescendants(Object.class, SomeClass.class.getPackage());

        // then
        assertThat(descendants, IsIterableContainingInOrder.<Class<?>>contains(SomeClass.class, SomeKotlinClass.class));
    }

    private <T> Collection<Class<? extends T>> getDescendants(Class<T> parentType, Package javaPackage)
    {
        return dexClassFinder.getDescendants(parentType, GluePath.parse(javaPackage.getName()));
    }

    @Test
    public void does_not_load_manifest_class() throws Exception {

        // given
        setDexFileEntries(SomeClass.class, Manifest.class);

        // when
        final Collection<Class<?>> descendants = getDescendants(Object.class, SomeClass.class.getPackage());

        // then
        assertThat(descendants, containsOnly(SomeClass.class));
    }

    @Test
    public void does_not_load_R_class() throws Exception {

        // given
        setDexFileEntries(SomeClass.class, R.class);

        // when
        final Collection<Class<?>> descendants = getDescendants(Object.class, SomeClass.class.getPackage());

        // then
        assertThat(descendants, containsOnly(SomeClass.class));
    }

    @Test
    public void does_not_load_R_inner_class() throws Exception {

        // given
        setDexFileEntries(SomeClass.class, R.SomeInnerClass.class);

        // when
        final Collection<Class<?>> descendants = getDescendants(Object.class, SomeClass.class.getPackage());

        // then
        assertThat(descendants, containsOnly(SomeClass.class));
    }

    @Test
    public void only_loads_class_which_is_not_the_parent_type() throws Exception {

        // given
        setDexFileEntries(Integer.class, Number.class);

        // when
        final Class parentType = Number.class;
        @SuppressWarnings("unchecked")
        final Collection<Class<?>> descendants = getDescendants(parentType, Object.class.getPackage());

        // then
        assertThat(descendants, containsOnly(Integer.class));
    }

    @Test
    public void only_loads_class_which_is_assignable_to_parent_type() throws Exception {

        // given
        setDexFileEntries(Integer.class, String.class);

        // when
        final Class parentType = Number.class;
        @SuppressWarnings("unchecked")
        final Collection<Class<?>> descendants = getDescendants(parentType, Object.class.getPackage());

        // then
        assertThat(descendants, containsOnly(Integer.class));
    }

    @Test
    public void does_not_load_kotlin_inlined_classes() throws Exception {
        // given
        Class<?> kotlinInlinedFunClass = Class.forName("io.cucumber.junit.stub.wanted.SomeKotlinClass$someFun$$inlined$sortedBy$1");
        setDexFileEntries(SomeClass.class, kotlinInlinedFunClass);

        // when
        final Collection<Class<?>> descendants = getDescendants(Object.class, SomeClass.class.getPackage());

        // then
        assertThat(descendants, containsOnly(SomeClass.class));
    }

    @Test
    public void does_not_throw_exception_if_class_not_found() throws Exception {
        // given
        setDexFileEntries(Arrays.asList(SomeClass.class.getName(),"SomeNotExistentClass"));

        // when
        final Collection<Class<?>> descendants = getDescendants(Object.class, SomeClass.class.getPackage());

        // then
        assertThat(descendants, containsOnly(SomeClass.class));
    }

    private Matcher<Iterable<? extends Class<?>>> containsOnly(final Class<?> type) {
        return IsIterableContainingInOrder.<Class<?>>contains(type);
    }

    private void setDexFileEntries(final Class... entryClasses) throws NoSuchFieldException, IllegalAccessException {
        Collection<String> entries = classToName(entryClasses);
        setDexFileEntries(entries);
    }

    private void setDexFileEntries(Collection<String> entries) throws NoSuchFieldException, IllegalAccessException {
        final Field roboData = DexFile.class.getDeclaredField("__robo_data__");
        final ShadowDexFile shadowDexFile = (ShadowDexFile) roboData.get(dexFile);
        shadowDexFile.setEntries(entries);
    }

    private Collection<String> classToName(final Class... entryClasses) {
        final List<String> names = Lists.newArrayList();
        for (final Class entryClass : entryClasses) {
            names.add(entryClass.getName());
        }

        return names;
    }
}
