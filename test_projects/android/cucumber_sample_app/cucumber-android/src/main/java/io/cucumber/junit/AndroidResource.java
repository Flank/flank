package io.cucumber.junit;

import android.content.Context;
import android.content.res.AssetManager;
import cucumber.runtime.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Android specific implementation of {@link cucumber.runtime.io.Resource} which is apple
 * to create {@link InputStream}s for android assets.
 */
public final class AndroidResource implements Resource {

    /**
     * The {@link Context} to get the {@link InputStream} from
     */
    private final Context context;

    /**
     * The path of the resource.
     */
    private final URI path;
    private final String pathInAssets;

    /**
     * Creates a new instance for the given parameters.
     * @param context the {@link Context} to create the {@link InputStream} from
     * @param path the path to the resource
     */
    AndroidResource(final Context context, final URI path) {
        this.context = context;
        this.path = path;
        this.pathInAssets = path.getSchemeSpecificPart();
    }

    @Override
    public URI getPath() {
        return path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return context.getAssets().open(pathInAssets, AssetManager.ACCESS_UNKNOWN);
    }

    @Override
    public String toString() {
        return "AndroidResource (" + pathInAssets + ")";
    }
}
