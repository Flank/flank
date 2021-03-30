# Flank high-level design

Flank architecture is based on the classic [PresentationDomainDataLayering](https://www.martinfowler.com/bliki/PresentationDomainDataLayering.html) proposed by Martin Fowler.

## Diagram

![architecture diagram](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/hld/flank-component-diagram.puml)

## Advantages

Basing on the model above it will be easy to:
* scale the presentation layer by adding necessary implementations.
* replace external data providers.
* separate third-party library code from the domain, for keeping code clean.
* consider each layer as a separate module if needed.
* deliver builds for different configurations.
* deliver the domain layer as a standalone library.
