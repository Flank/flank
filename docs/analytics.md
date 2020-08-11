# Flank Analytics

Flank makes use of various analytics, below contains a list of the analytics and how to enable/disable them from being sent to FTL.

## Flank

 - BugSnag
   - **Disable** - ADD "DISABLE" to the analytics_uuid for example: echo "DISABLED" > ~/.gsutil/analytics-uuid
   - **Enable** - Remove "DISABLE" from the analytics_uuid file for example: echo "$(grep -v "DISABLED" ~/.gsutil/analytics-uuid)" > ~/.gsutil/analytics-uuid
- 
