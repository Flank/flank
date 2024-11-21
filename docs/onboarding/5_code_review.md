## Code review
Do your best and do not forget about some good practices, 
formalized and described below. 

### Be aware
- Good code review requires a good understood of a problem.
- Code review is an important part of the development process and delivery. 

### Do code review by steps
1. Read the issue related to the pull request.
1. Read the description of the pull request.
1. Make sure the description is clear for you. 
1. Make sure the description is sufficient for you.
1. Read the committed changes 
1. Make sure you are ok with implementation.
1. Make sure the result of what changed is clear for you.
1. Make sure the pull request really solves the related issue in the desired way.
1. Make sure the testing scenario is clear for you.
1. Do the tests step by step and collect the output.
1. Make sure the result of tests is equal to expected
1. Attach report about test results under pull request.

### Notify about fail
If any step above will fail for you, try to reproduce it for sure and
notify about a faced problem using pull request comment.
Additionally, you may attach any of:
- GitHub `commitable` suggestion if possible, and you already know it.
- Description of what is unclear for you.
- Description of what should be changed or what is missing.
- Any additional information important for final quality.

### Some tips
- Make sure you clearly understand what you are reviewing.
- Don't be afraid of paying attention to details if feel they are important.
- It's always a good idea to open IDE, try to identify the root, and trace the implementation if the pull request is not trivial.
- Ask if you are not sure, suggest if you are sure.
