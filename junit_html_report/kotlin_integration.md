`bitrise/inline.html`

```javascript
var o=[{key:"group-0",name:"AssigneeListPageTest#displaysStudentItems",startIndex:0,count:1},{key:"group-1",name:"SpeedGraderCommentsPageTest#displaysAuthorName",startIndex:1,count:1}],r=[{key:"item-0",name:'android.support.test.espresso.NoMatchingViewException: No views in hierarchy found matching: (with text: is "Everyone" and has sibling: with id: com.instructure.teacher:id/assigneeSubtitleView)'},{key:"item-1",name:"java.lang.RuntimeException: Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds. If you specified a non default root matcher, it may be picking a root that never takes focus."}]
```

group:
`=[{key:"group-0",...]`

item:
`=[{key:"item-0",...]`

Regex search & replace with serialized JSON

Group format:
```javascript

export const dataGroups: IGroup[] = [
{
  key: 'group-0',
  name: 'AssigneeListPageTest#displaysStudentItems',
  startIndex: 0,
  count: 1,
},
{
  key: 'group-1',
  name: 'SpeedGraderCommentsPageTest#displaysAuthorName',
  startIndex: 1,
  count: 1,
}];
```

Item format:
```javascript
export const dataItems: IItem[] = [
  {
    key: 'item-0',
    name: 'android.support.test.espresso.NoMatchingViewException: No views in hierarchy found matching: (with text: is "Everyone" and has sibling: with id: com.instructure.teacher:id/assigneeSubtitleView)'
  },
  {
    key: 'item-1',
    name: 'java.lang.RuntimeException: Waited for the root of the view hierarchy to have window focus and not request layout for 10 seconds. If you specified a non default root matcher, it may be picking a root that never takes focus.'
  }
];
```
