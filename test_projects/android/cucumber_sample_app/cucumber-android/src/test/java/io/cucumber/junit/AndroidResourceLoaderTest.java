package io.cucumber.junit;

import android.content.Context;
import android.content.res.AssetManager;
import com.google.common.collect.Lists;
import cucumber.runtime.io.Resource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AndroidResourceLoaderTest {

    private final Context context = mock(Context.class);
    private final AssetManager assetManager = mock(AssetManager.class, RETURNS_SMART_NULLS);
    private final AndroidResourceLoader androidResourceLoader = new AndroidResourceLoader(context);

    @Before
    public void beforeEachTest() {
        when(context.getAssets()).thenReturn(assetManager);
    }

    @Test
    public void retrieves_resource_by_given_path_and_suffix() {

        // given
        final URI path = URI.create("file:some/path/some.feature");
        final String suffix = "feature";

        // when
        final List<Resource> resources = Lists.newArrayList(androidResourceLoader.resources(path, suffix));

        // then
        assertThat(resources.size(), is(1));
        assertThat(resources.get(0).getPath(), is(path));
    }

    @Test
    public void retrieves_resources_recursively_from_given_path() throws IOException {

        // given
        final String path = "file:dir";
        final String dir = "dir";
        final String dirFile = "dir.feature";
        final String subDir = "subdir";
        final String subDirFile = "subdir.feature";
        final String suffix = "feature";

        when(assetManager.list(dir)).thenReturn(new String[]{subDir, dirFile});
        when(assetManager.list(dir + "/" + subDir)).thenReturn(new String[]{subDirFile});

        // when
        final List<Resource> resources = Lists.newArrayList(androidResourceLoader.resources(URI.create(path), suffix));

        // then
        assertThat(resources.size(), is(2));
        assertThat(resources, hasItem(withPath(path + "/" + dirFile)));
        assertThat(resources, hasItem(withPath(path + "/" + subDir + "/" + subDirFile)));
    }

    @Test
    public void only_retrieves_those_resources_which_end_the_specified_suffix() throws IOException {

        // given
        final String dir = "dir";
        String path = "file:" + dir;
        final String expected = "expected.feature";
        final String unexpected = "unexpected.thingy";
        final String suffix = "feature";
        when(assetManager.list(dir)).thenReturn(new String[]{expected, unexpected});

        // when
        final List<Resource> resources = Lists.newArrayList(androidResourceLoader.resources(URI.create(path), suffix));

        // then
        assertThat(resources.size(), is(1));
        assertThat(resources, hasItem(withPath(path + "/" + expected)));
    }

    private static Matcher<? super Resource> withPath(final String path) {
        return new TypeSafeMatcher<Resource>() {
            @Override
            protected boolean matchesSafely(final Resource item) {
                return item.getPath().toString().equals(path);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("resource with path: " + path);
            }
        };
    }
}
