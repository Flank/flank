package cucumber.cukeulator.test;

import java.util.Locale;

import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.cucumberexpressions.Transformer;

import static java.util.Locale.ENGLISH;

public class TypeRegistryConfiguration implements TypeRegistryConfigurer {

    @Override
    public Locale locale() {
        return ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        typeRegistry.defineParameterType(new ParameterType<Integer>(
                "digit",
                "[0-9]",
                Integer.class,
                new Transformer<Integer>() {
                    @Override
                    public Integer transform(String text) {
                        return Integer.parseInt(text);
                    }
                })
        );

        typeRegistry.defineParameterType(new ParameterType<Character>(
                "operator",
                "[+–x\\/=]",
                Character.class,
                new Transformer<Character>() {
                    @Override
                    public Character transform(String text) {
                        return text.charAt(0);
                    }
                })
        );
    }
}
