package io.cucumber.junit;

import android.content.Context;
import android.content.res.AssetManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AndroidResourceTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    private final Context context = Mockito.mock(Context.class);

    @Test
    public void getPath_returns_given_path() {

        // given
        final URI path = URI.create("file:some/path.feature");
        final AndroidResource androidResource = new AndroidResource(context, path);

        // when
        final URI result = androidResource.getPath();

        // then
        assertThat(result, is(path));
    }

   
    @Test
    public void toString_outputs_the_path() {

        // given
        final URI path = URI.create("file:some/path.feature");
        final AndroidResource androidResource = new AndroidResource(context, path);

        // when
        final String result = androidResource.toString();

        // then
        assertEquals("AndroidResource (" + path.getSchemeSpecificPart() + ")",result);
    }

    @Test
    public void getInputStream_returns_asset_stream() throws IOException {

        // given
        final URI path = URI.create("file:some/path.feature");
        AssetManager assetManager = Mockito.mock(AssetManager.class);
        InputStream stream = Mockito.mock(InputStream.class);
        Mockito.when(assetManager.open("some/path.feature",AssetManager.ACCESS_UNKNOWN)).thenReturn(stream);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        final AndroidResource androidResource = new AndroidResource(context, path);

        // when
        final InputStream result = androidResource.getInputStream();

        // then
        assertSame(stream, result);
    }
}
