## v21.03.1
### Bug Fixes
- [#1680](https://github.com/Flank/flank/pull/1680) Fix output report error with dump shards ([pawelpasterz](https://github.com/pawelpasterz))
- [#1671](https://github.com/Flank/flank/pull/1671) Saving service account by flank script ([adamfilipow92](https://github.com/adamfilipow92))
- [#1662](https://github.com/Flank/flank/pull/1662) Fix NPE in GcToolResults ([pawelpasterz](https://github.com/pawelpasterz))
- [#1657](https://github.com/Flank/flank/pull/1657) Remove physical device from integration tests ([adamfilipow92](https://github.com/adamfilipow92))
- [#1656](https://github.com/Flank/flank/pull/1656) Snapshot publish by disabling closing staging artifacts ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1652](https://github.com/Flank/flank/pull/1652) Fix snapshot upload ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1649](https://github.com/Flank/flank/pull/1649) JUnitReport.xml only contained 50 test results ([dmytrodanylyk](https://github.com/dmytrodanylyk))
- [#1648](https://github.com/Flank/flank/pull/1648) Publishing to GithubPackages ([piotradamczyk5](https://github.com/piotradamczyk5))
### Features
- [#1675](https://github.com/Flank/flank/pull/1675) Return directories path in Flank Github action ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1673](https://github.com/Flank/flank/pull/1673) Run Flank Github action with config provided by user ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1663](https://github.com/Flank/flank/pull/1663) Added reading service account in Github action ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1660](https://github.com/Flank/flank/pull/1660) Add Flank GitHub action ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1659](https://github.com/Flank/flank/pull/1659) Remove flank-actions ([Sloox](https://github.com/Sloox))
- [#1655](https://github.com/Flank/flank/pull/1655) Update Flank Slack release notifier ([Sloox](https://github.com/Sloox))
### CI Changes
- [#1679](https://github.com/Flank/flank/pull/1679) Fix publishing snapshots ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1654](https://github.com/Flank/flank/pull/1654) Treat env json as a string ([pawelpasterz](https://github.com/pawelpasterz))
- [#1653](https://github.com/Flank/flank/pull/1653) Parse env object to JSON ([pawelpasterz](https://github.com/pawelpasterz))
- [#1639](https://github.com/Flank/flank/pull/1639) Update workflow file ([pawelpasterz](https://github.com/pawelpasterz))
### Documentation
- [#1621](https://github.com/Flank/flank/pull/1621) Corellium sandbox ([pawelpasterz](https://github.com/pawelpasterz))
- [#1670](https://github.com/Flank/flank/pull/1670) Add SDD for 1609 feature request ([pawelpasterz](https://github.com/pawelpasterz))
- [#1644](https://github.com/Flank/flank/pull/1644) Add Flank output research docs ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
### Tests update
- [#1661](https://github.com/Flank/flank/pull/1661) Implement logic to clear LocalGcs after each test ([pawelpasterz](https://github.com/pawelpasterz))
### Refactor
- [#1651](https://github.com/Flank/flank/pull/1651) Change packages of commands ([piotradamczyk5](https://github.com/piotradamczyk5))

## v21.03.0
### CI Changes
- [#1641](https://github.com/Flank/flank/pull/1641) Add missing subcommands ([pawelpasterz](https://github.com/pawelpasterz))
- [#1642](https://github.com/Flank/flank/pull/1642) Fix release workflow ([pawelpasterz](https://github.com/pawelpasterz))
- [#1640](https://github.com/Flank/flank/pull/1640) Fix gradle command ([pawelpasterz](https://github.com/pawelpasterz))
- [#1618](https://github.com/Flank/flank/pull/1618) Publish Flank-scripts to Github ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1614](https://github.com/Flank/flank/pull/1614) Publish flank to maven central ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
### Documentation
- [#1627](https://github.com/Flank/flank/pull/1627) Explore test-targets-for-shards SDD ([Sloox](https://github.com/Sloox), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1592](https://github.com/Flank/flank/pull/1592) Fix SDD for Github action ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1590](https://github.com/Flank/flank/pull/1590) Github action SDD proposal ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1583](https://github.com/Flank/flank/pull/1583) Flank-scripts ops package organization ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1566](https://github.com/Flank/flank/pull/1566) Flank-scripts command overview ([piotradamczyk5](https://github.com/piotradamczyk5))
### Features
- [#1635](https://github.com/Flank/flank/pull/1635) Part 2 of IT tests ([Sloox](https://github.com/Sloox), [pawelpasterz](https://github.com/pawelpasterz))
- [#1624](https://github.com/Flank/flank/pull/1624) Part 1 of multiple OS IT tests ([Sloox](https://github.com/Sloox))
- [#1559](https://github.com/Flank/flank/pull/1559) Add storing output as json ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
### Bug Fixes
- [#1631](https://github.com/Flank/flank/pull/1631) Update compare files in IT ([Sloox](https://github.com/Sloox), [pawelpasterz](https://github.com/pawelpasterz))
- [#1628](https://github.com/Flank/flank/pull/1628) Remove analytics from integration tests ([Sloox](https://github.com/Sloox))
- [#1626](https://github.com/Flank/flank/pull/1626) Spacing fix for yml file ([Sloox](https://github.com/Sloox))
- [#1620](https://github.com/Flank/flank/pull/1620) Authenticate in Github CLI before release Flank-Scripts ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1619](https://github.com/Flank/flank/pull/1619) Releasing flank-scripts ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1617](https://github.com/Flank/flank/pull/1617) Release job ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1612](https://github.com/Flank/flank/pull/1612) NPE on dumpShards ([adamfilipow92](https://github.com/adamfilipow92))
- [#1593](https://github.com/Flank/flank/pull/1593) Remove GameloopIT test for IOS ([Sloox](https://github.com/Sloox))
- [#1585](https://github.com/Flank/flank/pull/1585) Print Flank version only on run command ([adamfilipow92](https://github.com/adamfilipow92))
- [#1581](https://github.com/Flank/flank/pull/1581) Fixes for Workflow files & IT tests ([Sloox](https://github.com/Sloox), [adamfilipow92](https://github.com/adamfilipow92))
- [#1575](https://github.com/Flank/flank/pull/1575) Increase heap size ([pawelpasterz](https://github.com/pawelpasterz))
- [#1564](https://github.com/Flank/flank/pull/1564) Windows issues ([Sloox](https://github.com/Sloox))
- [#1560](https://github.com/Flank/flank/pull/1560) Fix IT workflow for windows ([Sloox](https://github.com/Sloox))
- [#1557](https://github.com/Flank/flank/pull/1557) Move shards json to result directory ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
### Refactor
- [#1589](https://github.com/Flank/flank/pull/1589) Move function to files which use them ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1584](https://github.com/Flank/flank/pull/1584) Reorganize domain layer of flank scripts ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1574](https://github.com/Flank/flank/pull/1574) Refactor CLI of Flank-scripts ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1572](https://github.com/Flank/flank/pull/1572) Flank scripts features not depends on each other ([adamfilipow92](https://github.com/adamfilipow92))
- [#1533](https://github.com/Flank/flank/pull/1533) Refactor flank scripts ([jan-gogo](https://github.com/jan-gogo), [adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))

## v21.02.0
### Bug Fixes
- [#1547](https://github.com/Flank/flank/pull/1547) IT test fix ()
- [#1540](https://github.com/Flank/flank/pull/1540) Dummy var for pr ([Sloox](https://github.com/Sloox))
- [#1546](https://github.com/Flank/flank/pull/1546) Fix flaky FilesTest ([pawelpasterz](https://github.com/pawelpasterz))
- [#1526](https://github.com/Flank/flank/pull/1526) Windows Integration tests ([Sloox](https://github.com/Sloox))
- [#1521](https://github.com/Flank/flank/pull/1521) Fix NPE and logging ([pawelpasterz](https://github.com/pawelpasterz))
- [#1506](https://github.com/Flank/flank/pull/1506) Improve Sentry logging for tests ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1470](https://github.com/Flank/flank/pull/1470) Resolve Fails on Windows ([Sloox](https://github.com/Sloox), [pawelpasterz](https://github.com/pawelpasterz))
- [#1490](https://github.com/Flank/flank/pull/1490) Fix incorrect method usage on master ([pawelpasterz](https://github.com/pawelpasterz))
- [#1465](https://github.com/Flank/flank/pull/1465) CI failures on performance tests ([piotradamczyk5](https://github.com/piotradamczyk5))
### CI Changes
- [#1537](https://github.com/Flank/flank/pull/1537) Integration Test for all OS's ([Sloox](https://github.com/Sloox))
- [#1534](https://github.com/Flank/flank/pull/1534) Added Windows workflow ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1535](https://github.com/Flank/flank/pull/1535) Windows integration test ([Sloox](https://github.com/Sloox))
- [#1468](https://github.com/Flank/flank/pull/1468) Use pointer workflows to preserve run date ([pawelpasterz](https://github.com/pawelpasterz), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1456](https://github.com/Flank/flank/pull/1456) Update config for CLA ([piotradamczyk5](https://github.com/piotradamczyk5))
### Tests update
- [#1525](https://github.com/Flank/flank/pull/1525) Add missing iOS options in IT ([pawelpasterz](https://github.com/pawelpasterz))
- [#1514](https://github.com/Flank/flank/pull/1514) Update compare file and make url option not required ([pawelpasterz](https://github.com/pawelpasterz), [adamfilipow92](https://github.com/adamfilipow92))
### Features
- [#1448](https://github.com/Flank/flank/pull/1448) Filtering test configurations (iOS TestPlans) ([axelzuziak-gogo](https://github.com/axelzuziak-gogo), [jan-gogo](https://github.com/jan-gogo))
- [#1504](https://github.com/Flank/flank/pull/1504) Sending configuration to mixpanel ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1474](https://github.com/Flank/flank/pull/1474) Amend pre-commit hook ([Sloox](https://github.com/Sloox))
- [#1471](https://github.com/Flank/flank/pull/1471) Migrate from bugsnag to sentry ([adamfilipow92](https://github.com/adamfilipow92))
- [#1428](https://github.com/Flank/flank/pull/1428) Rewrite pre-commit hook & ktlint apply to idea into Flank Scripts ([Sloox](https://github.com/Sloox))
### Documentation
- [#1495](https://github.com/Flank/flank/pull/1495) Choose analytics tool ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
### Refactor
- [#1450](https://github.com/Flank/flank/pull/1450) Enable properties file in flank scripts ([pawelpasterz](https://github.com/pawelpasterz), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1473](https://github.com/Flank/flank/pull/1473) Update picocli to latest version ([pawelpasterz](https://github.com/pawelpasterz), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1466](https://github.com/Flank/flank/pull/1466) Change packages in the common module ([pawelpasterz](https://github.com/pawelpasterz))

## v21.01.1
### CI Changes
- [#1451](https://github.com/Flank/flank/pull/1451) Added CLA assistant ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1447](https://github.com/Flank/flank/pull/1447) Fix dependency update workflow ([pawelpasterz](https://github.com/pawelpasterz))
- [#1443](https://github.com/Flank/flank/pull/1443) Add missing step ([pawelpasterz](https://github.com/pawelpasterz))
### Bug Fixes
- [#1453](https://github.com/Flank/flank/pull/1453) Fix problem with test-runner-class ([piotradamczyk5](https://github.com/piotradamczyk5))

## v21.01.0
### CI Changes
- [#1439](https://github.com/Flank/flank/pull/1439) Remove sanity checks and change full suite IT OS to macOS ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1433](https://github.com/Flank/flank/pull/1433) Change IT workflow comment ([pawelpasterz](https://github.com/pawelpasterz))
- [#1424](https://github.com/Flank/flank/pull/1424) Auto update firebase api client ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1412](https://github.com/Flank/flank/pull/1412) 1411 implement version ([pawelpasterz](https://github.com/pawelpasterz))
- [#1409](https://github.com/Flank/flank/pull/1409) Trigger integration tests from pr comment ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1385](https://github.com/Flank/flank/pull/1385) Get rid of binaries submodule ([jan-gogo](https://github.com/jan-gogo), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1400](https://github.com/Flank/flank/pull/1400) Change gradle command ([pawelpasterz](https://github.com/pawelpasterz))
- [#1398](https://github.com/Flank/flank/pull/1398) Add create google account config step ([pawelpasterz](https://github.com/pawelpasterz))
- [#1353](https://github.com/Flank/flank/pull/1353) Implement workflow for full suite IT (cron + manual) ([pawelpasterz](https://github.com/pawelpasterz), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1378](https://github.com/Flank/flank/pull/1378) Update GH API ([pawelpasterz](https://github.com/pawelpasterz))
### Features
- [#1436](https://github.com/Flank/flank/pull/1436) 1422 autodetect new gcloud features ([pawelpasterz](https://github.com/pawelpasterz))
- [#1429](https://github.com/Flank/flank/pull/1429) Change uploading file output ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1426](https://github.com/Flank/flank/pull/1426) Support for iOS gameloop tests ([Sloox](https://github.com/Sloox), [jan-gogo](https://github.com/jan-gogo), [adamfilipow92](https://github.com/adamfilipow92))
- [#1421](https://github.com/Flank/flank/pull/1421) Default project Id - use GOOGLE_APPLICATION_CREDENTIALS first, then GOOGLE_CLOUD_PROJECT ([pawelpasterz](https://github.com/pawelpasterz))
- [#1404](https://github.com/Flank/flank/pull/1404) Add basic gameloop IT for Android ([Sloox](https://github.com/Sloox), [adamfilipow92](https://github.com/adamfilipow92))
- [#1393](https://github.com/Flank/flank/pull/1393) Upload matrix ids ([Sloox](https://github.com/Sloox), [jan-gogo](https://github.com/jan-gogo))
- [#1362](https://github.com/Flank/flank/pull/1362) Add logic to verify xml results ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1321](https://github.com/Flank/flank/pull/1321) IOS support for testplans ([axelzuziak-gogo](https://github.com/axelzuziak-gogo), [jan-gogo](https://github.com/jan-gogo))
- [#1375](https://github.com/Flank/flank/pull/1375) Add new output style to print smaller output ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1370](https://github.com/Flank/flank/pull/1370) Add support for "fail fast" feature on FTL ([RainNapper](https://github.com/RainNapper))
### Refactor
- [#1432](https://github.com/Flank/flank/pull/1432) Small changes in flank-scripts module ([pawelpasterz](https://github.com/pawelpasterz))
- [#1418](https://github.com/Flank/flank/pull/1418) Create module with shared utilities ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1417](https://github.com/Flank/flank/pull/1417) Update firebase_apis module ([pawelpasterz](https://github.com/pawelpasterz))
### Bug Fixes
- [#1414](https://github.com/Flank/flank/pull/1414) Add attempts to performance test ([jan-gogo](https://github.com/jan-gogo))
- [#1410](https://github.com/Flank/flank/pull/1410) Fix integration tests on CI. ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1413](https://github.com/Flank/flank/pull/1413) Fix calculate shards test ([pawelpasterz](https://github.com/pawelpasterz), [jan-gogo](https://github.com/jan-gogo))
- [#1405](https://github.com/Flank/flank/pull/1405) CancelCommandRuns should works independent ([Sloox](https://github.com/Sloox), [adamfilipow92](https://github.com/adamfilipow92))
- [#1395](https://github.com/Flank/flank/pull/1395) Print args when iOS tests are not found ([jan-gogo](https://github.com/jan-gogo), [adamfilipow92](https://github.com/adamfilipow92))
- [#1387](https://github.com/Flank/flank/pull/1387) Fix NPE ([pawelpasterz](https://github.com/pawelpasterz), [jan-gogo](https://github.com/jan-gogo))
- [#1372](https://github.com/Flank/flank/pull/1372) Fix Flank Scripts version verification tasks ([piotradamczyk5](https://github.com/piotradamczyk5))
### Documentation
- [#1401](https://github.com/Flank/flank/pull/1401) Update orchestrator version in docs ([adamfilipow92](https://github.com/adamfilipow92))
- [#1380](https://github.com/Flank/flank/pull/1380) Added documentation about handling #1374 issue ([piotradamczyk5](https://github.com/piotradamczyk5))
### Tests update
- [#1316](https://github.com/Flank/flank/pull/1316) [IT] Add test cases ([pawelpasterz](https://github.com/pawelpasterz), [adamfilipow92](https://github.com/adamfilipow92))

## v20.12.0
### Bug Fixes
- [#1366](https://github.com/Flank/flank/pull/1366) Generation of release notes ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1357](https://github.com/Flank/flank/pull/1357) Fix release Action ([Sloox](https://github.com/Sloox), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1333](https://github.com/Flank/flank/pull/1333) Publishing Flank snapshot ([Sloox](https://github.com/Sloox), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1338](https://github.com/Flank/flank/pull/1338) Fix printing supported versions id  ([adamfilipow92](https://github.com/adamfilipow92))
- [#1329](https://github.com/Flank/flank/pull/1329) Uploading performance metrics for multiple matrices ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1326](https://github.com/Flank/flank/pull/1326) Downloading performance Metrics ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1323](https://github.com/Flank/flank/pull/1323) Nested subdirectories in results-dir ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1307](https://github.com/Flank/flank/pull/1307) Integration tests on windows ([pawelpasterz](https://github.com/pawelpasterz), [adamfilipow92](https://github.com/adamfilipow92))
- [#1301](https://github.com/Flank/flank/pull/1301) Handling broken token issues ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1298](https://github.com/Flank/flank/pull/1298) Fix incorrect printed shards number ([pawelpasterz](https://github.com/pawelpasterz))
- [#1295](https://github.com/Flank/flank/pull/1295) Print formatted message instead of throwing NPE ([pawelpasterz](https://github.com/pawelpasterz))
- [#1283](https://github.com/Flank/flank/pull/1283) Integration tests on windows ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1275](https://github.com/Flank/flank/pull/1275) Fix getting PR number on pull_request_review ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1272](https://github.com/Flank/flank/pull/1272) Fix integration test counter and trigger ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1219](https://github.com/Flank/flank/pull/1219) #842 support for test targets flag in multiple testbundles xctest ([axelzuziak-gogo](https://github.com/axelzuziak-gogo), [jan-gogo](https://github.com/jan-gogo))
- [#1261](https://github.com/Flank/flank/pull/1261) Add additional index to matrix when multiple test runs ([pawelpasterz](https://github.com/pawelpasterz), [adamfilipow92](https://github.com/adamfilipow92))
- [#1250](https://github.com/Flank/flank/pull/1250) Fix running flank workflows on Windows ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1247](https://github.com/Flank/flank/pull/1247) Flank web documentation link ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1243](https://github.com/Flank/flank/pull/1243) Disable cache for :test_runner:shadowJar task ([pawelpasterz](https://github.com/pawelpasterz))
- [#1233](https://github.com/Flank/flank/pull/1233) Fix withClassName filter ([pawelpasterz](https://github.com/pawelpasterz))
- [#1221](https://github.com/Flank/flank/pull/1221) Always dump shards ([pawelpasterz](https://github.com/pawelpasterz), [adamfilipow92](https://github.com/adamfilipow92))
- [#1236](https://github.com/Flank/flank/pull/1236) Empty branch name in workflows ([jan-gogo](https://github.com/jan-gogo))
- [#1232](https://github.com/Flank/flank/pull/1232) Test_runner:resolveArtifacts task ([jan-gogo](https://github.com/jan-gogo))
- [#1225](https://github.com/Flank/flank/pull/1225) Release job missing env ()
- [#1188](https://github.com/Flank/flank/pull/1188) Revert "feat: Dump shards and upload on every run (#1171)" ([pawelpasterz](https://github.com/pawelpasterz))
- [#1177](https://github.com/Flank/flank/pull/1177) Fix flank-scripts test ([pawelpasterz](https://github.com/pawelpasterz))
- [#1167](https://github.com/Flank/flank/pull/1167) Flank action ([Sloox](https://github.com/Sloox))
- [#1161](https://github.com/Flank/flank/pull/1161) Release notes slack tweaks ([Sloox](https://github.com/Sloox))
- [#1164](https://github.com/Flank/flank/pull/1164) Dependabot security issues ([Sloox](https://github.com/Sloox))
### Refactor
- [#1350](https://github.com/Flank/flank/pull/1350) Ios test artifacts cleanup #1345 ([axelzuziak-gogo](https://github.com/axelzuziak-gogo), [jan-gogo](https://github.com/jan-gogo))
- [#1339](https://github.com/Flank/flank/pull/1339) Simplify beforeRunTests return singature ([jan-gogo](https://github.com/jan-gogo))
- [#1281](https://github.com/Flank/flank/pull/1281) Prepare flank codebase for supporting iOS testplans ([axelzuziak-gogo](https://github.com/axelzuziak-gogo), [jan-gogo](https://github.com/jan-gogo))
- [#1246](https://github.com/Flank/flank/pull/1246) Rewrite scripts to Kotlin ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
### CI Changes
- [#1361](https://github.com/Flank/flank/pull/1361) Publishing and downloading flank scripts ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1352](https://github.com/Flank/flank/pull/1352) Make flank release more automatically ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1344](https://github.com/Flank/flank/pull/1344) Add directory filter for IT ([pawelpasterz](https://github.com/pawelpasterz))
- [#1310](https://github.com/Flank/flank/pull/1310) Copy properties from issue to pull request ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1320](https://github.com/Flank/flank/pull/1320) Added pre-commit-hook ([Sloox](https://github.com/Sloox))
- [#1286](https://github.com/Flank/flank/pull/1286) Update existing bot comment ([pawelpasterz](https://github.com/pawelpasterz))
- [#1269](https://github.com/Flank/flank/pull/1269) Add build scan for ubuntu workflow ([pawelpasterz](https://github.com/pawelpasterz))
- [#1265](https://github.com/Flank/flank/pull/1265) Refresh integration tests ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1263](https://github.com/Flank/flank/pull/1263) Github Action optimizations ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1253](https://github.com/Flank/flank/pull/1253) Use Github app token for creating release notes ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1248](https://github.com/Flank/flank/pull/1248) Update deprecated options ::set-env and ::add-path ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1240](https://github.com/Flank/flank/pull/1240) Add current date to dependecy PR ([pawelpasterz](https://github.com/pawelpasterz))
- [#1148](https://github.com/Flank/flank/pull/1148) Add scripts for test artifacts managment ([jan-gogo](https://github.com/jan-gogo), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1210](https://github.com/Flank/flank/pull/1210) Enable RC versions for gradle (dependency update task) ([pawelpasterz](https://github.com/pawelpasterz))
- [#1158](https://github.com/Flank/flank/pull/1158) Fix test artifacts url ([jan-gogo](https://github.com/jan-gogo))
- [#1152](https://github.com/Flank/flank/pull/1152) Added WSL workflow ([Sloox](https://github.com/Sloox), [piotradamczyk5](https://github.com/piotradamczyk5))
### Documentation
- [#1355](https://github.com/Flank/flank/pull/1355) Add documentation about code review by step ([jan-gogo](https://github.com/jan-gogo))
- [#1311](https://github.com/Flank/flank/pull/1311) Update documentations & Add Headings ([Sloox](https://github.com/Sloox))
- [#1222](https://github.com/Flank/flank/pull/1222) Add flank website code ([jan-gogo](https://github.com/jan-gogo))
- [#1229](https://github.com/Flank/flank/pull/1229) Update documentation ([pawelpasterz](https://github.com/pawelpasterz))
- [#1215](https://github.com/Flank/flank/pull/1215) Added documentation about missing options ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1190](https://github.com/Flank/flank/pull/1190) Added documentation about Cucumber support ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1181](https://github.com/Flank/flank/pull/1181) Investigation incorrect outcome for flaky tests ([pawelpasterz](https://github.com/pawelpasterz), [adamfilipow92](https://github.com/adamfilipow92))
- [#1173](https://github.com/Flank/flank/pull/1173) Add FAQ section about sync problems ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1131](https://github.com/Flank/flank/pull/1131) Investigate flank options ([jan-gogo](https://github.com/jan-gogo), [adamfilipow92](https://github.com/adamfilipow92))
### Features
- [#1325](https://github.com/Flank/flank/pull/1325) IOS GameLoop addition ([Sloox](https://github.com/Sloox), [axelzuziak-gogo](https://github.com/axelzuziak-gogo))
- [#1299](https://github.com/Flank/flank/pull/1299) Add test targets for shard ([Sloox](https://github.com/Sloox), [pawelpasterz](https://github.com/pawelpasterz))
- [#1292](https://github.com/Flank/flank/pull/1292) Added Performance Metrics for Android ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1289](https://github.com/Flank/flank/pull/1289) Add ios app for gameloop ([Sloox](https://github.com/Sloox))
- [#1291](https://github.com/Flank/flank/pull/1291) Add --test-special-entitlements for iOS ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1266](https://github.com/Flank/flank/pull/1266) IOS add directories-to-pull  ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1287](https://github.com/Flank/flank/pull/1287) Ios scenario numbers ([Sloox](https://github.com/Sloox))
- [#1284](https://github.com/Flank/flank/pull/1284) Add Type to iOS ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1262](https://github.com/Flank/flank/pull/1262) Gameloop addition ([Sloox](https://github.com/Sloox))
- [#1273](https://github.com/Flank/flank/pull/1273) Add Additional Ipas option to iOS ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1267](https://github.com/Flank/flank/pull/1267) Add Other Files for iOS ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1255](https://github.com/Flank/flank/pull/1255) Obb file addition ([Sloox](https://github.com/Sloox))
- [#1256](https://github.com/Flank/flank/pull/1256) Update test shard limits ([adamfilipow92](https://github.com/adamfilipow92))
- [#1244](https://github.com/Flank/flank/pull/1244) Added scenario-numbers ([Sloox](https://github.com/Sloox))
- [#1238](https://github.com/Flank/flank/pull/1238) Scenario Labels ([Sloox](https://github.com/Sloox))
- [#1230](https://github.com/Flank/flank/pull/1230) Add type ([Sloox](https://github.com/Sloox))
- [#1186](https://github.com/Flank/flank/pull/1186) Grant permissions addition ([Sloox](https://github.com/Sloox))
- [#1214](https://github.com/Flank/flank/pull/1214) Added printing missing options ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1175](https://github.com/Flank/flank/pull/1175) Android integration tests run on non wsl windows ([Sloox](https://github.com/Sloox), [adamfilipow92](https://github.com/adamfilipow92))
- [#1171](https://github.com/Flank/flank/pull/1171) Dump shards and upload on every run ([adamfilipow92](https://github.com/adamfilipow92))
- [#1163](https://github.com/Flank/flank/pull/1163) Integration tests run when pr is approved and not draft ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))
- [#1159](https://github.com/Flank/flank/pull/1159) Improve error message on matrix failures ([pawelpasterz](https://github.com/pawelpasterz), [adamfilipow92](https://github.com/adamfilipow92))
- [#1133](https://github.com/Flank/flank/pull/1133) Auto update dependencies ([piotradamczyk5](https://github.com/piotradamczyk5))
### Tests update
- [#1300](https://github.com/Flank/flank/pull/1300) Add flank multi test targets example ([jan-gogo](https://github.com/jan-gogo))
- [#1274](https://github.com/Flank/flank/pull/1274) Disable iOS test for Windows ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1187](https://github.com/Flank/flank/pull/1187) Remove verification test for edge cases of incorrect outcome in flaky tests ([adamfilipow92](https://github.com/adamfilipow92))
- [#1174](https://github.com/Flank/flank/pull/1174) Added cucumber sample app for testing #1118 ([piotradamczyk5](https://github.com/piotradamczyk5))

## v20.09.3
### Bug Fixes
- [#1151](https://github.com/Flank/flank/pull/1151) Update flank-scripts serialization ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1149](https://github.com/Flank/flank/pull/1149) Flank-scripts next release tag generation ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1138](https://github.com/Flank/flank/pull/1138) Fix cost report ([pawelpasterz](https://github.com/pawelpasterz))
- [#1137](https://github.com/Flank/flank/pull/1137) Running Code coverage job ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1130](https://github.com/Flank/flank/pull/1130) Bump action versioning ([Sloox](https://github.com/Sloox))
- [#1124](https://github.com/Flank/flank/pull/1124) Reflect gclouds outcome for robo tests ([pawelpasterz](https://github.com/pawelpasterz))
- [#1121](https://github.com/Flank/flank/pull/1121) Dockerfile fix for slack message send ([Sloox](https://github.com/Sloox))
- [#1116](https://github.com/Flank/flank/pull/1116) Fixes release workflows ([Sloox](https://github.com/Sloox))
### Features
- [#1141](https://github.com/Flank/flank/pull/1141) Add Java 15 compile support ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1126](https://github.com/Flank/flank/pull/1126) Allow Flank using different environment variables between test-apks ([adamfilipow92](https://github.com/adamfilipow92))
- [#1078](https://github.com/Flank/flank/pull/1078) Add proguard initial step into Flank configuration ([Sloox](https://github.com/Sloox))
- [#1125](https://github.com/Flank/flank/pull/1125) Readded shadowjar minimization ([Sloox](https://github.com/Sloox))
- [#1109](https://github.com/Flank/flank/pull/1109) Integration tests for Flank ([adamfilipow92](https://github.com/adamfilipow92))
### Documentation
- [#1139](https://github.com/Flank/flank/pull/1139) Optimization docs addition ([Sloox](https://github.com/Sloox))
- [#1136](https://github.com/Flank/flank/pull/1136) Update documentation related with test-targets-always-run feature ([pawelpasterz](https://github.com/pawelpasterz))
### CI Changes
- [#1114](https://github.com/Flank/flank/pull/1114) Add Windows workflow ([Sloox](https://github.com/Sloox))
- [#1095](https://github.com/Flank/flank/pull/1095) Add new bash scripts for test artifacts generation ([axelzuziak-gogo](https://github.com/axelzuziak-gogo), [jan-gogo](https://github.com/jan-gogo))
### Refactor
- [#1113](https://github.com/Flank/flank/pull/1113) Use single settings.gradle for flank projects ([jan-gogo](https://github.com/jan-gogo), [piotradamczyk5](https://github.com/piotradamczyk5))

## v20.09.2
### Features
- [#1111](https://github.com/Flank/flank/pull/1111) Check if gcs path exist before run ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1110](https://github.com/Flank/flank/pull/1110) Finialize the slack sending integration with actions ([Sloox](https://github.com/Sloox))
- [#1108](https://github.com/Flank/flank/pull/1108) Support for robo tests without robo script ([pawelpasterz](https://github.com/pawelpasterz))
- [#1104](https://github.com/Flank/flank/pull/1104) Send slack message for releases ([Sloox](https://github.com/Sloox))
- [#1097](https://github.com/Flank/flank/pull/1097) Print the matrices web link at the end of a run ([adamfilipow92](https://github.com/adamfilipow92))
### Bug Fixes
- [#1107](https://github.com/Flank/flank/pull/1107) Running iOS test on Linux ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1059](https://github.com/Flank/flank/pull/1059) Shards and Tests count do not match ([pawelpasterz](https://github.com/pawelpasterz), [adamfilipow92](https://github.com/adamfilipow92))
- [#1067](https://github.com/Flank/flank/pull/1067) Matrix path not found in json ([adamfilipow92](https://github.com/adamfilipow92), [piotradamczyk5](https://github.com/piotradamczyk5))

## v20.09.1
### Bug Fixes
- [#1100](https://github.com/Flank/flank/pull/1100) Fix gcs files validation ([pawelpasterz](https://github.com/pawelpasterz))
- [#1093](https://github.com/Flank/flank/pull/1093) Flank release bot formatting ([Sloox](https://github.com/Sloox))
- [#1091](https://github.com/Flank/flank/pull/1091) Add missing authors to release notes ([adamfilipow92](https://github.com/adamfilipow92))

## v20.09.0
### Bug Fixes
- [#1087](https://github.com/Flank/flank/pull/1087) Fix release action ([adamfilipow92](https://github.com/adamfilipow92))
- [#1080](https://github.com/Flank/flank/pull/1080) Flaky Flank-scripts test ([Sloox](https://github.com/Sloox))
- [#1079](https://github.com/Flank/flank/pull/1079) Fix slack notification in slack ([Sloox](https://github.com/Sloox))
- [#1054](https://github.com/Flank/flank/pull/1054) Firebase refresh fails when test zip file doesn't exist (#1052) ([axelzuziak-gogo](https://github.com/axelzuziak-gogo), [jan-gogo](https://github.com/jan-gogo))
- [#1070](https://github.com/Flank/flank/pull/1070) Avoid fast fail while creating a bucket ([adamfilipow92](https://github.com/adamfilipow92))
- [#1066](https://github.com/Flank/flank/pull/1066) Bad validation of `local-result-dir` by Doctor ([piotradamczyk5](https://github.com/piotradamczyk5))
### CI Changes
- [#1058](https://github.com/Flank/flank/pull/1058) Post slack message about releases ([Sloox](https://github.com/Sloox))

## v20.08.4
### Features
- [#1056](https://github.com/Flank/flank/pull/1056) Added validation of smart-flank-gcs-path to not override different junit results ([adamfilipow92](https://github.com/adamfilipow92))
- [#1040](https://github.com/Flank/flank/pull/1040) Add test axis value column to summary table ([jan-gogo](https://github.com/jan-gogo))
- [#1043](https://github.com/Flank/flank/pull/1043) Add printing messages for MatrixCanceledError and InfrastructureError ([pawelpasterz](https://github.com/pawelpasterz))
- [#1042](https://github.com/Flank/flank/pull/1042) Added option for default test time and average time for smart shard ([piotradamczyk5](https://github.com/piotradamczyk5))
### Tests update
- [#1055](https://github.com/Flank/flank/pull/1055) Added missing test t cover duplicated tests issue ([piotradamczyk5](https://github.com/piotradamczyk5))
### Bug Fixes
- [#1041](https://github.com/Flank/flank/pull/1041) Invoking flank yml on gcloud cli ([adamfilipow92](https://github.com/adamfilipow92))
- [#1053](https://github.com/Flank/flank/pull/1053) Avoid releasing on document changes only ([Sloox](https://github.com/Sloox))
- [#1048](https://github.com/Flank/flank/pull/1048) Flank finding and running duplicate tests ([MatthewTPage](https://github.com/MatthewTPage))
- [#1051](https://github.com/Flank/flank/pull/1051) PR check from fork ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1044](https://github.com/Flank/flank/pull/1044) Master not compiling ([Sloox](https://github.com/Sloox))
- [#994](https://github.com/Flank/flank/pull/994) Size annotations support ([pgreze](https://github.com/pgreze))
### Refactor
- [#1047](https://github.com/Flank/flank/pull/1047) MatrixMap immutability ([Sloox](https://github.com/Sloox))
- [#1028](https://github.com/Flank/flank/pull/1028) Savedmatrix immutability change ([Sloox](https://github.com/Sloox))
### Documentation
- [#1009](https://github.com/Flank/flank/pull/1009) Avoid multiple identical lines printing ([adamfilipow92](https://github.com/adamfilipow92))

## v20.08.3
### CI Changes
- [#1031](https://github.com/Flank/flank/pull/1031) Disable publishing snapshot to Github Packages ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1022](https://github.com/Flank/flank/pull/1022) Publish to GitHub packages ([piotradamczyk5](https://github.com/piotradamczyk5))
### Bug Fixes
- [#1027](https://github.com/Flank/flank/pull/1027) OtherNativeCrash NPE ([jan-gogo](https://github.com/jan-gogo))
- [#1023](https://github.com/Flank/flank/pull/1023) Release notes message at GitHub release ([piotradamczyk5](https://github.com/piotradamczyk5))
### Refactor
- [#1025](https://github.com/Flank/flank/pull/1025) Update kotlin version to 1.4 ([pawelpasterz](https://github.com/pawelpasterz))

## v20.08.2
### Features
- [#1020](https://github.com/Flank/flank/pull/1020) Make release notes more organized ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1013](https://github.com/Flank/flank/pull/1013) Generating docs before release ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1012](https://github.com/Flank/flank/pull/1012) Add ip-blocks command to test-environment command ([pawelpasterz](https://github.com/pawelpasterz))
- [#999](https://github.com/Flank/flank/pull/999) Implement ip-blocks list command ([pawelpasterz](https://github.com/pawelpasterz))
- [#996](https://github.com/Flank/flank/pull/996) Auto generate release notes for next release ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#995](https://github.com/Flank/flank/pull/995) Implement command `models describe` for ios/android ([adamfilipow92](https://github.com/adamfilipow92))
- [#991](https://github.com/Flank/flank/pull/991) Validate orientation and fail fast ([Sloox](https://github.com/Sloox))
- [#969](https://github.com/Flank/flank/pull/969) Add locales description command for ios and android ([adamfilipow92](https://github.com/adamfilipow92))
- [#992](https://github.com/Flank/flank/pull/992) Update google api ([jan-gogo](https://github.com/jan-gogo))
- [#988](https://github.com/Flank/flank/pull/988) Add versions description command for ios and android ([adamfilipow92](https://github.com/adamfilipow92))
### Refactor
- [#1018](https://github.com/Flank/flank/pull/1018) Fetch artifacts async ([pawelpasterz](https://github.com/pawelpasterz))
### Bug Fixes
- [#919](https://github.com/Flank/flank/pull/919) Rate limit exceeded ([pawelpasterz](https://github.com/pawelpasterz), [jan-gogo](https://github.com/jan-gogo))
- [#1005](https://github.com/Flank/flank/pull/1005) Generation of release notes ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1007](https://github.com/Flank/flank/pull/1007) Failing tests ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#990](https://github.com/Flank/flank/pull/990) Exclusion of @Suppress test ([piotradamczyk5](https://github.com/piotradamczyk5))
### CI Changes
- [#1015](https://github.com/Flank/flank/pull/1015) Update mergify configuration ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#1011](https://github.com/Flank/flank/pull/1011) Generate release notes for Github release description ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#989](https://github.com/Flank/flank/pull/989) Check if valid title is used in PR ([piotradamczyk5](https://github.com/piotradamczyk5))
### Documentation
- [#987](https://github.com/Flank/flank/pull/987) Analytics addition to readme ([Sloox](https://github.com/Sloox))

## v20.08.1
- [#978](https://github.com/Flank/flank/pull/978) Firebaseopensource.com addition ([sloox](https://github.com/Sloox))
- [#937](https://github.com/Flank/flank/pull/968) Improve error message on iOS when test or xctestrun-file not found ([sloox](https://github.com/Sloox))
- [#952](https://github.com/Flank/flank/pull/952) Fix version printing on Flank release ([sloox](https://github.com/Sloox))
- [#950](https://github.com/Flank/flank/pull/950) Fix crash when --legacy-junit-result set. ([adamfilipow92](https://github.com/adamfilipow92))
- [#948](https://github.com/Flank/flank/pull/948) Increment retry tries and change sync tag for jfrogSync. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#946](https://github.com/Flank/flank/pull/946) Added tests for flank scripts. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#935](https://github.com/Flank/flank/pull/935) Process junit xml even when full junit is not enabled. ([kozaxinan](https://github.com/kozaxinan))
- [#962](https://github.com/Flank/flank/pull/962) Make table text left aligned. ([pawelpasterz](https://github.com/pawelpasterz))
- [#965](https://github.com/Flank/flank/pull/965) Fast fail when full-junit-result and legacy-junit-result. ([adamfilipow92](https://github.com/adamfilipow92))
- [#970](https://github.com/Flank/flank/pull/970) Fixing Flood of snapshot  releases. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#972](https://github.com/Flank/flank/pull/972) Fix printing release version. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#964](https://github.com/Flank/flank/pull/964) Implement network-profiles describe feature. ([pawelpasterz](https://github.com/pawelpasterz))

## v20.08.0
- [#890](https://github.com/Flank/flank/pull/890) Convert bitrise ubuntu workflow into GitHub actions. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#876](https://github.com/Flank/flank/pull/876) Added option to print Android available devices to test against. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#895](https://github.com/Flank/flank/pull/895) Added option to print iOS available devices to test against. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#894](https://github.com/Flank/flank/pull/894) Added option to print Android available versions to test against. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#897](https://github.com/Flank/flank/pull/897) Added option to print iOS available versions to test against. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#901](https://github.com/Flank/flank/pull/901) Added option to print Android and iOS available test-environment. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#904](https://github.com/Flank/flank/pull/904) Added option to print provided software. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#906](https://github.com/Flank/flank/pull/906) Added option to print network profiles. ([adamfilipow92](https://github.com/adamfilipow92))
- [#908](https://github.com/Flank/flank/pull/908) Added option to print iOS available locales to test against.  ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#906](https://github.com/Flank/flank/pull/909) Added option to print iOS and Android screen orientations. ([adamfilipow92](https://github.com/adamfilipow92))
- [#907](https://github.com/Flank/flank/pull/907) Added option to print Android available locales to test against. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#913](https://github.com/Flank/flank/pull/913) Add Gradle Enterprise API example. ([pawelpasterz](https://github.com/pawelpasterz))
- [#916](https://github.com/Flank/flank/pull/916) Test artifacts monorepo. ([jan-gogo](https://github.com/jan-gogo))
- [#910](https://github.com/Flank/flank/pull/910) Migrate Bitrise release workflow into GitHub actions.  ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#915](https://github.com/Flank/flank/pull/915) Update virtual devices sharding limit. ([adamfilipow92](https://github.com/adamfilipow92))
- [#920](https://github.com/Flank/flank/pull/920) Improve .yml validation on `doctor` command. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#934](https://github.com/Flank/flank/pull/934) Delete incorrect flank snapshot labels. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#926](https://github.com/Flank/flank/pull/926) Flank should reflect gcloud exit codes. ([adamfilipow92](https://github.com/adamfilipow92))
- [#917](https://github.com/Flank/flank/pull/917) Fix an incorrect outcome. ([pawelpasterz](https://github.com/pawelpasterz))
- [#939](https://github.com/Flank/flank/pull/939) Run *list commands when flank.yml not found should display right output. ([adamfilipow92](https://github.com/adamfilipow92))

## v20.07.0
- [#857](https://github.com/Flank/flank/pull/857) Added multimodule setup for test app. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#837](https://github.com/Flank/flank/pull/837) Added obfuscate option to dump shards. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#868](https://github.com/Flank/flank/pull/868) Restored weblinks to all test results, not just failures. ([rainnapper](https://github.com/rainnapper))
- [#828](https://github.com/Flank/flank/pull/828) Store test results in gcloud bucket. ([adamfilipow92](https://github.com/adamfilipow92))
- [#865](https://github.com/Flank/flank/pull/865) Flank needs to respect the timeout value as that's a cap for billing purposes. ([adamfilipow92](https://github.com/adamfilipow92), [pawelpasterz](https://github.com/pawelpasterz))
- [#866](https://github.com/Flank/flank/pull/866) Fix printing all matrix links. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#862](https://github.com/Flank/flank/pull/862) Added printing outcome details. ([piotradamczyk5](https://github.com/piotradamczyk5), [jan-gogo](https://github.com/jan-gogo))
- [#876](https://github.com/Flank/flank/pull/876) Added --directories-to-pull validation and avoid making request with empty toolStepResult. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#875](https://github.com/Flank/flank/pull/875) Enhance permission denied exception logs. ([adamfilipow92](https://github.com/adamfilipow92), [pawelpasterz](https://github.com/pawelpasterz))

## v20.06.2
- [#853](https://github.com/Flank/flank/pull/853) Store @Ignore tests in the JUnit XML without sending ignored tests to FTL. ([piotradamczyk5](https://github.com/piotradamczyk5), [adamfilipow92](https://github.com/adamfilipow92))
- [#853](https://github.com/Flank/flank/pull/858) Handle duplicated apk names. ([jan-gogo](https://github.com/jan-gogo))

## v20.06.1
- [#840](https://github.com/Flank/flank/pull/840) Fix parametrized tests. ([jan-gogo](https://github.com/jan-gogo), [adamfilipow92](https://github.com/adamfilipow92), [pawelpasterz](https://github.com/pawelpasterz), [piotradamczyk5](https://github.com/piotradamczyk5))

## v20.06.0

- [#831](https://github.com/Flank/flank/pull/831) Refactor config entities and arguments. ([jan-gogo](https://github.com/jan-gogo))
- [#817](https://github.com/Flank/flank/pull/817) Add AndroidTestContext as base data for dump shards & test execution. ([jan-gogo](https://github.com/jan-gogo))
- [#801](https://github.com/Flank/flank/pull/801) Omit missing app apk if additional-app-test-apks specified. ([jan-gogo](https://github.com/jan-gogo))
- [#784](https://github.com/Flank/flank/pull/784) Add output-style option. ([jan-gogo](https://github.com/jan-gogo))
- [#779](https://github.com/Flank/flank/pull/779) Print retries & display additional info. ([jan-gogo](https://github.com/jan-gogo))
- [#793](https://github.com/Flank/flank/issues/793) Better error message on file not found. ([adamfilipow92](https://github.com/adamfilipow92))
- [#808](https://github.com/Flank/flank/issues/808) Fixed dry run crashes. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#807](https://github.com/Flank/flank/issues/807) Fix Bugsnag being initialized during tests. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#805](https://github.com/Flank/flank/pull/805) Fix overlapping results. ([pawelpasterz](https://github.com/pawelpasterz))
- [#812](https://github.com/Flank/flank/issues/812) Convert bitrise macOS workflow to github action. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#799](https://github.com/Flank/flank/pull/799) Refactor Shared object by splitting it into smaller functions. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#798](https://github.com/Flank/flank/pull/798) Remove failure nodes from tests that passed on retry so that Jenkins JUnit plugin marks them as successful. ([adamfilipow92](https://github.com/adamfilipow92))
- [#822](https://github.com/Flank/flank/pull/822) Allow runtime test discovery when sharding is disabled by not setting test-targets. This unblocks cucumber testing. ([adamfilipow92](https://github.com/adamfilipow92))
- [#819](https://github.com/Flank/flank/pull/819) Display matrix results in a table format. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#825](https://github.com/Flank/flank/pull/825) Automatically convert -1 in maximum-test-shards to the maximum shard amount. ([adamfilipow92](https://github.com/adamfilipow92))
- [#833](https://github.com/Flank/flank/pull/833) More error messages improvements. ([piotradamczyk5](https://github.com/piotradamczyk5))
- [#826](https://github.com/Flank/flank/pull/826) Omit `environment-variables` for robo test executions instead of throwing exception. ([adamfilipow92](https://github.com/adamfilipow92))

## v20.05.2

- [#781](https://github.com/Flank/flank/pull/781) Remove local exists check on cloud results-dir. Fixes crash when results-dir is set by the user. ([adamfilipow92](https://github.com/adamfilipow92))
- [#656](https://github.com/Flank/flank/issues/656) Improve error message reporting. ([adamfilipow92](https://github.com/adamfilipow92))
- [#783](https://github.com/Flank/flank/pull/783) Use legacy results for iOS by default. ([pawelpasterz](https://github.com/pawelpasterz))
- [#794](https://github.com/Flank/flank/pull/794) Enhance `--dump-shards` to dump shards from all test apks ([bootstraponline](https://github.com/bootstraponline), [jan-gogo](https://github.com/jan-gogo))

## v20.05.1

- [#775](https://github.com/Flank/flank/issues/775) Fix exception thrown for null start time. ([pawelpasterz](https://github.com/pawelpasterz))

## v20.05.0

- [#764](https://github.com/Flank/flank/pull/771) Fix crash on parse some control chars. ([adamfilipow92](https://github.com/adamfilipow92))
- [#772](https://github.com/Flank/flank/pull/772) Fail fast when results-dir is incorrect. ([jan-gogo](https://github.com/jan-gogo))
- [#757](https://github.com/Flank/flank/pull/767) Reduce memory usage by using Reader and Writer instead of ByteArrays. ([jan-gogo](https://github.com/jan-gogo))
- [#763](https://github.com/Flank/flank/pull/763) Use "localhost" as default for hostname to fix backward compatibility. ([jan-gogo](https://github.com/jan-gogo))
- [#757](https://github.com/Flank/flank/pull/757) Print version and revision before each command. ([jan-gogo](https://github.com/jan-gogo))
- [#759](https://github.com/Flank/flank/pull/759) Add shard name for uploaded xctestrun files. ([pawelpasterz](https://github.com/pawelpasterz))
- [#755](https://github.com/Flank/flank/pull/755) Remove ascii doc generated section header. ([jan-gogo](https://github.com/jan-gogo))
- [#731](https://github.com/Flank/flank/pull/731) Refactor jUnit HTML report. ([Writhe](https://github.com/Writhe))
- [#754](https://github.com/Flank/flank/pull/754) Sync README.md flank.yml flank.ios.yml. ([jan-gogo](https://github.com/jan-gogo))
- [#746](https://github.com/Flank/flank/pull/746) Ignore apk with filtered out tests instead of failing. ([pawelpasterz](https://github.com/pawelpasterz))
- [#741](https://github.com/Flank/flank/pull/741) Allow APKs with zero tests. ([fondesa](https://github.com/fondesa))
- [#737](https://github.com/Flank/flank/pull/737) Generate ascii doc. ([jan-gogo](https://github.com/jan-gogo))
- [#720](https://github.com/Flank/flank/pull/720) Update group id from `flank` to `com.github.flank` ([bootstraponline](https://github.com/bootstraponline))
- [#714](https://github.com/Flank/flank/pull/714) Add support for num-uniform-shards option. ([jan-gogo](https://github.com/jan-gogo))
- [#712](https://github.com/Flank/flank/pull/712) Add keep file path for ios. ([pawelpasterz](https://github.com/pawelpasterz))
- [#711](https://github.com/Flank/flank/pull/711) Remove hardcoded height. ([pawelpasterz](https://github.com/pawelpasterz))
- [#708](https://github.com/Flank/flank/pull/708) Add ignore failed tests option to Flank. ([pawelpasterz](https://github.com/pawelpasterz))
- [#704](https://github.com/Flank/flank/pull/709) Add robo for robo-directives & robo-script options. ([jan-gogo](https://github.com/jan-gogo))
- [#704](https://github.com/Flank/flank/pull/704) Fix shards calculation when there are ignored tests and shardTime is -1. ([jan-gogo](https://github.com/jan-gogo))
- [#692](https://github.com/Flank/flank/pull/698) Add support for other-files option. ([jan-gogo](https://github.com/jan-gogo))
- [#695](https://github.com/Flank/flank/pull/695) Add support for additional-apks option. ([jan-gogo](https://github.com/jan-gogo))
- [#683](https://github.com/Flank/flank/pull/683) Print web link. ([pawelpasterz](https://github.com/pawelpasterz))
- [#692](https://github.com/Flank/flank/pull/692) Add support for network-profiles list command & --network-profile option. ([jan-gogo](https://github.com/jan-gogo))
- [#689](https://github.com/Flank/flank/pull/689) Add support for client-details option. ([jan-gogo](https://github.com/jan-gogo))
- [#687](https://github.com/Flank/flank/pull/687) Debug message printed after every command. ([pawelpasterz](https://github.com/pawelpasterz))
- [#684](https://github.com/Flank/flank/pull/684) Add overhead time to junit test case report. ([jan-gogo](https://github.com/jan-gogo))
- [#666](https://github.com/Flank/flank/pull/666) Use API instead of XML for result parsing for android. ([jan-gogo](https://github.com/jan-gogo))
- [#678](https://github.com/Flank/flank/pull/678) Skip Bugsnag initialization if user disabled gcloud analytics. ([pawelpasterz](https://github.com/pawelpasterz))
- [#672](https://github.com/Flank/flank/pull/672) Flank timeout feature. ([pawelpasterz](https://github.com/pawelpasterz))
- [#657](https://github.com/Flank/flank/pull/657) Fix execution hangs. ([pawelpasterz](https://github.com/pawelpasterz))
- [#654](https://github.com/Flank/flank/pull/654) Fix test filters when using both notPackage and notClass. ([jan-gogo](https://github.com/jan-gogo))
- [#648](https://github.com/Flank/flank/pull/648) Include @Ignore JUnit tests in JUnit XML. ([pawelpasterz](https://github.com/pawelpasterz))
- [#646](https://github.com/Flank/flank/pull/646) Adopt kotlin-logging as a logging framework. ([jan-gogo](https://github.com/jan-gogo))
- [#644](https://github.com/Flank/flank/pull/644) Use high performance options by default. Video, login, and perf metrics are now disabled by default. ([pawelpasterz](https://github.com/pawelpasterz))
- [#643](https://github.com/Flank/flank/pull/643) Add --dry option to android run & ios run. ([jan-gogo](https://github.com/jan-gogo))
- [#642](https://github.com/Flank/flank/pull/642) Flank doctor should exit 1 on validation issues. ([pawelpasterz](https://github.com/pawelpasterz))
- [#641](https://github.com/Flank/flank/pull/641) Move all deps to Deps.kt ([doodla](https://github.com/doodla))
- [#640](https://github.com/Flank/flank/pull/640) Update gradle to 6.2.1 and JaCoCo to 0.8.5. ([doodla](https://github.com/doodla))
- [#639](https://github.com/Flank/flank/pull/639) Cache all uploads and downloads to GCS. ([Kurt-Bonatz](https://github.com/Kurt-Bonatz))
- [#635](https://github.com/Flank/flank/pull/635) Default to 2m time estimate for unknown tests when sharding. ([RainNapper](https://github.com/RainNapper))
- [#621](https://github.com/Flank/flank/pull/621) Adopt server side sharding. Max shard count is 50. ([bootstraponline](https://github.com/bootstraponline))

## v8.1.0

- [#612](https://github.com/Flank/flank/pull/612) Print HtmlErrorReport location. ([bootstraponline](https://github.com/bootstraponline))
- [#615](https://github.com/Flank/flank/pull/615) Add `--keep-file-path` for Android when downloading assets from Google Cloud Storage. ([tahirhajizada](https://github.com/tahirhajizada))

## v8.0.1

- [#608](https://github.com/Flank/flank/pull/608) Use MatrixRollupOutcome to set exit code value. ([bootstraponline](https://github.com/bootstraponline))

## v8.0.0

- [#595](https://github.com/Flank/flank/pull/595) Rename `flaky-test-attempts` to `num-flaky-test-attempts`. Rename `repeat-tests` to `num-test-runs`. ([bootstraponline](https://github.com/bootstraponline))
- [#605](https://github.com/Flank/flank/pull/605) Improve exit code logging. Use matrix outcome to set exit code. ([bootstraponline](https://github.com/bootstraponline))
- [#597](https://github.com/Flank/flank/pull/597) Support parsing testLabExecutionId. ([yogurtearl](https://github.com/yogurtearl))
- [#599](https://github.com/Flank/flank/pull/599) Disable FAIL_ON_UNKNOWN_PROPERTIES for forward compatibility. ([narenkmanoharan](https://github.com/narenkmanoharan))

## v7.0.2

- [#589](https://github.com/Flank/flank/pull/589) Fix java.lang.NumberFormatException: empty String. ([vfadc](https://github.com/vfadc))
- [#587](https://github.com/Flank/flank/pull/587) Optimize polling. ([bootstraponline](https://github.com/bootstraponline))

## v7.0.1

- [#586](https://github.com/Flank/flank/pull/586) Poll devices in parallel. Fixes performance regression in Flank v7. ([bootstraponline](https://github.com/bootstraponline))

## v7.0.0

- [#574](https://github.com/Flank/flank/pull/574) Improve test shard error reporting. Update device catalog to use projectId. ([bootstraponline](https://github.com/bootstraponline))
- [#582](https://github.com/Flank/flank/pull/582) Fix iOS exit code when using flaky-test-attempts. Don't print environment-variables to stdout for security. ([bootstraponline](https://github.com/bootstraponline))
- [#584](https://github.com/Flank/flank/pull/584) Poll all test executions instead of only the first per matrix. ([bootstraponline](https://github.com/bootstraponline))
- [#585](https://github.com/Flank/flank/pull/585) Fix bug in smart flank when sharding tests that run in 0 seconds. ([bootstraponline](https://github.com/bootstraponline))

## v6.2.3

- [#567](https://github.com/Flank/flank/pull/567) Fix `--app` & `--test` on Android CLI. ([bootstraponline](https://github.com/bootstraponline))
- [#571](https://github.com/Flank/flank/pull/571) Add `flank ios run --dump-shards` and `flank android run --dump-shards` for debugging ([bootstraponline](https://github.com/bootstraponline))
- [#572](https://github.com/Flank/flank/pull/572) Fix exit code reporting when using `flaky-test-attempts` ([bootstraponline](https://github.com/bootstraponline))

## v6.2.2

- [#566](https://github.com/Flank/flank/pull/566) Fix `--test` & `--xctestrun-file` on iOS CLI. ([bootstraponline](https://github.com/bootstraponline))

## v6.2.1

- [#563](https://github.com/Flank/flank/pull/563) Fix CLI support for iOS. ([bootstraponline](https://github.com/bootstraponline))

## v6.2.0

- [#560](https://github.com/Flank/flank/pull/560) Add `--test-runner-class` support for Android. ([jschear](https://github.com/jschear))

## v6.1.0

- [#542](https://github.com/Flank/flank/pull/542) Add `additional-app-test-apks` to include multiple app/test apk pairs in a single run. ([bootstraponline](https://github.com/bootstraponline))

## v6.0.1

- [#550](https://github.com/Flank/flank/pull/550) Update dex-test-parser to fix Inherited crash. ([bootstraponline](https://github.com/bootstraponline))

## v6.0.0
- [#541](https://github.com/Flank/flank/pull/541) Rename `--test-shards` CLI flag to `--max-test-shards`. Add `--smart-flank-gcs-path` CLI flag. ([bootstraponline](https://github.com/bootstraponline))
- [#544](https://github.com/Flank/flank/pull/544) Fix empty test targets crash. ([bootstraponline](https://github.com/bootstraponline))
- [#548](https://github.com/Flank/flank/pull/548) Fix flank cancel. Print os name when sharding iOS tests. ([bootstraponline](https://github.com/bootstraponline))
- [#549](https://github.com/Flank/flank/pull/549) Update to llvm 8.0.0 and Swift 5.0.1. ([bootstraponline](https://github.com/bootstraponline))

## v5.1.0
- [#537](https://github.com/Flank/flank/pull/537) Add `smart-flank-disable-upload` yml option to prevent new results from overriding previous results. ([elihart](https://github.com/elihart))

## v5.0.2

- [#538](https://github.com/Flank/flank/pull/538) Update `dextestparser`. Fixes APK parsing crash. ([bootstraponline](https://github.com/bootstraponline))
- [#536](https://github.com/Flank/flank/pull/536) Always calculate exit code from matrix status instead of JUnit XML. ([bootstraponline](https://github.com/bootstraponline))

## v5.0.1

- [#534](https://github.com/Flank/flank/pull/534) Fix updateMatrixFile not saving `matrix_ids.json`. ([bootstraponline](https://github.com/bootstraponline))

## v5.0.0

- [#506](https://github.com/Flank/flank/pull/506) Add bugsnag reporting to detect Flank crashes. ([bootstraponline](https://github.com/bootstraponline))
- [#507](https://github.com/Flank/flank/pull/507) Improve error message when credentials fail to load, folder doesn't exist, and on bucket creation failure. Properly pass through user credential when checking the storage bucket. ([bootstraponline](https://github.com/bootstraponline))
- [#514](https://github.com/Flank/flank/pull/514) Rename `testShards` to `maxTestShards` ([miguelslemos](https://github.com/miguelslemos))
- [#518](https://github.com/Flank/flank/pull/518) Add deprecation warnings when old key names are used. `flank android doctor --fix` will auto fix the YAML file. ([bootstraponline](https://github.com/bootstraponline))
- [#519](https://github.com/Flank/flank/pull/519)  Rename `maxTestShards` to `max-test-shards`, `shardTime` to `shard-time`, `repeatTests` to `repeat-tests`, `smartFlankGcsPath` to `smart-flank-gcs-path`, `disableSharding` to `disable-sharding`. Moved `project` from `gcloud` to `flank` ([bootstraponline](https://github.com/bootstraponline))
- [#523](https://github.com/Flank/flank/pull/523) Add `--local-result-dir` to make it easy to find the test result at a fixed path. ([bootstraponline](https://github.com/bootstraponline))
- [#524](https://github.com/Flank/flank/pull/524) Fix iOS test sharding when there's a space in the path ([bootstraponline](https://github.com/bootstraponline))
- [#522](https://github.com/Flank/flank/pull/522) Correctly report test results and exit code when using `flaky-test-attempts` ([bootstraponline](https://github.com/bootstraponline))
- [#530](https://github.com/Flank/flank/pull/530) Save files downloaded with `files-to-download` to device root folder. Fix web links in HTML report. ([bootstraponline](https://github.com/bootstraponline))
- [#533](https://github.com/Flank/flank/pull/533) Update matrix_ids.json on every matrix change. Useful for debugging runs that crashed. ([jschear](https://github.com/jschear))

## v4.4.0

- [#505](https://github.com/Flank/flank/pull/505) Fix `flank auth login` by using `google-auth-library-java`. ([bootstraponline](https://github.com/bootstraponline))
- [#501](https://github.com/Flank/flank/pull/501) Fix nullability check in SavedMatrix. ([bootstraponline](https://github.com/bootstraponline))
- [#493](https://github.com/Flank/flank/pull/493) Fix getDefaultBucket timeout. ([bootstraponline](https://github.com/bootstraponline))

## v4.3.1

- [#491](https://github.com/Flank/flank/pull/491) Fix `shardTime` when `testShards` is `-1` ([Macarse](https://github.com/Macarse))

## v4.3.0

- [#436](https://github.com/Flank/flank/pull/436) Print how accurate test times are when using smart flank ([Macarse](https://github.com/Macarse))
- [#462](https://github.com/Flank/flank/pull/462) Always find all iOS tests when sharding. ([bootstraponline](https://github.com/bootstraponline))
- [#471](https://github.com/Flank/flank/pull/471) Add dynamic bucket counts for Smart Flank via `shardTime` ([Macarse](https://github.com/Macarse))
- [#473](https://github.com/Flank/flank/pull/473) Add `disableSharding` flag ([Macarse](https://github.com/Macarse))
- [#476](https://github.com/Flank/flank/pull/476) Retry matrix creation when FTL API errors. ([bootstraponline](https://github.com/bootstraponline))
- [#477](https://github.com/Flank/flank/pull/477) Fix large number merging on iOS ([bootstraponline](https://github.com/bootstraponline))
- [#478](https://github.com/Flank/flank/pull/478) Add iOS regular expression filtering for `test-targets` ([Macarse](https://github.com/Macarse))
- [#479](https://github.com/Flank/flank/pull/479) Improve invalid iOS regex error message ([bootstraponline](https://github.com/bootstraponline))
- [#481](https://github.com/Flank/flank/pull/481) Add iOS support for locale and orientation ([Macarse](https://github.com/Macarse))
- [#482](https://github.com/Flank/flank/pull/482) Document Android code coverage with orchestrator on FTL ([bootstraponline](https://github.com/bootstraponline))
- [#485](https://github.com/Flank/flank/pull/485) Replace : in folder name to improve Windows support. ([Sunil-plsr](https://github.com/Sunil-plsr))
- [#487](https://github.com/Flank/flank/pull/487) Document all Flank YAML properties in README ([bootstraponline](https://github.com/bootstraponline))

## v4.2.0

- Fix create Gcs bucket [#444](https://github.com/Flank/flank/pull/444)
- Add `files-to-download` to Android and iOS. Specify a list of regular expressions to download files from the Google Cloud Storage bucket. [#441](https://github.com/Flank/flank/pull/441)
- Add `flank auth login` to authorize with a user account instead of a service account. [#446](https://github.com/Flank/flank/pull/436)
- Add `flaky-test-attempts` support on Android and iOS which automatically retries failed tests. [#454](https://github.com/Flank/flank/pull/454)

## v4.1.1

- Add support for parameterized iOS tests when shard count is 1. [#435](https://github.com/Flank/flank/pull/435)
- Add support for `SkipTestIdentifiers` when parsing iOS `.xctestrun` files. [#435](https://github.com/Flank/flank/pull/435)

## v4.1.0

- `app`, `test`, and `xctestrun-file` now support `~`, environment variables, and globs (`*`, `**`) when resolving paths. [#386](https://github.com/Flank/flank/pull/386)
- Add CLI support for `flank android run` and `flank ios run`
- Add experimental `smartFlankGcsPath` to shard iOS and Android tests by time using historical run data. The amount of shards used is set by `testShards`. [#385](https://github.com/Flank/flank/pull/385)
- Fix parsing empty testcase [#402](https://github.com/Flank/flank/pull/402)
- Add progress bar when uploading files. [#403](https://github.com/Flank/flank/pull/403)
- iOS iPhone 8 default device has been updated from 11.2 to 12.0.

## v4.0.0

- Add `flank cancel` command. See `flank cancel --help`
- Add `flank refresh` command. See `flank refresh --help`
- Automatically detect projectId from service account credential
- Always generate aggregated JUnit XML reports
- Update HTML report to use create-react-app v2.1.0
- Add support for Xcode 10.1
- Improve test parsing to detect Swift tests that throw errors
- Update Flank exit codes, see readme for details.
- Link to matrix root when testing on multiple devices
