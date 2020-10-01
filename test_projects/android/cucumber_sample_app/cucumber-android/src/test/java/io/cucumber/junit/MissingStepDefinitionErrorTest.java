package io.cucumber.junit;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

;

public class MissingStepDefinitionErrorTest {

    @Test
    public void puts_snippet_with_preceeding_new_line_into_exception_message() {

        // given
        final String snippet = "some snippet";

        // when
        final String message = new MissingStepDefinitionError(snippet).getMessage();

        // then
        assertThat(message, is("\n\nsome snippet"));
    }
}
