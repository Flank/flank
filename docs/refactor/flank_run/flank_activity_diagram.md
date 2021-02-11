# Flank Activity Diagram 

![activity_diagram](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/refactor/flank_run/flank_activity_diagram.puml)

## Description

The diagram shows abstract view of all possible flank activities.

* The `#LightGreen` color relates to CLI commands.
* The `#LightBlue` color relates to domain scope.
* The default yellow color reserved for top-level functions that are not atomic and can be converted to `frame`.
* Frame can represent a package or rather the top-level function composed of other functions.
* The `#snow` color relates to low-level functions
* Low-level functions should be atomic ( can import only non-domain utils and third-party libraries? - consider ).
* Low-level functions cannot be converted to frame

The complete activity diagram will contain only low-level activities represented by `#snow` color.
All top-level functions represented by yellow activities contains hidden complexity and should be converted to frames.  

# Layers
```
presentation (CLI) -> domain -> utils -> data
```
