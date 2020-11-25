---
name: Full suite IT test report
about: Report failed IT tests
title: Full Suite integration tests failed on master [{{ env.RUN_DATE }}]
labels: bug
---
### Integration Test failed on master 
**Timestamp:** {{ env.RUN_DATE }}
**Buildscan url for [{{ env.RUN_ID }}](https://github.com/Flank/flank/actions/runs/{{ env.RUN_ID }})**
{{ env.BUILD_SCAN_URL }}
