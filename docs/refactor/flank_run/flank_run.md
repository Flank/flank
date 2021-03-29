# Flank run refactor

## References

* https://github.com/Flank/flank/issues/1317 

# Insights

## Fix import dependencies

Only the cli commands can be aware of run package.
So any code inside the run package which is imported somewhere else then cli, 
must be reorganized and moved outside the run package.
