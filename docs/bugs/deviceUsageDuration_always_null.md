# deviceUsageDuration always null

## deviceUsageDuration description on docs

> How much the device resource is used to perform the test.
>  
> This is the device usage used for billing purpose, which is different from the run_duration,
> for example, infrastructure failure won't be charged for device usage.
>  
> PRECONDITION_FAILED will be returned if one attempts to set a device_usage on a step which
> already has this field set.
>  
> - In response: present if previously set. - In create request: optional - In update request:
> optional
> @return value or {@code null} for none

## Problem description

Problem found on pull request: [Flank needs to respect the timeout value as that's a cap for billing purposes. #865](https://github.com/Flank/flank/pull/865)

`deviceUsageDuration` still is null even if we testing on blaze plan with free quota spent

## Next steps

In future we should check problem status and if problem is fixed on testlab we should implement it on Flank
