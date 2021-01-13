# Analytics

To optimize the user experience and make a proper decision about features supported by Flank there is a need to collect
data about usage of Flank.

## Tools choosing

It was suggested to use Firebase/Google analytics because Flank is ultimately a Firebase integration (for test lab).
Due to the lack of API and no easy method to use for server/desktop/native apps, it was decided to research for
alternatives.
After some research 2 tools were find as interesting solution for Flank use case:\
- [MixPanel Analytics](https://mixpanel.com/) with features:
    - [Java SDK](https://developer.mixpanel.com/docs/java),
    - dashboard
    - many tools to analyze the usage of Flank
    - free plan could be used by [100k users/month](https://mixpanel.com/pricing/)
- [Segment by Twilio](https://segment.com/pricing/?ref=nav) with features:
    - dashboard
    - [Java SDK](https://segment.com/docs/connections/sources/catalog/libraries/server/java/)
    - dashboard
    - free Plan Includes [1,000 visitors/mo](https://segment.com/pricing/?ref=nav)

Other evaluated tools do not have good API, free plan, and/or dashboard, so as a team we decided to make a proof of concept
for MixPanel and Segment

## POC Mixpanel

### Scenario
Send configuration provided by a user to MixPanel

**TODO**

## POC Segment

### Scenario
Send configuration provided by a user to Segment.

### Description
During making POC Segment appears to be a not a standalone solution. It is just a tunnel to map and send data to other destination(s).
A destination for this proof of concept was Google Analytics.

### Usage in Flank
Sending events to Segment is super easy from developer perspective
1. Add dependency
   ```kotlin
    implementation("com.segment.analytics.java:analytics:2.1.1")
   ```
1. Create instance of Analytics
   ```kotlin
        private val analytics = Analytics.builder("WRITE KEY") // could be found on Segment source configuration
            .build()
   ```
1. Enqueue event with map properties
   ```kotlin
        analytics.enqueue(
            TrackMessage.builder("EVENT NAME") // event name
                .userId("USER ID") // user id for indentification purpose
                .properties(configurationProperties) // properties Map<String, Any>
        )
   ```
1. Flush events to make sure that they will be sent
   ```kotlin
   analytics.flush()
   ```

### Results
The destination configuration was very complicated. It was not well described on Segment site.
Segment needs Google Tracking ID for sending events to them, however to have it, [you must
use an old version of Google Analytics(Universal) which is not so easy to find, new Google Analytics does not use this
property](https://support.google.com/analytics/answer/7372977?hl=en) and Segment does not work with the latest version of
Google Analytics.
After configuring Segment and Google Analytics (Universal old version) events are received by the Google tool. However, data are
not readable and could not be analyzed. This tool is mostly used for websites and does not allow querying properties or
custom events. Maybe this is the fault of Segment integration or Flank specific use case, but this tool does not work properly with Google Analytics.
Other destinations are not worth to be a consideration, because it is better to use standalone versions of them.


### Pros
- easy to use

### Cons
- it is not standalone (needs additional tools)
- configuration of destinations is not easy, there is not enough information for it
- implementation force user to use an older version of the end solution (Google Analytics)

## Decision MixPanel vs Segment

**TODO** 
