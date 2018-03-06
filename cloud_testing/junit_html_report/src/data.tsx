import {
  IGroup
} from 'office-ui-fabric-react/lib/components/GroupedList/index';

export interface IItem {
  /**
   * Unique identifier for the item.
   */
  key: string;
  /**
   * Display name for the item.
   */
  name: string;
}

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
