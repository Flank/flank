FROM openjdk:8-alpine

COPY . /

RUN apk add bash curl git zip && \
  curl -s "https://get.sdkman.io" | bash

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk version"

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk install kotlin"

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk install kscript"

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk install gradle"

RUN chmod +x /compile.sh

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && /compile.sh"

RUN chmod +x /entrypoint.sh

RUN chmod +x /sendMessage

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && /test.sh"

ENTRYPOINT /bin/bash /entrypoint.sh
